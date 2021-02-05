package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.views.activities.FullScreenActivity

class ProductImagesAdapter(val context: Context, val originalImages: List<String>): PagerAdapter() {

    var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    lateinit var currImg: ImageView

    override fun getCount() = originalImages.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View =
            mLayoutInflater.inflate(R.layout.banner_detail_layout, container, false)
        val imageView = itemView.findViewById<View>(R.id.img_banner) as ImageView
        currImg = imageView

        if (originalImages.isNotEmpty()) {
            Glide.with(context)
                .load(originalImages[position])
                .centerCrop()
                .into(imageView)
            imageView.setOnClickListener {
                val i = Intent(context, FullScreenActivity::class.java)
                i.putExtra("image", originalImages[position])
                context.startActivity(i)
            }
        } else {
            imageView.setImageResource(R.drawable.app_logo)
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

    fun getCurrentImage(): ImageView {
        return currImg
    }
}