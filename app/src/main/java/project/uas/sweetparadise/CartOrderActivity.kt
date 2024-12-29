package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Cart
import java.text.NumberFormat
import java.util.Locale

class CartOrderActivity : AppCompatActivity() {

    private lateinit var adapter: adapterCartOrder
    private var cartItems: MutableList<Cart> = mutableListOf()
    private var selectedPaymentMethod: String? = null
    private var userId: Int = -1 // Tambahkan variabel global untuk userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_order)

        val recyclerView = findViewById<RecyclerView>(R.id.rvOrderMenu)
        val db = AppDatabase.getDatabase(this)

        adapter = adapterCartOrder(cartItems, db)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPreferences = getSharedPreferences("SweetParadisePrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("CURRENT_USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val _buttonBack = findViewById<ImageView>(R.id.back)
        var _point = findViewById<TextView>(R.id.textView3)
        val checkboxPoint = findViewById<CheckBox>(R.id.checkbox)
        val _btnCashier = findViewById<FrameLayout>(R.id.btnCashier)
        val _btnOther = findViewById<FrameLayout>(R.id.btnOther)
        var _totalOrder = findViewById<TextView>(R.id.totalOrder)
        val _btnOrder = findViewById<Button>(R.id.btnOrder)

        _buttonBack.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        // Listener untuk memilih metode pembayaran
        _btnCashier.setOnClickListener {
            selectedPaymentMethod = "Cashier"
            _btnCashier.isSelected = true
            _btnOther.isSelected = false
            highlightSelectedPaymentMethod(_btnCashier, _btnOther)
        }
        _btnOther.setOnClickListener {
            selectedPaymentMethod = "Other"
            _btnCashier.isSelected = false
            _btnOther.isSelected = true
            highlightSelectedPaymentMethod(_btnOther, _btnCashier)
        }

        _btnOrder.setOnClickListener {
            if (selectedPaymentMethod == null) {
                showToast("Please select a payment method.")
            } else {
                when (selectedPaymentMethod) {
                    "Cashier" -> {
                        val intent = Intent(this, BillAfterActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    }
                    "Other" -> {
                        showToast("Other payment method is not implemented yet.")
                    }
                }
            }
        }

        // Load data dari database
        if (userId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val carts = db.cartDao().getUserCart(userId)
                    val user = db.userDao().getUserById(userId)

                    withContext(Dispatchers.Main) {
                        cartItems.clear()
                        cartItems.addAll(carts)
                        adapter.notifyDataSetChanged()

                        val userPoints = user?.point ?: 0
                        _point.text = formatToRupiah(userPoints)
                        updateTotalBasedOnPoints(checkboxPoint, _totalOrder, userPoints)
                    }
                } catch (e: Exception) {
                    Log.e("CartOrderActivity", "Error loading cart: ${e.message}", e)
                }
            }
        }

        //utk checkbox point
        checkboxPoint.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                val userPoints = db.userDao().getUserById(userId)?.point ?: 0
                withContext(Dispatchers.Main) {
                    updateTotalBasedOnPoints(checkboxPoint, _totalOrder, userPoints)
                }
            }
        }

        //utk button tambah dan kurang
        adapter.setOnItemClickCallBack(object : adapterCartOrder.OnItemClickCallback {
            override fun minData(dtOrder: Cart, quantity: Int) {
                updateCartQuantity(db, dtOrder, quantity, checkboxPoint, _totalOrder, userId)
            }

            override fun plusData(dtOrder: Cart, quantity: Int) {
                updateCartQuantity(db, dtOrder, quantity, checkboxPoint, _totalOrder, userId)
            }
        })

    }

    private fun updateCartQuantity(
        db: AppDatabase,
        dtOrder: Cart,
        quantity: Int,
        checkboxPoint: CheckBox,
        _totalOrder: TextView,
        userId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // Update database
            db.cartDao().updateCartQuantity(dtOrder.id, quantity)
            val userPoints = db.userDao().getUserById(userId)?.point ?: 0
            withContext(Dispatchers.Main) {
                dtOrder.quantity = quantity
                adapter.notifyItemChanged(cartItems.indexOf(dtOrder))
                updateTotalBasedOnPoints(checkboxPoint, _totalOrder, userPoints)
            }
        }
    }

    private fun calculateTotalOrder(): Int {
        var total = 0
        for (item in cartItems) {
            total += item.price * item.quantity
        }
        return total
    }

    private fun formatToRupiah(amount: Int): String {
        val format = NumberFormat.getInstance(Locale("id", "ID")) // Format Rupiah
        return format.format(amount)
    }

    // Fungsi untuk hitung total jika menggunakan poin
    private fun updateTotalBasedOnPoints(checkboxPoint: CheckBox, _totalOrder: TextView, userPoints: Int) {
        if (checkboxPoint.isChecked) {
            val totalAfterDiscount = calculateTotalOrder() - userPoints
            _totalOrder.text = "Rp ${formatToRupiah(totalAfterDiscount)}"
        } else {
            _totalOrder.text = "Rp ${formatToRupiah(calculateTotalOrder())}"
        }
    }

    // Fungsi untuk memberikan efek visual pada metode pembayaran yang dipilih
    private fun highlightSelectedPaymentMethod(selected: FrameLayout, other: FrameLayout) {
        selected.setBackgroundResource(R.drawable.payment_selected_border)
        other.setBackgroundResource(R.drawable.payment_border)
    }

    // Fungsi untuk menampilkan toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
