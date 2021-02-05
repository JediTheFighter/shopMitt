package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.models.ViewCart

class CartBillAdapter(
    val context: Context,
    val list: ArrayList<ViewCart.Data.Total>
): RecyclerView.Adapter<CartBillAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.bill_title)
        val value: TextView = view.findViewById(R.id.bill_value)

        fun bind(item: ViewCart.Data.Total) {
            title.text = item.title
            value.text = item.text.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.row_cart_bill, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.bind(item)
    }

    override fun getItemCount() = list.size
}