package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.uas.sweetparadise.database.Cart

class BillAfterActivity : AppCompatActivity() {

    private lateinit var adapter: adapterBill
    private var billItems: MutableList<Cart> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_after_order)

        val recyclerView = findViewById<RecyclerView>(R.id.rvOrderBill)
        val db = AppDatabase.getDatabase(this)

        adapter = adapterBill(billItems)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _username = findViewById<TextView>(R.id.username)
        val _locationName = findViewById<TextView>(R.id.locationName)
        val _date = findViewById<TextView>(R.id.date)
        val _time = findViewById<TextView>(R.id.time)
        val _priceAmount = findViewById<TextView>(R.id.priceAmount)
//        val _taxAmount = findViewById<TextView>(R.id.taxAmount)
//        val _otherAmount = findViewById<TextView>(R.id.otherAmount)
        val _totalAmount = findViewById<TextView>(R.id.totalAmount)
        val _btnGenerateQR = findViewById<TextView>(R.id.btnGenerateQR)

        _buttonBack.setOnClickListener {
            val intent = Intent(this, CartOrderActivity::class.java)
            startActivity(intent)
        }








    }
}