package project.uas.sweetparadise

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Cart

class MenuDetailActivity : AppCompatActivity() {
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_detail)

        val sharedPreferences = getSharedPreferences("SweetParadisePrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("CURRENT_USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _tvTitle = findViewById<TextView>(R.id.tvTitle)
        val _imgMenu = findViewById<ImageView>(R.id.imgMenu)
        val _tvPrice = findViewById<TextView>(R.id.tvPrice)
        val _tvDescription = findViewById<TextView>(R.id.tvDescription)
        val _btnAddCart = findViewById<Button>(R.id.btnAddCart)
        val _tvQuantity = findViewById<TextView>(R.id.tvQuantity)
        val _btnAdd = findViewById<ImageView>(R.id.btnAdd)
        val _btnSub = findViewById<ImageView>(R.id.btnSub)
        val _etNote = findViewById<TextView>(R.id.etNote)

        val menuId = intent.getIntExtra("id", -1)
        val menuName = intent.getStringExtra("name") ?: "Unknown Menu"
        val menuDescription = intent.getStringExtra("description") ?: "No description provided."
        val menuPrice = intent.getIntExtra("price", 0)

        val db = AppDatabase.getDatabase(this)

        _tvTitle.text = menuName
        _tvPrice.text = "Rp. $menuPrice"
        _tvDescription.text = menuDescription

        lifecycleScope.launch {
            val image = withContext(Dispatchers.IO) { db.menuDao().getImage(menuId) }
            image.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                _imgMenu.setImageBitmap(bitmap)
            }
        }

        _buttonBack.setOnClickListener {
            finish()
        }

        _btnAddCart.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val existingCartItem = db.cartDao().getCartItemByMenuId(userId, menuId)

                if (existingCartItem != null) {
                    val newQuantity = existingCartItem.quantity + _tvQuantity.text.toString().toInt()

                    db.cartDao().updateCart(
                            id = existingCartItem.id,
                            quantity = newQuantity,
                            menuNote = _etNote.text.toString()
                    )

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MenuDetailActivity,
                            "Kuantitas menu berhasil diperbarui di Cart",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                } else {
                    db.cartDao().insertCart(
                        Cart(
                            userId =  userId,
                            menuId = menuId,
                            price = menuPrice,
                            quantity = _tvQuantity.text.toString().toInt(),
                            menuNote = _etNote.text.toString()
                        )
                    )

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MenuDetailActivity,
                            "Menu berhasil ditambahkan ke Cart",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        }


        _btnAdd.setOnClickListener {
            _tvQuantity.text = (_tvQuantity.text.toString().toInt() + 1).toString()
        }

        _btnSub.setOnClickListener {
            if (_tvQuantity.text.toString().toInt() > 1) {
                _tvQuantity.text = (_tvQuantity.text.toString().toInt() - 1).toString()
            }
        }
    }
}
