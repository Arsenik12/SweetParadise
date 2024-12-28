package project.uas.sweetparadise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.uas.sweetparadise.database.Menu

class adapterMenu(private val daftarMenu: MutableList<Menu>) :
    RecyclerView.Adapter<adapterMenu.ListViewHolder>() {
        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val _menuName = itemView.findViewById<TextView>(R.id.menuName)
            val _menuDescription = itemView.findViewById<TextView>(R.id.menuDescription)
            val _menuPrice = itemView.findViewById<TextView>(R.id.menuPrice)
            val _menuFavorite = itemView.findViewById<ImageView>(R.id.ivFav)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): adapterMenu.ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_cart_order, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daftarMenu.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val dtMenu = daftarMenu[position]
        holder._menuName.text = dtMenu.name
        holder._menuDescription.text = dtMenu.description
        holder._menuPrice.text = "Rp ${dtMenu.price}"

        if (dtMenu.favorite == true)
        holder._menuFavorite.setImageResource(R.drawable.heart_icon_chosen)

        holder._menuFavorite.setOnClickListener {
            if (dtMenu.favorite == true) {
                dtMenu.favorite = false
                holder._menuFavorite.setImageResource(R.drawable.heart_icon)
            } else {
                dtMenu.favorite = true
                holder._menuFavorite.setImageResource(R.drawable.heart_icon_chosen)
            }
        }
    }
}