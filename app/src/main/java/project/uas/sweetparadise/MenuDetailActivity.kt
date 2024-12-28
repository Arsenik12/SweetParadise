package project.uas.sweetparadise

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_detail)

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _tvTitle = findViewById<TextView>(R.id.tvTitle)
        val _imgMenu = findViewById<ImageView>(R.id.imgMenu)
        val _tvPrice = findViewById<TextView>(R.id.tvPrice)
        val _tvDescription = findViewById<TextView>(R.id.tvDescription)
        val _btnNotes = findViewById<FrameLayout>(R.id.btnNotes)

        _buttonBack.setOnClickListener {
            finish()
        }



    }
}