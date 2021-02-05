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
import com.ecommerce.shopmitt.models.SubCategoryModel
import kotlin.properties.Delegates

class SubCategoryAdapter(val onItemClicked: (SubCategoryModel.SubCategory) -> Unit) :
    RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.sub_cat_title)
        val image: ImageView = view.findViewById(R.id.sub_cat_img)
        val root: View = view.findViewById(R.id.root)
    }

    var items: MutableList<SubCategoryModel.SubCategory> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_sub_category_item, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.name

        Glide.with(holder.itemView.context)
            .load(item.originalImage)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .into(holder.image)

        holder.root.setOnClickListener {
            onItemClicked(item)
        }
    }

    override fun getItemCount() = items.size
}