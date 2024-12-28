package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

        Thread {
            val readMenu = db.menuDao().getAllMenus().toMutableList()
            runOnUiThread {
                menus.addAll(readMenu)
                adapter.notifyDataSetChanged()
            }
        }.start()

        adapter = adapterMenu(menus)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val _buttonBack = findViewById<ImageView>(R.id.back)
        val _btnSearch = findViewById<ImageView>(R.id.search)
        val _etSearch = findViewById<EditText>(R.id.search_bar)
        val _buttonAdd = findViewById<FloatingActionButton>(R.id.btnAdd)

        _buttonBack.setOnClickListener {
            finish()
        }

        _buttonAdd.setOnClickListener {
            val intent = Intent(this, addMenu::class.java)
            startActivity(intent)
        }
    }

}