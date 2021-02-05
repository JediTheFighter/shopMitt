package com.ecommerce.shopmitt.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.DeliverySlotModel
import kotlin.properties.Delegates

class DeliveryTimeSlotAdapter(val onItemClicked: (DeliverySlotModel.Data.Date.TimeSlot) -> Unit) :
    RecyclerView.Adapter<DeliveryTimeSlotAdapter.ViewHolder>() {

    private var lastCheckedPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rdTimeSlot: RadioButton = view.findViewById(R.id.time_slot)

        fun bind(item: DeliverySlotModel.Data.Date.TimeSlot, position: Int) {
            rdTimeSlot.text = item.timeSlot
        }
    }

    var items: MutableList<DeliverySlotModel.Data.Date.TimeSlot> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.row_timeslot, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item, position)

        holder.rdTimeSlot.isChecked = lastCheckedPosition == position

        holder.rdTimeSlot.setOnClickListener {
            lastCheckedPosition = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = items.size

    fun getTimeSlot(): String? {
        return if (lastCheckedPosition == -1) null else items[lastCheckedPosition].timeSlot
    }

}