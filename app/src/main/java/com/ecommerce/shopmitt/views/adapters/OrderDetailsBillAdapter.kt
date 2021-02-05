package com.ecommerce.shopmitt.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.OrderDetailsModel
import kotlin.properties.Delegates

class OrderDetailsBillAdapter(val onItemClicked: (OrderDetailsModel.Data.Total) -> Unit) :
    RecyclerView.Adapter<OrderDetailsBillAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvKey: TextView = view.findViewById(R.id.tv_key)
        private val tvVal: TextView = view.findViewById(R.id.tv_value)

        fun bind(item: OrderDetailsModel.Data.Total) {
            tvKey.text = item.title
            tvVal.text = item.text
        }
    }

    var items: MutableList<OrderDetailsModel.Data.Total> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_orders_bill, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)
    }

    override fun getItemCount() = items.size
}