package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

class ShippingMethodsAdapter(
    val onItemClicked: (ShippingMethodsModel.Data.ShippingMethod) -> Unit,
    val context: Context
) :
    RecyclerView.Adapter<ShippingMethodsAdapter.ViewHolder>() {

    private val shippingBridge = context as ShippingMethodBridge
    private var lastCheckedPosition = -1
    private var onclick = false

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.title)
        val image: ImageView = view.findViewById(R.id.img)
        val cvRoot: MaterialCardView = view.findViewById(R.id.cv_root)

        fun bind(item: ShippingMethodsModel.Data.ShippingMethod) {
            title.text = item.title.toString()

            Glide.with(context)
                .load(item.image)
                .centerCrop()
                .into(image)
        }

        fun setClick(b: Boolean, item: ShippingMethodsModel.Data.ShippingMethod) {
            lastCheckedPosition = title.tag as Int

            if (b) {
                if (item.code?.contains("pickup")!!) {
                    shippingBridge.onSelectStore(adapterPosition)
                } else {
                    shippingBridge.onDeselectStore()
                }
                notifyDataSetChanged()
            }

            callSetShippingMethod(lastCheckedPosition)
        }

        fun onSelected() {
            cvRoot.strokeColor = ContextCompat.getColor(context,R.color.colorPrimary)
        }

        fun onDeselected() {
            cvRoot.strokeColor = ContextCompat.getColor(context,R.color.cardViewStroke)
        }
    }

    fun callSetShippingMethod(lastCheckedPosition: Int) {
        (context as PaymentActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                context.hideLoadingDialog()
                val model = `object` as BaseModel
                shippingBridge.onSelectMethod()
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                context.hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, context).setShippingMethod(getShippingMethod(lastCheckedPosition))
    }

    private fun getShippingMethod(checkedPosition: Int): JsonObject {

        val obj = JsonObject()

        val method = JsonObject()
        obj.addProperty("shipping_method", items[checkedPosition].code)

        obj.addProperty("comment", "")

        obj.add("method", method)
        method.addProperty("code", items[checkedPosition].code)
        method.addProperty("title", items[checkedPosition].title)
        method.addProperty("cost", items[checkedPosition].cost)
        method.addProperty("tax_class_id", items[checkedPosition].taxClassId)
        method.addProperty("text", items[checkedPosition].text)

        return obj
    }

    var items: MutableList<ShippingMethodsModel.Data.ShippingMethod> by Delegates.observable(
        mutableListOf()
    ) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_shipping_methods, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)
        holder.cvRoot.setOnClickListener {
            onclick = true
            holder.setClick(true, item)
        }

        holder.title.tag = position

        /*if (!onclick) if (position == 0) {
            holder.setClick(false, item)
        }*/

        if (lastCheckedPosition == position) {
            holder.onSelected()
        } else {
            holder.onDeselected()
        }
    }

    override fun getItemCount() = items.size

    fun getCheckedPosition(): Int {
        return lastCheckedPosition
    }

    interface ShippingMethodBridge {
        fun onSelectStore(adapterPosition: Int)
        fun onDeselectStore()
        fun onSelectMethod()
    }
}