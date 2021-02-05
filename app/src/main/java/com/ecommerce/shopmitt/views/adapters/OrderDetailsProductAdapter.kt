package com.ecommerce.shopmitt.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.OrderDetailsModel
import kotlin.properties.Delegates

class OrderDetailsProductAdapter(val onItemClicked: (OrderDetailsModel.Data.Product) -> Unit) :
    RecyclerView.Adapter<OrderDetailsProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvItemName: TextView = view.findViewById(R.id.tv_item_name)
        private val tvQty: TextView = view.findViewById(R.id.tv_qty)
        private val tvRate: TextView = view.findViewById(R.id.tv_rate)
        private val tvAmount: TextView = view.findViewById(R.id.tv_amount)
        private val prodImg: ImageView = view.findViewById(R.id.prod_img)

        fun bind(item: OrderDetailsModel.Data.Product) {

            if (item.option.isNotEmpty()) {
                tvItemName.text = item.name?.replace("&amp;","&") +" "+ item.option[0].value?.replace("&amp;","&")
            } else {
                tvItemName.text = item.name?.replace("&amp;","&")
            }
            tvQty.text = " x "+item.quantity
            tvRate.text = item.price
            tvAmount.text = item.total

            Glide.with(itemView.context)
                .load(item.originalImage)
                .centerCrop()
                .into(prodImg)
        }

    }

    var items: MutableList<OrderDetailsModel.Data.Product> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_orders_product, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)
    }

    override fun getItemCount() = items.size
}