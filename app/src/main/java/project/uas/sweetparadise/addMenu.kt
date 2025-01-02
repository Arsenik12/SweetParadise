package project.uas.sweetparadise

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
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
import java.util.Locale

class addMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _btnTambah = findViewById<FrameLayout>(R.id.btnTambah)
        val _etNama = findViewById<EditText>(R.id.etNama)
        val _etDescription = findViewById<EditText>(R.id.etDescription)
        val _etPrice = findViewById<EditText>(R.id.etPrice)
        val _etCategory = findViewById<EditText>(R.id.etCategory)
        val _btnAddKategori = findViewById<FrameLayout>(R.id.btnAddKategori)
        val _etImage = findViewById<EditText>(R.id.etImage)

        _btnTambah.setOnClickListener {
            if (_etNama.text.isEmpty() || _etDescription.text.isEmpty() || _etPrice.text.isEmpty() || _etCategory.text.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua kolom", Toast.LENGTH_SHORT).show()
            } else {
                val categoryName = _etCategory.text.toString().trim().lowercase(Locale.getDefault())  // Ensure the category name is lowercase

                CoroutineScope(Dispatchers.IO).launch {
                    val category = db.categoryDao().getCategoryByName(categoryName)

                    if (category == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@addMenu, "Kategori tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val imageName = _etImage.text.toString().trim().replace(" ", "_").lowercase(Locale.getDefault())

                    val imageResId = getDrawableResourceId(imageName)
                    if (imageResId == 0) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@addMenu, "Gambar tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val bitmap = BitmapFactory.decodeResource(resources, imageResId)
                    val imageBytes = bitmapToByteArray(bitmap)

                    val newMenu = Menu(
                        name = _etNama.text.toString(),
                        description = _etDescription.text.toString(),
                        price = _etPrice.text.toString().toInt(),
                        categoryId = category.id,
                        image = imageBytes
                    )

                    // Menambahkan menu baru ke database
                    db.menuDao().insertMenu(newMenu)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@addMenu, "Menu berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }

        _btnAddKategori.setOnClickListener {
            val categoryName = _etCategory.text.toString().lowercase(Locale.getDefault())
            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val newCategory = Category(name = categoryName)
                    db.categoryDao().insertCategory(newCategory)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@addMenu,
                            "Kategori berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }

    }

    private fun getDrawableResourceId(imageName: String): Int {
        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        return if (resId != 0) resId else 0
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream)
        return stream.toByteArray()
    }
}