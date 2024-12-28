package project.uas.sweetparadise

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase

class MenuDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_detail)

        // Mendapatkan referensi ke elemen UI
        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _tvTitle = findViewById<TextView>(R.id.tvTitle)
        val _imgMenu = findViewById<ImageView>(R.id.imgMenu)
        val _tvPrice = findViewById<TextView>(R.id.tvPrice)
        val _tvDescription = findViewById<TextView>(R.id.tvDescription)
        val _btnNotes = findViewById<FrameLayout>(R.id.btnNotes)

        val menuId = intent.getIntExtra("id", -1)
        if (menuId == -1) {
            finish()
            return
        }

        val menuName = intent.getStringExtra("name") ?: "Unknown Menu"
        val menuDescription = intent.getStringExtra("description") ?: "No description provided."
        val menuPrice = intent.getIntExtra("price", 0)

        val db = AppDatabase.getDatabase(this)

        _tvTitle.text = menuName
        _tvPrice.text = "Rp. $menuPrice"
        _tvDescription.text = menuDescription

        // Mengambil data gambar di thread terpisah
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
            // Tambahkan aksi untuk Notes jika diperlukan
        }
    }
}
