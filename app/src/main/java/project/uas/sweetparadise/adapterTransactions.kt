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
import project.uas.sweetparadise.database.Bill

class adapterTransactions (private val daftarTransactions : MutableList<Bill>, private val db: AppDatabase) :
    RecyclerView.Adapter<adapterTransactions.ListViewHolder>() {
        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val _dateTime = itemView.findViewById<TextView>(R.id.dateTime)
            val _tQuantity = itemView.findViewById<TextView>(R.id.tQuantity)
            val _tPrice = itemView.findViewById<TextView>(R.id.tPrice)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ListViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rec_transactions, parent, false)
            return ListViewHolder(view)
        }
    override fun getItemCount(): Int {
        return daftarTransactions.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val dtTrans = daftarTransactions[position]

        CoroutineScope(Dispatchers.IO).launch {
            val user = db.userDao().getUserById(dtTrans.userId)
            val bill = db.billDao().getBillsByUserId(dtTrans.userId)

            if (user != null) {
                withContext(Dispatchers.Main) {
                    holder._dateTime.text =
                }
            }
        }
    }
}