package com.ecommerce.shopmitt.views.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivitySizeChartBinding

class SizeChartActivity : BaseActivity() {

    private lateinit var binding: ActivitySizeChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySizeChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getExtrasData()
    }

    private fun getExtrasData() {
        val image = intent.getStringExtra("url")

        Glide.with(this)
            .load(image)
            .fitCenter()
            .into(binding.imgDisplay)

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    override val fragmentContainer: Int
        get() = 0
}