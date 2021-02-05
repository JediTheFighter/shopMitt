package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.OrdersModel
import com.ecommerce.shopmitt.views.activities.OrderActivity
import kotlin.properties.Delegates

class OrdersAdapter(
    val onItemClicked: (OrdersModel.Data) -> Unit,
    val context: Context,
) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvOrderId : TextView = view.findViewById(R.id.tv_order_id)
        private val tvProducts : TextView = view.findViewById(R.id.tv_products)
        private val tvPayment : TextView = view.findViewById(R.id.tv_payment)
        private val tvStatus : TextView = view.findViewById(R.id.tv_status)
        private val tvDate : TextView = view.findViewById(R.id.tv_date)
        private val tvQty : TextView = view.findViewById(R.id.tv_qty)
        val root: View = view.findViewById(R.id.rl_root)

        fun bind(item: OrdersModel.Data) {

            tvOrderId.text = "Order ID : #" + item.orderId
            tvProducts.text = item.name
            tvPayment.text = "Total : " +item.total
            tvStatus.text = item.status
            tvDate.text = item.dateAdded

            if(item.products == 1)
                tvQty.text = item.products.toString() + " Item"
            else
                tvQty.text = item.products.toString() + " Items"

            tvStatus.setTextColor(item.getStatusColor())
        }
    }

    var items: MutableList<OrdersModel.Data> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_order, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)

        holder.root.setOnClickListener {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra("order_id", item.orderId)
            intent.putExtra("order_status", item.status)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size
}