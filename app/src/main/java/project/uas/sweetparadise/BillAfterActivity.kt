package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Bill
import project.uas.sweetparadise.database.Cart
import project.uas.sweetparadise.helper.DateHelper.getCurrentDate
import project.uas.sweetparadise.helper.TimeHelper.getCurrentTime
import java.text.NumberFormat
import java.util.Locale

class BillAfterActivity : AppCompatActivity() {

    private lateinit var adapter: adapterBill
    private var billItems: MutableList<Cart> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_after_order)

        val recyclerView = findViewById<RecyclerView>(R.id.rvOrderBill)
        val db = AppDatabase.getDatabase(this)

        adapter = adapterBill(billItems, db)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userId = intent.getIntExtra("USER_ID", -1)
        Log.d(
            "BillAfterActivity",
            "Received USER_ID: $userId"
        )  // Log untuk memastikan userId diterima dengan benar
        if (userId != -1) {
            Log.d("ReceivedUserId", "User ID received: $userId")
        } else {
            Log.e("ReceivedUserId", "No User ID found.")
        }

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _username = findViewById<TextView>(R.id.username)
        val _locationName = findViewById<TextView>(R.id.locationName)
        val _date = findViewById<TextView>(R.id.date)
        val _time = findViewById<TextView>(R.id.time)
        val _priceAmount = findViewById<TextView>(R.id.priceAmount)
        val _taxAmount = findViewById<TextView>(R.id.taxAmount)
        val _otherAmount = findViewById<TextView>(R.id.otherAmount)
        val _totalAmount = findViewById<TextView>(R.id.totalAmount)
        val _btnGenerateQR = findViewById<TextView>(R.id.btnGenerateQR)

        _buttonBack.setOnClickListener {
            val intent = Intent(this, CartOrderActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        //ke QrActivity
        _btnGenerateQR.setOnClickListener {
            if (userId != -1) {
                val intent = Intent(this, QrActivity::class.java)
                intent.putExtra("USER_ID", userId) // Kirim userId ke QrActivity
                startActivity(intent)
            } else {
                Log.e("BillAfterActivity", "Invalid User ID. Cannot generate QR.")
            }
        }

        _date.text = getCurrentDate()
        _time.text = getCurrentTime()

        // Mengambil data dari cart ke billItems
        if (userId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val carts = db.cartDao().getUserCart(userId)
                    val user = db.userDao().getUserById(userId)

                    // Process cart data and calculate amounts
                    val priceAmount = carts.sumOf { it.price * it.quantity }
                    val taxAmount = priceAmount * 0.1
                    val totalAmount = priceAmount + taxAmount
                    val totalQuantity = carts.sumOf { it.quantity }

                    // buat objek Bill
                    val bill = Bill(
                        userId = userId,
                        date = getCurrentDate(),
                        time = getCurrentTime(),
                        quantity = totalQuantity,
                        totalPrice = totalAmount.toInt()
                    )

                    //insert bill to database
                    db.billDao().insertBill(bill)

                    withContext(Dispatchers.Main) {
                        billItems.clear()
                        billItems.addAll(carts)
                        Log.d("BillAfterActivity", "Cart items loaded: ${carts.size} items.")
                        adapter.notifyDataSetChanged()

                        // Menampilkan username dan lokasi
                        _username.text = user?.username ?: "Unknown"
                        _locationName.text = "Your Location"

                        // Harga total semua item
                        _priceAmount.text = formatToRupiah(priceAmount)

                        // 10% dari total price
                        _taxAmount.text = formatToRupiah(taxAmount.toInt())

                        // Hitung total harga
                        _totalAmount.text = formatToRupiah(totalAmount.toInt())
                    }
                    Log.d("BillAfterActivity", "Bill saved successfully!")
                } catch (e: Exception) {
                    Log.e("BillAfterActivity", "Error fetching data: ${e.message}")
                }
            }
        }






    }

    private fun formatToRupiah(amount: Int): String {
        val format = NumberFormat.getInstance(Locale("id", "ID")) // Format Rupiah
        return format.format(amount)
    }

}