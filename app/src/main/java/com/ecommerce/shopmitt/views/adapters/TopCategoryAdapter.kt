package com.ecommerce.shopmitt.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.CategoryModel
import kotlin.properties.Delegates

class TopCategoryAdapter(val onItemClicked: (CategoryModel.Data) -> Unit) :
    RecyclerView.Adapter<TopCategoryAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val tvTitle: TextView = view.findViewById(R.id.cat_title)
        val img: ImageView = view.findViewById(R.id.cat_img)
        val root: View = view.findViewById(R.id.root)
    }

    var items: MutableList<CategoryModel.Data> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_top_category, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]

        Glide.with(holder.itemView.context)
            .load(item.originalImage)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .into(holder.img)

        holder.tvTitle.text = item.name?.replace("&amp;","&")

        holder.root.setOnClickListener {
            onItemClicked(item)
        }
    }

    override fun getItemCount() = items.size
}