package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.models.ShippingMethodsModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.PaymentActivity
import com.google.android.material.card.MaterialCardView
import com.google.gson.JsonObject
import kotlin.properties.Delegates

class StoreAdapter(
    val onItemClicked: (String) -> Unit,
    val context: Context
) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private val storeBridge = context as StoreBridge
    private var lastCheckedPosition = -1
    private var onclick = false

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rdBtn: RadioButton = view.findViewById(R.id.rd_btn)

        fun bind(item: String) {
            rdBtn.text = item
        }

        fun setClick(b: Boolean, item: String) {
            lastCheckedPosition = rdBtn.tag as Int
            rdBtn.isChecked = true

            if (b) {
                storeBridge.onClickStore(item)
                notifyDataSetChanged()
            }
        }

    }



    var items: MutableList<String> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_stores, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)
        holder.rdBtn.setOnClickListener {
            onclick = true
            holder.setClick(true,item)
        }

        holder.rdBtn.tag = position

        if (!onclick) if (position == 0) {
            holder.setClick(false, item)
        }

        holder.rdBtn.isChecked = lastCheckedPosition == position
    }

    override fun getItemCount() = items.size

    fun getCheckedPosition() : Int {
        return lastCheckedPosition
    }

    fun setFirstItem() {
        lastCheckedPosition = 0
        storeBridge.onClickStore(items[0])
        notifyDataSetChanged()
    }

    interface StoreBridge {
        fun onClickStore(storeName: String)
    }
}