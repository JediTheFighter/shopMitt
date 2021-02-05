package com.ecommerce.shopmitt.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.BannerModel
import kotlin.properties.Delegates

class SquareBannerAdapter(val onItemClicked: (BannerModel.Data) -> Unit) :
    RecyclerView.Adapter<SquareBannerAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val img: ImageView = view.findViewById(R.id.image)
        val root: View = view.findViewById(R.id.root)
    }

    var items: MutableList<BannerModel.Data> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_square_banner, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        Glide.with(holder.itemView.context)
            .load(item.image)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .into(holder.img)

        holder.root.setOnClickListener {
            onItemClicked(item)
        }
    }

    override fun getItemCount() = items.size
}