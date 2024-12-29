package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Menu

class MenuActivity : AppCompatActivity() {
    private lateinit var adapter: adapterMenu
    private var menus: MutableList<Menu> = mutableListOf()
    private var filteredMenus: MutableList<Menu> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val recyclerView = findViewById<RecyclerView>(R.id.rvMenu)
        val db = AppDatabase.getDatabase(this)

        Thread {
            val readMenu = db.menuDao().getAllMenus().toMutableList()
            runOnUiThread {
                menus.addAll(readMenu)
                filteredMenus.addAll(readMenu.filter { it.categoryId == 1 })
                adapter.notifyDataSetChanged()
            }
        }.start()

        adapter = adapterMenu(filteredMenus)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userId = intent.getIntExtra("USER_ID", -1)  // Mengambil userId, default -1 jika tidak ada
        if (userId != -1) {
            Log.d("ReceivedUserId", "User ID received: $userId")  // Menampilkan userId untuk debugging
        } else {
            Log.e("ReceivedUserId", "No User ID found.")
        }

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _btnSearch = findViewById<ImageView>(R.id.search)
        val _etSearch = findViewById<EditText>(R.id.search_bar)
        val _buttonAdd = findViewById<FloatingActionButton>(R.id.btnAdd)
        val _tvItems = findViewById<TextView>(R.id.tvItems)
        val _tvAmount = findViewById<TextView>(R.id.tvAmount)
        val _btnSnack = findViewById<FrameLayout>(R.id.btnSnack)
        val _btnBeverage = findViewById<FrameLayout>(R.id.btnBeverage)
        val _btnCake = findViewById<FrameLayout>(R.id.btnCake)
        val _btnOther = findViewById<FrameLayout>(R.id.btnOther)
        val _tvCake = findViewById<TextView>(R.id.tvCake)
        val _tvSnack = findViewById<TextView>(R.id.tvSnack)
        val _tvBeverage = findViewById<TextView>(R.id.tvBeverage)
        val _tvOther = findViewById<TextView>(R.id.tvOther)
        val _btnCart = findViewById<FrameLayout>(R.id.btnCart)

        _buttonBack.setOnClickListener {
            finish()
        }

        _buttonAdd.setOnClickListener {
            val intent = Intent(this, addMenu::class.java)
            startActivity(intent)
        }

        _btnSearch.setOnClickListener {
            val query = _etSearch.text.toString().trim().lowercase()

            val filteredList = menus.filter {
                it.name.lowercase().contains(query)
            }

            filteredMenus.clear()
            filteredMenus.addAll(filteredList)
            adapter.notifyDataSetChanged()
        }

        _btnCake.setOnClickListener {
            filterMenuByCategory(1) // Filter berdasarkan kategori Kue
            _btnCake.background = getDrawable(R.drawable.border_button_chosen)
            _tvCake.setTextColor(getColor(R.color.brown))

            _btnSnack.background = getDrawable(R.drawable.border_button)
            _btnBeverage.background = getDrawable(R.drawable.border_button)
            _btnOther.background = getDrawable(R.drawable.border_button)
            _tvSnack.setTextColor(getColor(R.color.gray))
            _tvBeverage.setTextColor(getColor(R.color.gray))
            _tvOther.setTextColor(getColor(R.color.gray))
        }

        _btnSnack.setOnClickListener {
            filterMenuByCategory(2) // Filter berdasarkan kategori Snack
            _btnSnack.background = getDrawable(R.drawable.border_button_chosen)
            _tvSnack.setTextColor(getColor(R.color.brown))

            _btnCake.background = getDrawable(R.drawable.border_button)
            _btnBeverage.background = getDrawable(R.drawable.border_button)
            _btnOther.background = getDrawable(R.drawable.border_button)
            _tvCake.setTextColor(getColor(R.color.gray))
            _tvBeverage.setTextColor(getColor(R.color.gray))
            _tvOther.setTextColor(getColor(R.color.gray))
        }

        _btnBeverage.setOnClickListener {
            filterMenuByCategory(3) // Filter berdasarkan kategori Beverage
            _btnBeverage.background = getDrawable(R.drawable.border_button_chosen)
            _tvBeverage.setTextColor(getColor(R.color.brown))

            _btnCake.background = getDrawable(R.drawable.border_button)
            _btnSnack.background = getDrawable(R.drawable.border_button)
            _btnOther.background = getDrawable(R.drawable.border_button)
            _tvCake.setTextColor(getColor(R.color.gray))
            _tvSnack.setTextColor(getColor(R.color.gray))
            _tvOther.setTextColor(getColor(R.color.gray))
        }

        _btnOther.setOnClickListener {
            filterMenuByCategory(4) // Filter berdasarkan kategori Other
            _btnOther.background = getDrawable(R.drawable.border_button_chosen)
            _tvOther.setTextColor(getColor(R.color.brown))

            _btnCake.background = getDrawable(R.drawable.border_button)
            _btnSnack.background = getDrawable(R.drawable.border_button)
            _btnBeverage.background = getDrawable(R.drawable.border_button)
            _tvCake.setTextColor(getColor(R.color.gray))
            _tvSnack.setTextColor(getColor(R.color.gray))
            _tvBeverage.setTextColor(getColor(R.color.gray))
        }

        lifecycleScope.launch {
            val userCartList = withContext(Dispatchers.IO) { db.cartDao().getUserCart(1) }

            if (userCartList.isNotEmpty()) {
                var totalQuantity = 0
                var totalPrice = 0

                for (cartItem in userCartList) {
                    totalQuantity += cartItem.quantity
                    totalPrice += cartItem.price
                }
                _tvItems.text = "$totalQuantity Item Selected"
                _tvAmount.text = "Rp. $totalPrice"
            } else {
                _tvItems.text = "No items in cart"
                _tvAmount.text = "Rp. 0"
            }
        }

        _btnCart.setOnClickListener {

            val intent = Intent(this, addMenu::class.java)
            startActivity(intent)

//            try {
//                val intent = Intent(this@MenuActivity, CartOrderActivity::class.java)
//                intent.putExtra("USER_ID", userId)  // Mengirimkan userId ke CartOrderActivity
//                Log.d("MenuActivity", "Sending USER_ID: $userId")  // Log untuk memastikan userId dikirim
//                startActivity(intent)
//            } catch (e: Exception) {
//                Log.e("MenuActivity", "Error starting activity: ${e.message}")
//            }
        }
    }

    private fun filterMenuByCategory(categoryId: Int) {
        filteredMenus.clear()
        filteredMenus.addAll(menus.filter { it.categoryId == categoryId })
        adapter.notifyDataSetChanged()
    }
}
