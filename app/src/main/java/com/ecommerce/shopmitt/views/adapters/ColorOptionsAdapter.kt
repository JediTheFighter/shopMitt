package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.models.DetailsModel

class ColorOptionsAdapter(
    val context: Context,
    val list: ArrayList<DetailsModel.Data.Option.OptionValue>,
    val optionPos: Int
): RecyclerView.Adapter<ColorOptionsAdapter.ViewHolder>() {

    private var colorSelected = context as ColorSelected
    private var selectedItemPos = -1

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val selectionBorder: LinearLayout = view.findViewById(R.id.selection_border)
        val colorBlock: ImageView = view.findViewById(R.id.color_block)

        fun bind(item: DetailsModel.Data.Option.OptionValue) {

            Glide.with(itemView.context).load(item.image).centerCrop().into(colorBlock)
        }

        fun itemSelected() {
            selectionBorder.background = ContextCompat.getDrawable(itemView.context,R.drawable.circle_border_selected)
        }

        fun itemDeSelected() {
            selectionBorder.background = ContextCompat.getDrawable(itemView.context,R.drawable.circle_border_not_selected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_color_option, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.bind(item)

        if (position == selectedItemPos) {
            holder.itemSelected()
        } else {
            holder.itemDeSelected()
        }

        holder.colorBlock.setOnClickListener {
            colorSelected.onColorSelected(optionPos,position)
            selectedItemPos = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = list.size

    fun setFirstItem() {
        selectedItemPos = 0
        notifyDataSetChanged()
    }

    interface ColorSelected {
        fun onColorSelected(optionPos: Int,optionValPos: Int)
    }
}