package project.uas.sweetparadise

import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Menu

class MenuActivity : AppCompatActivity() {

    private lateinit var adapter: adapterMenu
    private var menus: MutableList<Menu> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val recyclerView = findViewById<RecyclerView>(R.id.rvMenu)
        val db = AppDatabase.getDatabase(this)

        menus = db.menuDao().getAllMenus().toMutableList()

        adapter = adapterMenu(menus.toMutableList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _search = findViewById<ImageView>(R.id.search)

        _buttonBack.setOnClickListener {
            finish()
        }
    }
}