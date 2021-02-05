package com.ecommerce.shopmitt.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.TransactionModel
import com.ecommerce.shopmitt.utils.Constants.CURRENCY
import kotlin.properties.Delegates

class WalletAdapter(val onItemClicked: (TransactionModel.Data.History) -> Unit) :
    RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val transId: TextView = view.findViewById(R.id.trans_id)
        val transAmt: TextView = view.findViewById(R.id.trans_amt)
        val transDate: TextView = view.findViewById(R.id.trans_date)
        val transStatus: TextView = view.findViewById(R.id.trans_status)
        val transType: TextView = view.findViewById(R.id.trans_type)

        fun bind(item: TransactionModel.Data.History) {
            transId.text = "Order ID: #" + item.orderId
            transAmt.text = "Amount: " + item.amount
            transDate.text = item.date
            if (item.type == "D") {
                transType.text = "Debit"
                transType.background = ContextCompat.getDrawable(itemView.context,R.color.quantum_googred)
            }
            else {
                transType.text = "Credit"
                transType.background = ContextCompat.getDrawable(itemView.context,R.color.colorPrimary)
            }

            transStatus.text = item.status
            transDate.text = item.date
        }
    }

    var items: MutableList<TransactionModel.Data.History> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_wallet_item, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)
    }

    override fun getItemCount() = items.size
}