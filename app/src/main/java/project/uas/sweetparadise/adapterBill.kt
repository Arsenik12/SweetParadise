package project.uas.sweetparadise

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.uas.sweetparadise.database.Cart

class adapterBill(private val daftarBill: MutableList<Cart>) : RecyclerView.Adapter<adapterBill.ListViewHolder>() {




    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _menuName = itemView.findViewById<TextView>(R.id.menuName)
        val _menuNote = itemView.findViewById<TextView>(R.id.menuNote)
        val _menuQuantity = itemView.findViewById<TextView>(R.id.menuQuantity)
        val _menuPrice = itemView.findViewById<TextView>(R.id.menuPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


}