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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Cart
import project.uas.sweetparadise.database.Notification
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartOrderActivity : AppCompatActivity() {

    private lateinit var adapter: adapterCartOrder
    private var cartItems: MutableList<Cart> = mutableListOf()
    private var selectedPaymentMethod: String? = null
    private var userId: Int = -1
    private lateinit var launcher: ActivityResultLauncher<Intent>

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
        val _btnCashier = findViewById<FrameLayout>(R.id.btnCashier)
        val _btnOther = findViewById<FrameLayout>(R.id.btnOther)
        val _totalOrder = findViewById<TextView>(R.id.totalOrder)
        val _btnOrder = findViewById<Button>(R.id.btnOrder)
        var _point = findViewById<TextView>(R.id.textView3)
        val checkboxPoint = findViewById<CheckBox>(R.id.checkbox)


        _buttonBack.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

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
                        _btnOther.setOnClickListener {
                            startPayment()
                        }
                    }
                }
            }
        }

        initLauncher()
        loadCartData(db)
        initUiKitApi()

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

    // Fungsi untuk hitung total jika menggunakan poin
    private fun updateTotalBasedOnPoints(
        checkboxPoint: CheckBox,
        _totalOrder: TextView,
        userPoints: Int
    ) {
        val totalOrder = calculateTotalOrder()
        val totalAfterDiscount = if (checkboxPoint.isChecked) {
            totalOrder - userPoints
        } else {
            totalOrder
        }

        _totalOrder.text = "Rp ${formatToRupiah(totalAfterDiscount)}"
    }

    private fun formatToRupiah(value: Int): String {
        val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
        return formatter.format(value).replace("Rp", "Rp ").replace(",00", "")
    }

    private fun initLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val transactionResult =
                        result.data?.getParcelableExtra<com.midtrans.sdk.uikit.api.model.TransactionResult>(
                            UiKitConstants.KEY_TRANSACTION_RESULT
                        )
                    if (transactionResult != null) {
                        when (transactionResult.status) {
                            UiKitConstants.STATUS_SUCCESS -> {
                                Toast.makeText(
                                    this,
                                    "Transaction Successful: ${transactionResult.transactionId}",
                                    Toast.LENGTH_LONG
                                ).show()

                                val totalOrder = calculateTotalOrder() // Total transaksi
                                val pointsEarned = calculatePointsEarned(totalOrder) // Hitung poin baru

                                // Perbarui poin pengguna di tabel users
                                updateUserPoints(userId, pointsEarned, false)

                                // Tambahkan notifikasi
                                addNotification(userId, pointsEarned)

                                // Hapus isi keranjang
                                clearCart()

                                // Arahkan ke HomeActivity
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            UiKitConstants.STATUS_PENDING -> {
                                Toast.makeText(
                                    this,
                                    "Transaction Pending: ${transactionResult.transactionId}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            UiKitConstants.STATUS_FAILED -> {
                                Toast.makeText(
                                    this,
                                    "Transaction Failed: ${transactionResult.transactionId}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "No transaction result found", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun initUiKitApi() {
        UiKitApi.Builder()
            .withMerchantClientKey("SB-Mid-client-SmiD5AWIUxMTweiU") // Client Key Midtrans
            .withContext(applicationContext)
            .withMerchantUrl("https://merchant-server-drab.vercel.app/api/charge/") // URL Server Backend Integration
            .withColorTheme(CustomColorTheme("#652B27", "#4B1D18", "#FFE51255"))
            .enableLog(true)
            .build()
    }

    private fun loadCartData(db: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val carts = db.cartDao().getUserCart(userId)
                withContext(Dispatchers.Main) {
                    cartItems.clear()
                    cartItems.addAll(carts)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CartOrderActivity,
                        "Failed to load cart data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startPayment() {
        val gson = Gson()
        val db = AppDatabase.getDatabase(this)

        // Ambil detail keranjang dan menu
        CoroutineScope(Dispatchers.IO).launch {
            val cartItems = db.cartDao().getUserCart(userId)
            val itemDetails = cartItems.map { cartItem ->
                val menu = db.menuDao().getMenuById(cartItem.menuId)
                mapOf(
                    "id" to cartItem.menuId.toString(),
                    "price" to cartItem.price,
                    "quantity" to cartItem.quantity,
                    "name" to (menu?.name ?: "Unknown Item"),
                    "notes" to (cartItem.menuNote ?: "")
                )
            }

            val totalAmount = itemDetails.sumOf {
                it["price"].toString().toInt() * it["quantity"].toString().toInt()
            }

            // Payload untuk Midtrans
            val payload = mapOf(
                "transaction_details" to mapOf(
                    "order_id" to "order-${System.currentTimeMillis()}",
                    "gross_amount" to totalAmount
                ),
                "item_details" to itemDetails,
                "customer_details" to mapOf(
                    "first_name" to "Default",
                    "last_name" to "Customer",
                    "email" to "default.customer@example.com",
                    "phone" to "08123456789"
                )
            )

            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                gson.toJson(payload)
            )

            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://merchant-server-drab.vercel.app/api/charge/") // URL Server Backend Integration
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(
                            this@CartOrderActivity,
                            "Failed to connect to server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val snapResponse = gson.fromJson(responseBody, SnapResponse::class.java)
                        runOnUiThread {
                            if (!snapResponse.token.isNullOrEmpty()) {
                                UiKitApi.getDefaultInstance().startPaymentUiFlow(
                                    this@CartOrderActivity,
                                    launcher,
                                    snapResponse.token
                                )
                            } else {
                                Toast.makeText(
                                    this@CartOrderActivity,
                                    "Invalid Snap Token",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@CartOrderActivity,
                                "Error: ${response.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
        }
    }

    data class SnapResponse(
        val token: String,
        val redirect_url: String
    )

    private fun clearCart() {
        val db = AppDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.cartDao().deleteCartItemsAfterPaymentSuccess(userId)
                withContext(Dispatchers.Main) {
                    cartItems.clear()
                    adapter.notifyDataSetChanged()
                    Toast.makeText(
                        this@CartOrderActivity,
                        "Cart cleared successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CartOrderActivity,
                        "Failed to clear cart.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateUserPoints(userId: Int, additionalPoints: Int, isPointsUsed: Boolean) {
        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isPointsUsed) {
                    db.userDao().updateUserPoints(userId, 0)
                    withContext(Dispatchers.Main) {
                        Log.d("CartOrderActivity", "User points reset to 0 after usage.")
                    }
                } else {
                    val currentPoints = db.userDao().getUserPoints(userId) ?: 0
                    val updatedPoints = currentPoints + additionalPoints

                    // Perbarui poin di database
                    db.userDao().updateUserPoints(userId, updatedPoints)

                    // Tambahkan notifikasi ke database
                    addNotification(userId, additionalPoints)

                    withContext(Dispatchers.Main) {
                        Log.d("CartOrderActivity", "User points updated successfully. New points: $updatedPoints")
                    }
                }
            } catch (e: Exception) {
                Log.e("CartOrderActivity", "Error updating user points: ${e.message}")
            }
        }
    }

    private fun addNotification(userId: Int, pointsEarned: Int) {
        val db = AppDatabase.getDatabase(this)
        val message = "You earned $pointsEarned points from your last transaction!"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.notificationDao().insertNotification(
                    Notification(
                        userId = userId,
                        message = message,
                        date = getCurrentDate()
                    )
                )
                Log.d("CartOrderActivity", "Notification added for user ID: $userId")
            } catch (e: Exception) {
                Log.e("CartOrderActivity", "Error adding notification: ${e.message}")
            }
        }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun calculatePointsEarned(totalOrder: Int): Int {
        return (totalOrder * 0.03).toInt()
    }

    // utk toggle selected payment
    private fun highlightSelectedPaymentMethod(selected: FrameLayout, other: FrameLayout) {
        selected.setBackgroundResource(R.drawable.payment_selected_border)
        other.setBackgroundResource(R.drawable.payment_border)
    }

    // Fungsi untuk menampilkan toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
