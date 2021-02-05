package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.DeliverySlotModel
import com.ecommerce.shopmitt.models.SlotTitleModel
import kotlin.properties.Delegates

class TimeSlotAdapter(val onItemClicked: (SlotTitleModel) -> Unit, val context: Context) :
    RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {

    private val tabObject: TimeSlotBridge = context as TimeSlotBridge
    private var lastCheckedPosition = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val root: View = view.findViewById(R.id.root)
        val title: TextView = view.findViewById(R.id.title)
        val day: TextView = view.findViewById(R.id.day)

        fun bind(item: SlotTitleModel, position: Int) {
            title.text = item.title
            day.text = item.date
        }

        fun onSelected(item: SlotTitleModel) {
            root.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorBlue))
            tabObject.onSelectTab(adapterPosition)
        }

        fun onDeselected(item: SlotTitleModel) {
            root.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorWhite))
        }
    }

    var items: MutableList<SlotTitleModel> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.row_timeslot_title_lyt, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item,position)

        holder.root.setOnClickListener {
            lastCheckedPosition = position
            notifyDataSetChanged()
        }

        if (lastCheckedPosition == position) {
            holder.onSelected(item)
        } else {
            holder.onDeselected(item)
        }
    }

    fun selectFirstPos() {
        lastCheckedPosition = 0
        notifyDataSetChanged()
    }


    override fun getItemCount() = items.size

    interface TimeSlotBridge {
        fun onSelectTab(position: Int)
    }
}