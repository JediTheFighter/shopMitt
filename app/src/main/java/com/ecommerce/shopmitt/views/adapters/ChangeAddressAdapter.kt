package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.ShippingAddressModel
import com.ecommerce.shopmitt.views.activities.ChangeAddressActivity
import com.ecommerce.shopmitt.views.fragments.EditAddressDialogFragment
import kotlin.properties.Delegates

class ChangeAddressAdapter(
    val onItemClicked: (ShippingAddressModel.Data.Address) -> Unit,
    val context: Context,
    val def_id: String
) : RecyclerView.Adapter<ChangeAddressAdapter.ViewHolder>() {

    private val addressBridge: AddressBridge = context as AddressBridge

    private var lastCheckedPosition = -1

    private var data: ShippingAddressModel.Data.Address? = null
    private var address_id: String? = null
    private var isFirstTime = true

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rdBtn: RadioButton = view.findViewById(R.id.rd_btn)
        val imgDeleteItem: ImageView = view.findViewById(R.id.img_delete_item)
        val imgEditItem: ImageView = view.findViewById(R.id.img_edit_item)

        fun bind(item: ShippingAddressModel.Data.Address) {
            data = item
            rdBtn.text = getAddress(data!!)
            address_id = data?.addressId
        }

        fun onEditAddress(item: ShippingAddressModel.Data.Address, position: Int) {
            val bundle = Bundle()
            bundle.putString("name",item.firstname)
            bundle.putString("mobile",item.telephone)
            bundle.putString("pincode",item.postcode)
            bundle.putString("address",item.address1)
            bundle.putString("city",item.city)
            bundle.putString("address_id",item.addressId)
            bundle.putString("prev_address",item.prevAddress)
            bundle.putString("latitude",item.latitude)
            bundle.putString("longitude",item.longitude)
            EditAddressDialogFragment.newInstance(bundle).show((context as ChangeAddressActivity).supportFragmentManager,"edit")
        }

        fun onDeleteItem(item: ShippingAddressModel.Data.Address, position: Int) {
            addressBridge.onDeleteAddress(item.addressId)
        }

        fun onSetAddress(item: ShippingAddressModel.Data.Address) {
            addressBridge.onSetAddress(item.addressId,adapterPosition)
        }

    }

    private fun getAddress(data: ShippingAddressModel.Data.Address): String {
        val sb = StringBuilder("")

        sb.append(data.firstname)
        sb.append("\n")
        sb.append(data.address1)
        sb.append("\n")
        sb.append(data.city)
        sb.append(" - ")
        sb.append(data.postcode)
        sb.append("\n")
        sb.append(data.zone)
        sb.append(", ")
        sb.append(data.country)
        sb.append("\n")
        sb.toString()

        return sb.toString()
    }

    var items: MutableList<ShippingAddressModel.Data.Address> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_change_address, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.imgDeleteItem.visibility = if (items.size > 1) View.VISIBLE else View.INVISIBLE

        holder.bind(item)
        holder.rdBtn.isChecked = position == lastCheckedPosition

        if (def_id == items[position].addressId) {
            if (isFirstTime) {

                lastCheckedPosition = position
                holder.rdBtn.isChecked = true
                isFirstTime = false
            }
        }

        holder.imgEditItem.setOnClickListener {
            holder.onEditAddress(item,position)
        }

        holder.imgDeleteItem.setOnClickListener {
            holder.onDeleteItem(item,position)
        }

        holder.rdBtn.setOnClickListener {
            holder.onSetAddress(item)
        }
    }

    override fun getItemCount() = items.size

    interface AddressBridge {
        fun onDeleteAddress(addressId: String?)
        fun onSetAddress(addressId: String?, adapterPosition: Int)
    }
}