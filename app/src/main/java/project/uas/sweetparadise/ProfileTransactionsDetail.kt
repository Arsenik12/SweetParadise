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
        val _orderType = findViewById<TextView>(R.id.orderType)

        _buttonBack.setOnClickListener {
            val intent = Intent(this, ProfileTransactionsActivity::class.java)
            startActivity(intent)
        }

        // Retrieve the selected bill's date and time from the Intent
        val billDate = intent.getStringExtra("BILL_DATE") ?: ""
        val billTime = intent.getStringExtra("BILL_TIME") ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get history items matching the selected bill's date and time
                val filteredHistoryItems = db.historyDao().getHistoryByUserId(userId).filter { historyItem ->
                    historyItem.date == billDate && historyItem.time == billTime
                }

                val user = db.userDao().getUserById(userId)

                withContext(Dispatchers.Main) {
                    // Update the RecyclerView with the filtered history items
                    historyItems.clear()
                    historyItems.addAll(filteredHistoryItems)
                    adapter.notifyDataSetChanged()

                    // Calculate amounts based on the filtered history
                    val priceAmount = filteredHistoryItems.sumOf { it.price * it.quantity }
                    val taxAmount = priceAmount * 0.1
                    val totalAmount = priceAmount + taxAmount

                    // Get the status from the first filtered history item (assuming uniform status)
                    val orderTypeText = when (filteredHistoryItems.firstOrNull()?.status) {
                        1 -> "Dine In"
                        2 -> "Take Away"
                        3 -> "Delivery"
                        else -> "Unknown"
                    }
                    _orderType.text = orderTypeText

                    // Display user information and calculated amounts
                    _date.text = billDate
                    _time.text = billTime
                    _username.text = user?.username ?: "Unknown"
                    _locationName.text = "Your Location"
                    _priceAmount.text = formatToRupiah(priceAmount)
                    _taxAmount.text = formatToRupiah(taxAmount.toInt())
                    _totalAmount.text = formatToRupiah(totalAmount.toInt())
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