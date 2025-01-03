package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import project.uas.sweetparadise.helper.DateHelper.getCurrentDate
import project.uas.sweetparadise.helper.TimeHelper.getCurrentTime
import java.text.NumberFormat
import java.util.Locale

class BillAfterActivity : AppCompatActivity() {

    private lateinit var adapter: adapterBill
    private var billItems: MutableList<Cart> = mutableListOf()
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_after_order)

        val recyclerView = findViewById<RecyclerView>(R.id.rvOrderBill)
        val db = AppDatabase.getDatabase(this)

        adapter = adapterBill(billItems, db)
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
        val _username = findViewById<TextView>(R.id.username)
        val _locationName = findViewById<TextView>(R.id.locationName)
        val _date = findViewById<TextView>(R.id.date)
        val _time = findViewById<TextView>(R.id.time)
        val _priceAmount = findViewById<TextView>(R.id.priceAmount)
        val _taxAmount = findViewById<TextView>(R.id.taxAmount)
        val _totalAmount = findViewById<TextView>(R.id.totalAmount)

        _buttonBack.setOnClickListener {
            val intent = Intent(this, CartOrderActivity::class.java)
            startActivity(intent)
        }

        _date.text = getCurrentDate()
        _time.text = getCurrentTime()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val carts = db.cartDao().getUserCart(userId)
                val user = db.userDao().getUserById(userId)
                val address = db.addressDao().getAddressByUserId(userId)?.address ?: "Unknown Location"

                val priceAmount = carts.sumOf { it.price * it.quantity }
                val taxAmount = priceAmount * 0.1
                val totalAmount = priceAmount + taxAmount

                withContext(Dispatchers.Main) {
                    billItems.clear()
                    billItems.addAll(carts)
                    adapter.notifyDataSetChanged()

                    _username.text = user?.username ?: "Unknown"
                    _locationName.text = address
                    _priceAmount.text = formatToRupiah(priceAmount)
                    _taxAmount.text = formatToRupiah(taxAmount.toInt())
                    _totalAmount.text = formatToRupiah(totalAmount.toInt())
                }
            } catch (e: Exception) {
                Log.e("BillAfterActivity", "Error fetching data: ${e.message}")
            }
        }
    }

    private fun formatToRupiah(amount: Int): String {
        val format = NumberFormat.getInstance(Locale("id", "ID"))
        return format.format(amount)
    }
}
