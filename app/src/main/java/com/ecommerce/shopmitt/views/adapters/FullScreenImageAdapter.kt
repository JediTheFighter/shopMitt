package com.ecommerce.shopmitt.views.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.utils.TouchImageView

class FullScreenImageAdapter(
    val _activity: Activity, val _imageURL: String
): PagerAdapter() {


    private lateinit var inflater: LayoutInflater

    override fun getCount() = 1

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imgDisplay: TouchImageView
        val btnClose: Button

        inflater = _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var viewLayout: View? = null
        if (inflater != null) {
            viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false)
        }

        imgDisplay = viewLayout!!.findViewById<View>(R.id.imgDisplay) as TouchImageView
        btnClose = viewLayout!!.findViewById<View>(R.id.btnClose) as Button

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
//        imgDisplay.setImageBitmap(bitmap);


//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
//        imgDisplay.setImageBitmap(bitmap);
        Glide.with(_activity)
            .load(_imageURL)
            .fitCenter()
            .into(imgDisplay)

        // close button click event

        // close button click event
        btnClose.setOnClickListener { _activity.finish() }

        (container as ViewPager).addView(viewLayout)

        return viewLayout!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as RelativeLayout)
    }
}