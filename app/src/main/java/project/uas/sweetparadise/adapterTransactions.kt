package project.uas.sweetparadise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.uas.sweetparadise.database.Bill

class adapterTransactions(
    private val daftarBills: MutableList<Bill>
) : RecyclerView.Adapter<adapterTransactions.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tDate: TextView = itemView.findViewById(R.id.tDate)
        val tTime: TextView = itemView.findViewById(R.id.tTime)
        val tQuantity: TextView = itemView.findViewById(R.id.tQuantity)
        val tPrice: TextView = itemView.findViewById(R.id.tPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rec_transactions, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = daftarBills.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val bill = daftarBills[position]
        holder.tDate.text = bill.date
        holder.tTime.text = bill.time
        holder.tQuantity.text = bill.quantity.toString()
        holder.tPrice.text = "Rp ${bill.totalPrice}"
    }
}
