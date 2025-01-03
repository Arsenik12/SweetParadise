package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

class MenuDeliveryActivity : AppCompatActivity() {

    private lateinit var adapter: adapterMenu
    private var menus: MutableList<Menu> = mutableListOf()
    private var filteredMenus: MutableList<Menu> = mutableListOf()
    private var userId: Int = -1 // Tambahkan variabel global untuk userId

    companion object {
        const val REQUEST_CODE_ADD_TO_CART = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_delivery)

        // Mendapatkan userId dari SharedPreferences
        val sharedPreferences = getSharedPreferences("SweetParadisePrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("CURRENT_USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvMenu)
        val db = AppDatabase.getDatabase(this)

        // Mendapatkan data menu dari database
        lifecycleScope.launch(Dispatchers.IO) {
            val readMenu = db.menuDao().getAllMenus().toMutableList()
            withContext(Dispatchers.Main) {
                menus.addAll(readMenu)
                filteredMenus.addAll(readMenu.filter { it.categoryId == 1 })
                adapter.notifyDataSetChanged()
            }
        }

        // Inisialisasi adapter dan RecyclerView
        adapter = adapterMenu(filteredMenus, this, db.favoriteDao(), userId, 3)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _btnSearch = findViewById<ImageView>(R.id.search)
        val _etSearch = findViewById<EditText>(R.id.search_bar)
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
        val _buttonAdd = findViewById<FloatingActionButton>(R.id.btnAdd)
        val _btnCart = findViewById<Button>(R.id.btnCart)

        val _navHome = findViewById<LinearLayout>(R.id.navHome)
        val _navMenu = findViewById<LinearLayout>(R.id.navMenu)
        val _navProfile = findViewById<LinearLayout>(R.id.navProfile)


        _navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        _navMenu.setOnClickListener {
            val intent = Intent(this, MenuTypeActivity::class.java)
            startActivity(intent)
        }

        _navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Tombol kembali
        _buttonBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Fungsi pencarian
        _btnSearch.setOnClickListener {
            val query = _etSearch.text.toString().trim().lowercase()
            val filteredList = menus.filter { it.name.lowercase().contains(query) }
            filteredMenus.clear()
            filteredMenus.addAll(filteredList)
            adapter.notifyDataSetChanged()
        }

        // Tombol tambah menu
        _buttonAdd.setOnClickListener {
            val intent = Intent(this, addMenu::class.java)
            startActivity(intent)
        }

        // Update data keranjang
        lifecycleScope.launch {
            updateCartData(db, userId, _tvItems, _tvAmount)
        }

        _btnCart.setOnClickListener {
            //kirim status
            val intent = Intent(this, CartOrderActivity::class.java)
            intent.putExtra("STATUS", 3)
            startActivity(intent)
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
    }

    // Menangani hasil dari aktivitas lain
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TO_CART && resultCode == RESULT_OK) {
            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch {
                val _tvItems = findViewById<TextView>(R.id.tvItems)
                val _tvAmount = findViewById<TextView>(R.id.tvAmount)
                updateCartData(db, userId, _tvItems, _tvAmount)
            }
        }


    }

    // Mengupdate data keranjang
    private suspend fun updateCartData(db: AppDatabase, userId: Int, _tvItems: TextView, _tvAmount: TextView) {
        val userCartList = withContext(Dispatchers.IO) { db.cartDao().getUserCart(userId) }
        if (userCartList.isNotEmpty()) {
            val totalQuantity = userCartList.sumOf { it.quantity }
            val totalPrice = userCartList.sumOf { it.price * it.quantity }
            _tvItems.text = "$totalQuantity Item Selected"
            _tvAmount.text = "Rp. $totalPrice"
        } else {
            _tvItems.text = "No items in cart"
            _tvAmount.text = "Rp. 0"
        }
    }

    private fun filterMenuByCategory(categoryId: Int) {
        filteredMenus.clear()
        filteredMenus.addAll(menus.filter { it.categoryId == categoryId })
        adapter.notifyDataSetChanged() // Perbarui RecyclerView dengan menu yang difilter
    }
}
