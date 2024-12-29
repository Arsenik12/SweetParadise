package project.uas.sweetparadise

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Category
import project.uas.sweetparadise.database.Menu
import java.io.ByteArrayOutputStream

class addMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _btnTambah = findViewById<Button>(R.id.btnTambah)
        val _etNama = findViewById<EditText>(R.id.etNama)
        val _etDescription = findViewById<EditText>(R.id.etDescription)
        val _etPrice = findViewById<EditText>(R.id.etPrice)
        val _etCategory = findViewById<EditText>(R.id.etCategory)
        val _btnAddKategori = findViewById<Button>(R.id.btnAddKategori)

        _btnTambah.setOnClickListener {
            if (_etNama.text.isEmpty() || _etDescription.text.isEmpty() || _etPrice.text.isEmpty() || _etCategory.text.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua kolom", Toast.LENGTH_SHORT).show()
            } else {
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.reese_fudge)
                val imageBytes = bitmapToByteArray(bitmap)

                CoroutineScope(Dispatchers.IO).launch {
                    db.menuDao().insertMenu(
                        Menu(
                            name = _etNama.text.toString(),
                            description = _etDescription.text.toString(),
                            price = _etPrice.text.toString().toInt(),
                            categoryId = _etCategory.text.toString().toInt(),
                            image = imageBytes
                        )
                    )

                    // Setelah proses selesai, tampilkan Toast dan kembali ke activity utama di UI thread
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@addMenu,
                            "Menu berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()  // Pastikan finish() dipanggil setelah data masuk
                    }
                }
            }
        }
        _btnAddKategori.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.categoryDao().insertCategory(
                    Category(
                        name = _etCategory.text.toString()
                    )
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@addMenu,
                        "Menu berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
