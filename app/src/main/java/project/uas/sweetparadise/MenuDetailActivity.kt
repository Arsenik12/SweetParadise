package project.uas.sweetparadise

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
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
import project.uas.sweetparadise.database.Menu

class MenuDetailActivity : AppCompatActivity() {
    private var _userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_detail)

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _tvTitle = findViewById<TextView>(R.id.tvTitle)
        val _imgMenu = findViewById<ImageView>(R.id.imgMenu)
        val _tvPrice = findViewById<TextView>(R.id.tvPrice)
        val _tvDescription = findViewById<TextView>(R.id.tvDescription)
        val _btnNotes = findViewById<FrameLayout>(R.id.btnNotes)
        val _btnAddCart = findViewById<Button>(R.id.btnAddCart)
        var _tvQuantity = findViewById<TextView>(R.id.tvQuantity)
        val _btnAdd = findViewById<ImageView>(R.id.btnAdd)
        val _btnSub = findViewById<ImageView>(R.id.btnSub)
        val _tvNote = findViewById<TextView>(R.id.tvNote)

        val menuId = intent.getIntExtra("id", -1)
        if (menuId == -1) {
            finish()
            return
        }

        val menuName = intent.getStringExtra("name") ?: "Unknown Menu"
        val menuDescription = intent.getStringExtra("description") ?: "No description provided."
        val menuPrice = intent.getIntExtra("price", 0)

        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            _userId = withContext(Dispatchers.IO) { db.userDao().getId(1) }
        }

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

        _btnNotes.setOnClickListener {
        }

        _btnAddCart.setOnClickListener {
//            val intent = Intent(this, MenuDetailActivity::class.java)
//            intent.putExtra("quantity", _tvQuantity.text.toString().toInt())
//            intent.putExtra("amount", menuPrice.toString().toInt() * _tvQuantity.text.toString().toInt())
            CoroutineScope(Dispatchers.IO).launch {
                db.cartDao().insertCart(
                    Cart(
                        userId = _userId,
                        menuId = menuId,
                        price = menuPrice.toString().toInt() * _tvQuantity.text.toString().toInt(),
                        quantity = _tvQuantity.text.toString().toInt(),
                        menuNote = _tvNote.text.toString()
                    )
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MenuDetailActivity,
                        "Menu berhasil ditambahkan ke Cart",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
//            startActivity(intent)
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
