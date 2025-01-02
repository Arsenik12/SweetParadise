package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Bill
import project.uas.sweetparadise.database.Cart
import project.uas.sweetparadise.database.History
import project.uas.sweetparadise.helper.DateHelper.getCurrentDate
import project.uas.sweetparadise.helper.TimeHelper.getCurrentTime
import java.text.NumberFormat
import java.util.Locale

class ProfileTransactionsDetail : AppCompatActivity() {

    private lateinit var adapter: adapterHistory
    private var historyItems: MutableList<Cart> = mutableListOf()
    private var userId: Int = -1 // Tambahkan variabel global untuk userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_transactions_detail)

        val recyclerView = findViewById<RecyclerView>(R.id.rvOrderBill)
        val db = AppDatabase.getDatabase(this)

        adapter = adapterHistory(historyItems, db)
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
        val _otherAmount = findViewById<TextView>(R.id.otherAmount)
        val _totalAmount = findViewById<TextView>(R.id.totalAmount)

        _buttonBack.setOnClickListener {
            val intent = Intent(this, ProfileTransactionsActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val historys = db.historyDao().getHistoryByUserId(userId)
                val user = db.userDao().getUserById(userId)

                withContext(Dispatchers.Main) {
                    historyItems.clear()
                    historyItems.addAll(historys)
                    adapter.notifyDataSetChanged()

                    // Process cart data and calculate amounts
                    val priceAmount = historys.sumOf { it.price * it.quantity }
                    val taxAmount = priceAmount * 0.1
                    val totalAmount = priceAmount + taxAmount


                    withContext(Dispatchers.Main) {
                        historyItems.clear()
                        historyItems.addAll(historys)
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
                }
                } catch (e: Exception) {
                    Log.e("ProfileTransactions", "Error loading transaction: ${e.message}", e)
                }

            }
        }
        private fun formatToRupiah(amount: Int): String {
            val format = NumberFormat.getInstance(Locale("id", "ID")) // Format Rupiah
            return format.format(amount)
        }
    }