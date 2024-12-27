package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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


        val _btnDelivery = findViewById<ImageView>(R.id.btnDelivery)
        val _btnDineIn = findViewById<ImageView>(R.id.btnDineIn)
        val _btnTakeAway = findViewById<ImageView>(R.id.btnTakeAway)

        _btnDelivery.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        _btnDineIn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        _btnTakeAway.setOnClickListener {
            // Handle Take Away button click
        }

    }

    // Mengubah carousel image setiap 3 detik menggunakan handler
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

    // Menghentikan Handler untuk mencegah kebocoran memori saat activity dihancurkan
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

