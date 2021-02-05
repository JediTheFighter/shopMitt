package com.ecommerce.shopmitt.views.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.ReferralOffersModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.properties.Delegates

class ReferralOffersAdapter(val onItemClicked: (ReferralOffersModel.RewardOffers) -> Unit) :
    RecyclerView.Adapter<ReferralOffersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvOrderId: TextView = view.findViewById(R.id.tv_refer_offer)
        val tilRef: TextInputLayout = view.findViewById(R.id.til_ref)
        val edtVoucherCode: TextInputEditText = view.findViewById(R.id.edt_vouchercode)

        fun bind(item: ReferralOffersModel.RewardOffers) {
            tvOrderId.text = item.statement

            if (item.code != null) {
                tilRef.visibility = View.VISIBLE
            }
        }

    }

    var items: MutableList<ReferralOffersModel.RewardOffers> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_referral_item, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)

        holder.edtVoucherCode.setOnClickListener {
            val text: String? = item.code
            if (!text.isNullOrEmpty()) {
                val clipData = ClipData.newPlainText("key", text)
                val clipBoardManager: ClipboardManager = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipBoardManager.setPrimaryClip(clipData)
                Toast.makeText(holder.itemView.context, "Coupon code " + text.toString() + " has been copied to clipboard.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "Coupon code unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = items.size
}