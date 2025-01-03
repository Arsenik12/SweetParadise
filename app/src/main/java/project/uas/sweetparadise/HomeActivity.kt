package project.uas.sweetparadise

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private val images = listOf(
        listOf(R.drawable.carousel1), // Item 1
        listOf(R.drawable.carousel1), // Item 2
        listOf(R.drawable.carousel1)  // Item 3
    )

    private var currentPage = 0

    private var userId: Int = -1 // Tambahkan variabel global untuk userId


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val sharedPreferences = getSharedPreferences("SweetParadisePrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("CURRENT_USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val _navMenu = findViewById<LinearLayout>(R.id.navMenu)
        val _navProfile = findViewById<LinearLayout>(R.id.navProfile)
        val _btnPoints = findViewById<ImageView>(R.id.btnPoints)

        val db = AppDatabase.getDatabase(this)

        var username: String? = null

        // Coroutine untuk mengambil data dari database di background thread (IO)
        CoroutineScope(Dispatchers.IO).launch {
            // Ambil data username dari database
            val usernameFromDb = db.userDao().getUsernameById(userId)

            // Update UI di thread utama setelah mendapatkan data
            withContext(Dispatchers.Main) {
                usernameTextView = findViewById(R.id.tvUsername)
                usernameTextView.text = usernameFromDb // Menampilkan username di TextView
            }
        }

        _navMenu.setOnClickListener {
            val intent = Intent(this, MenuTypeActivity::class.java)
            startActivity(intent)
        }

        _navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        _btnPoints.setOnClickListener {
            val intent = Intent(this, CartOrderActivity::class.java)
            startActivity(intent)
        }

        // Setup tombol notifikasi
        val _btnNotif = findViewById<ImageView>(R.id.btnNotification)
        _btnNotif.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        // Setup ViewPager2
        viewPager = findViewById(R.id.carouselViewPager)
        viewPager.adapter = CarouselAdapter(images)

        // Start auto-scroll
        startAutoScroll()

        // Setup tombol Dine In, Take Away, dan Delivery
        val _btnDineIn = findViewById<ImageView>(R.id.btnDineIn)
        _btnDineIn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        val _btnTakeAway = findViewById<ImageView>(R.id.btnTakeAway)
        _btnTakeAway.setOnClickListener {
            val intent = Intent(this, MenuTakeAwayActivity::class.java)
            startActivity(intent)
        }

        val _btnDelivery = findViewById<ImageView>(R.id.btnDelivery)
        _btnDelivery.setOnClickListener {
            if (isLocationEnabled()) {
                // Jika lokasi aktif maka lanjut ke MenuActivity
                val intent = Intent(this, OSMAddressActivity::class.java)
                startActivity(intent)
            } else {
                // Jika lokasi tidak aktif maka minta user diminta untuk menyalakan lokasi
                Toast.makeText(this, "Harap aktifkan lokasi Anda untuk melanjutkan!", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
    }

    // Fungsi untuk memeriksa apakah lokasi aktif
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Fungsi untuk mengubah carousel image setiap 3 detik menggunakan handler
    private fun startAutoScroll() {
        val runnable = object : Runnable {
            override fun run() {
                if (currentPage == images.size) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnable, 3000)
    }

    // Fungsi untuk menghentikan auto-scroll
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}