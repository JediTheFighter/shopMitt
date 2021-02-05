package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.models.DetailsModel
import com.ecommerce.shopmitt.utils.CircularTextView

class SizeOptionsAdapter(
    val context: Context,
    val list: ArrayList<DetailsModel.Data.Option.OptionValue>,
    val optionPos: Int
) : RecyclerView.Adapter<SizeOptionsAdapter.ViewHolder>() {

    var sizeSelected = context as SizeSelected
    var selectedItemPos = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val optionText: CircularTextView = view.findViewById(R.id.option_text)
        val root: LinearLayout = view.findViewById(R.id.root)

        fun bind(item: DetailsModel.Data.Option.OptionValue) {
            optionText.text = item.name
        }

        fun onClickedText(item: DetailsModel.Data.Option.OptionValue) {
            optionText.text = item.name
        }

        fun onSelectedMode(item: DetailsModel.Data.Option.OptionValue) {
            optionText.setTextColor(ContextCompat.getColor(itemView.context, R.color.quantum_white_100))
            optionText.setStrokeWidth(2)
            optionText.solidColor = ContextCompat.getColor(context,R.color.optionSelected)
            optionText.setStrokeColor(ContextCompat.getColor(context,R.color.optionSelected))
        }

        fun onDeselectedMode(item: DetailsModel.Data.Option.OptionValue) {
            optionText.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorBlack))
            optionText.setStrokeWidth(2)
            optionText.solidColor = ContextCompat.getColor(context,R.color.colorWhite)
            optionText.setStrokeColor(ContextCompat.getColor(context,R.color.optionSelected))
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_size_option, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

        holder.optionText.setOnClickListener {
            holder.onClickedText(item)
            selectedItemPos = position
            sizeSelected.onSizeSelected(optionPos, position)
            notifyDataSetChanged()
        }

        if (position == selectedItemPos) {
            holder.onSelectedMode(item)
        } else {
            holder.onDeselectedMode(item)
        }
    }

    override fun getItemCount() = list.size

    fun setFirstItem() {
        selectedItemPos = 0
        notifyDataSetChanged()
    }

    interface SizeSelected {
        fun onSizeSelected(optionPos: Int, optionValPos: Int)
    }
}