package project.uas.sweetparadise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Cart
import java.text.NumberFormat
import java.util.Locale

class adapterBill(private val daftarBill: MutableList<Cart>, private val db: AppDatabase) :
    RecyclerView.Adapter<adapterBill.ListViewHolder>() {


    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _amountOrder = itemView.findViewById<TextView>(R.id.amountOrder)
        val _menuName = itemView.findViewById<TextView>(R.id.menuName)
        val _menuPrice = itemView.findViewById<TextView>(R.id.menuPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rec_bill, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daftarBill.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val dtBill = daftarBill[position]

        CoroutineScope(Dispatchers.IO).launch {
            val menu = db.menuDao().getMenuById(dtBill.menuId)

            if (menu != null) {
                withContext(Dispatchers.Main) {
                    holder._menuName.text = menu.name
                    holder._menuPrice.text = "${formatToRupiah(dtBill.price * dtBill.quantity)}"
                    holder._amountOrder.text = "${dtBill.quantity}x"
                }
            } else {
                withContext(Dispatchers.Main) {
                    holder._menuName.text = "Menu Tidak Ditemukan"
                    holder._menuPrice.text = "0"
                    holder._amountOrder.text = "0"
                }
            }
        }
    }


    private fun formatToRupiah(amount: Int): String {
        val format = NumberFormat.getInstance(Locale("id", "ID")) // Format Rupiah
        return format.format(amount)
    }

}