package project.uas.sweetparadise

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.media.audiofx.BassBoost
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.viewpager2.widget.ViewPager2

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Ambil username dari Intent di LoginActivity
        val username = intent.getStringExtra("USERNAME") ?: "Guest"

        // Setup username ke TextView
        usernameTextView = findViewById(R.id.tvUsername)
        usernameTextView.text = "$username"

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
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        val btnDelivery = findViewById<ImageView>(R.id.btnDelivery)
        btnDelivery.setOnClickListener {
            if (isLocationEnabled()) {
                // Jika lokasi aktif maka lanjut ke MenuActivity
                val intent = Intent(this, MenuActivity::class.java)
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

