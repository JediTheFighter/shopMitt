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
import com.google.gson.JsonObject
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.models.PaymentMethodsModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.PaymentActivity
import com.google.android.material.card.MaterialCardView
import kotlin.properties.Delegates

class PaymentMethodsAdapter(
    val onItemClicked: (PaymentMethodsModel.Data.PaymentMethod) -> Unit,
    val data: PaymentMethodsModel.Data,
    val context: Context
) :
    RecyclerView.Adapter<PaymentMethodsAdapter.ViewHolder>() {

    private var lastCheckedPosition = -1
    private var onclick = false
    private var selectedPaymentMethod: String? = null


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.title)
        val image: ImageView = view.findViewById(R.id.img)
        val cvRoot: MaterialCardView = view.findViewById(R.id.cv_root)

        fun setClick(b: Boolean) {
            lastCheckedPosition = title.tag as Int

            selectedPaymentMethod = items[lastCheckedPosition].code
            callSetPaymentMethod(lastCheckedPosition)
            if (b)
                notifyDataSetChanged()
        }

        fun bind(item: PaymentMethodsModel.Data.PaymentMethod) {
            title.text = item.title

            Glide.with(context)
                .load(item.image)
                .centerCrop()
                .into(image)
        }

        fun onSelected() {
            cvRoot.strokeColor = ContextCompat.getColor(context,R.color.colorPrimary)
        }

        fun onDeselected() {
            cvRoot.strokeColor = ContextCompat.getColor(context,R.color.cardViewStroke)
        }
    }

    private fun callSetPaymentMethod(lastCheckedPosition: Int) {
        (context as PaymentActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (context as PaymentActivity).hideLoadingDialog()
                val model = `object` as BaseModel
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (context as PaymentActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, context).setPaymentMethod(getPaymentMethod(lastCheckedPosition))
    }

    fun getSelectedPaymentMethod(): String? {
        return selectedPaymentMethod
    }

    private fun getPaymentMethod(lastCheckedPosition: Int): JsonObject {
        val obj = JsonObject()
        val method = JsonObject()
        obj.addProperty("payment_method", items[lastCheckedPosition].code)
        obj.addProperty("agree", "1")
        obj.addProperty("comment", data.comment)
        obj.add("method", method)
        method.addProperty("code", items[lastCheckedPosition].code)
        method.addProperty("title", items[lastCheckedPosition].title)
        method.addProperty("terms", items[lastCheckedPosition].terms)
        method.addProperty("sort_order", items[lastCheckedPosition].sortOrder)
        return obj
    }

    var items: MutableList<PaymentMethodsModel.Data.PaymentMethod> by Delegates.observable(
        mutableListOf()
    ) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_payment_methods, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)
        holder.title.tag = position

        /*if (!onclick) if (position == 0) {
            holder.setClick(false)
        }*/

        holder.cvRoot.setOnClickListener {
            onclick = true
            holder.setClick(onclick)
        }

        if (lastCheckedPosition == position) {
            holder.onSelected()
        } else {
            holder.onDeselected()
        }
    }

    override fun getItemCount() = items.size
}