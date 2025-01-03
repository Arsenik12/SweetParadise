package project.uas.sweetparadise

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import project.uas.sweetparadise.database.AppDatabase

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val db = AppDatabase.getDatabase(this)

        val _btnProfile = findViewById<FrameLayout>(R.id.btnProfile)
        val _btnLogout = findViewById<TextView>(R.id.btnLogout)

        val _profilePicture = findViewById<ImageView>(R.id.profilePicture)
        val _profileName = findViewById<TextView>(R.id.profileName)
        val _profilePhoneNo = findViewById<TextView>(R.id.profilePhoneNo)

        val _instagram = findViewById<ImageView>(R.id.instagram)
        val _facebook = findViewById<ImageView>(R.id.facebook)
        val _youtube = findViewById<ImageView>(R.id.youtube)
        val _twitter = findViewById<ImageView>(R.id.twitter)
        val _btnCustServ = findViewById<FrameLayout>(R.id.btnCustServ)

        val _btnTransactions = findViewById<FrameLayout>(R.id.btnTransactions)
        val _btnFavorites = findViewById<FrameLayout>(R.id.btnFavorites)
        val _btnSettings= findViewById<FrameLayout>(R.id.btnSettings)
        val _terms = findViewById<FrameLayout>(R.id.terms)
        val _privacyPol = findViewById<FrameLayout>(R.id.privaryPol)

        var name = _profileName.text.toString()
        var phoneNo = _profilePhoneNo.text.toString()

        val _navMenu = findViewById<LinearLayout>(R.id.navMenu)
        val _navHome = findViewById<LinearLayout>(R.id.navHome)


        var edited = "original"

        _btnProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java).apply {
                putExtra("PROFILE_NAME", name) //kirim nama
                putExtra("PROFILE_PHONE_NO", phoneNo) //kirim no HP
            }
            startActivity(intent)
        }

        edited = intent.getStringExtra("ET_PROFILE_STATUS").toString()

        // if status edited, update
        if (edited == "edited") {
            name = intent.getStringExtra("ET_PROFILE_NAME").toString()
            phoneNo = intent.getStringExtra("ET_PROFILE_PHONE_NO").toString()

            _profileName.text = name
            _profilePhoneNo.text = phoneNo
        }


        _navMenu.setOnClickListener {
            val intent = Intent(this, MenuTypeActivity::class.java)
            startActivity(intent)
        }

        _navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        _btnLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        _btnTransactions.setOnClickListener {
            val intent = Intent(this, ProfileTransactionsActivity::class.java)
            startActivity(intent)
        }

        _btnFavorites.setOnClickListener {
            val intent = Intent(this, ProfileFavoritesActivity::class.java)
            startActivity(intent)
        }

        _terms.setOnClickListener {
            val intent = Intent(this, ProfileTermsActivity::class.java)
            startActivity(intent)
        }

        _privacyPol.setOnClickListener {
            val intent = Intent(this, ProfilePrivacyActivity::class.java)
            startActivity(intent)
        }

        // untuk buka socmed
        _instagram.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://www.instagram.com/")
                    setPackage("com.android.chrome")
                }
                    startActivity(intent)
        }

        _facebook.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.facebook.com/")
                setPackage("com.android.chrome")
            }
            startActivity(intent)
        }

        _youtube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.youtube.com/")
                setPackage("com.android.chrome")
            }
            startActivity(intent)
        }

        _twitter.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.x.com/")
                setPackage("com.android.chrome")
            }
            startActivity(intent)
        }

        _btnCustServ.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://web.whatsapp.com/")
                setPackage("com.android.chrome")
            }
            startActivity(intent)
        }

    }
}