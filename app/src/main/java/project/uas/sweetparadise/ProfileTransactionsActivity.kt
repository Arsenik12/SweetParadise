package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Bill

class ProfileTransactionsActivity : AppCompatActivity() {

    private lateinit var adapter: adapterTransactions
    private var billItems: MutableList<Bill> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_transactions)

        val _rvTHistory = findViewById<RecyclerView>(R.id.rvTHistory)
        val _btnBack = findViewById<ImageView>(R.id.btnBack)

        val db = AppDatabase.getDatabase(this)

        adapter = adapterTransactions(billItems)
        _rvTHistory.adapter = adapter
        _rvTHistory.layoutManager = LinearLayoutManager(this)

        // Get userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("SweetParadisePrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("CURRENT_USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        _btnBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Load bills from database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bills = db.billDao().getBillsByUserId(userId)

                withContext(Dispatchers.Main) {
                    billItems.clear()
                    billItems.addAll(bills)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("ProfileTransactions", "Error loading transaction: ${e.message}", e)
            }
        }
    }
}
