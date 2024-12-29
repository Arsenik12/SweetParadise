package project.uas.sweetparadise

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import project.uas.sweetparadise.database.AddressDatabase
import project.uas.sweetparadise.database.AddressEntity
import java.util.*

class OSMAddressActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var gpsButton: ImageView
    private lateinit var confirmButton: Button
    private lateinit var addressInput: EditText
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private val locationPermissionCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Konfigurasi OSMDroid
        Configuration.getInstance().load(this, applicationContext.getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_lokasi)

        mapView = findViewById(R.id.map)
        gpsButton = findViewById(R.id.btnGps)
        confirmButton = findViewById(R.id.btnConfirmLocation)
        addressInput = findViewById(R.id.etAddressInput)

        mapView.setMultiTouchControls(true)

        // Meminta izin lokasi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            enableLocation()
        }

        // Setup tombol back ke homepage
        val _btnBack = findViewById<ImageView>(R.id.btnBack)
        _btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Setup tombol GPS
        gpsButton.setOnClickListener {
            val location = myLocationOverlay.myLocation
            if (location != null) {
                // Zoom ke lokasi pengguna
                mapView.controller.setCenter(location)
                mapView.controller.setZoom(18.0)

                // Mendapatkan alamat dari lokasi
                val address = getAddressFromLocation(location.latitude, location.longitude)
                addressInput.setText(address)
            } else {
                Toast.makeText(this, "Lokasi tidak ditemukan!", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup tombol Konfirmasi
        confirmButton.setOnClickListener {
            val address = addressInput.text.toString()
            if (address.isNotEmpty()) {
                saveAddressToDatabase(address) {
                    val intent = Intent(this@OSMAddressActivity, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Alamat kosong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk mengaktifkan overlay lokasi pengguna
    private fun enableLocation() {
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.runOnFirstFix {
            runOnUiThread {
                // Memusatkan peta ke lokasi user pada saat startup
                val location = myLocationOverlay.myLocation
                if (location != null) {
                    mapView.controller.setZoom(15.0)
                    mapView.controller.setCenter(location)
                }
            }
        }
        mapView.overlays.add(myLocationOverlay)
    }

    // Fungsi untuk mendapatkan alamat dari latitude dan longitude
    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            "Alamat tidak ditemukan"
        }
    }

    // Fungsi untuk menyimpan alamat ke Room Database
    private fun saveAddressToDatabase(address: String, onComplete: () -> Unit) {
        val database = AddressDatabase.getDatabase(this)
        lifecycleScope.launch {
            database.addressDao().insertAddress(AddressEntity(address = address))
            runOnUiThread {
                onComplete()
            }
        }
    }
}