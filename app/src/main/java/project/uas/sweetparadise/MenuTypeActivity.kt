package project.uas.sweetparadise

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_type)

        val _btnDineIn = findViewById<FrameLayout>(R.id.btnDineIn)
        val _btnTakeAway = findViewById<FrameLayout>(R.id.btnTakeAway)
        val _btnDelivery = findViewById<FrameLayout>(R.id.btnDelivery)

        val _navHome = findViewById<LinearLayout>(R.id.navHome)
        val _navMenu = findViewById<LinearLayout>(R.id.navMenu)
        val _navProfile = findViewById<LinearLayout>(R.id.navProfile)

        _btnDineIn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        _btnTakeAway.setOnClickListener {
            val intent = Intent(this, MenuTakeAwayActivity::class.java)
            startActivity(intent)
        }

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

        _navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        _navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}