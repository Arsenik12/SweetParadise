package project.uas.sweetparadise

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.Database.AppDatabase
import project.uas.sweetparadise.R

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

        // Mendapatkan data dari Intent
        val menuId = intent.getIntExtra("id", -1)
        if (menuId == -1) {
            Log.e("MenuDetailActivity", "Invalid menu ID")
            finish()
            return
        }

        val menuName = intent.getStringExtra("name") ?: "Unknown Menu"
        val menuDescription = intent.getStringExtra("description") ?: "No description provided."
        val menuPrice = intent.getIntExtra("price", 0)

        // Set data teks pada elemen UI
        _tvTitle.text = menuName
        _tvPrice.text = "Rp. $menuPrice"
        _tvDescription.text = menuDescription

        // Database instance
        val db = AppDatabase.getDatabase(this)

        // Mengambil data gambar di thread terpisah
        lifecycleScope.launch {
            val image = withContext(Dispatchers.IO) { db.menuDao().getImage(menuId) }
            if (image != null && image.isNotEmpty()) {
                try {
                    val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                    _imgMenu.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Log.e("MenuDetailActivity", "Error decoding image: ${e.message}")
                    _imgMenu.setImageResource(R.drawable.red_velvet)
                }
            } else {
                Log.e("MenuDetailActivity", "Image not found for menu ID: $menuId")
                _imgMenu.setImageResource(R.drawable.red_velvet)
            }
        }

        // Tombol kembali
        _buttonBack.setOnClickListener {
            finish()
        }

        // Tombol Notes (jika diperlukan)
        _btnNotes.setOnClickListener {
            // Tambahkan aksi untuk Notes di sini
        }
    }
}
