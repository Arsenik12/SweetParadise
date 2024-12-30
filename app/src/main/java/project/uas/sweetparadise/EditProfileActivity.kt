package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val _etNama = findViewById<EditText>(R.id.etNama)
        val _etPhoneNo = findViewById<EditText>(R.id.etPhoneNo)
        val _btnSave = findViewById<Button>(R.id.btnSave)
        val _btnBack = findViewById<ImageView>(R.id.btnBack)

        // terima data
        val name = intent.getStringExtra("PROFILE_NAME")
        val phoneNo = intent.getStringExtra("PROFILE_PHONE_NO")

        _etNama.setText(name)
        _etPhoneNo.setText(phoneNo)

        _btnSave.setOnClickListener {
            val ETname = _etNama.text.toString()
            val ETphoneNo = _etPhoneNo.text.toString()

            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("ET_PROFILE_NAME", ETname) // kirim edited nama
                putExtra("ET_PROFILE_PHONE_NO", ETphoneNo) // kirim edited no HP
                putExtra("ET_PROFILE_STATUS", "edited") // kirim status
            }
            startActivity(intent)
        }

        _btnBack.setOnClickListener {
            finish()
        }
    }
}