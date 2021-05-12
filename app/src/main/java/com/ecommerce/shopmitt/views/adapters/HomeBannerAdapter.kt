package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.models.BannerModel
import com.ecommerce.shopmitt.views.activities.DetailsActivity
import com.ecommerce.shopmitt.views.activities.ProductListActivity

class HomeBannerAdapter(val context: Context, var pics: List<BannerModel.Data>) : PagerAdapter() {

    var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount() = pics.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = mLayoutInflater.inflate(R.layout.banner_layout, container, false)
        val imageView = itemView.findViewById<View>(R.id.img_banner) as ImageView

        if (pics.isNotEmpty()) {
            Glide.with(context)
                .load(pics[position].image)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.app_logo)
        }
        itemView.setOnClickListener {
            var intent: Intent? = null
            intent = if (pics[position].type == "category" || pics[position].type == "2")
                Intent(context, ProductListActivity::class.java)
            else
                Intent(context, DetailsActivity::class.java)
            intent.putExtra("title", pics[position].title)
            intent.putExtra("id", pics[position].link)
            intent.putExtra("product_id",pics[position].link)
            intent.putExtra("sub_category_name",pics[position].title)
            intent.putExtra("sub_category_id",pics[position].link)
            context.startActivity(intent)
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout?)
    }
}