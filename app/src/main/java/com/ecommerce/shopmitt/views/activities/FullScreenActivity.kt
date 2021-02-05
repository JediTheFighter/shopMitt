package com.ecommerce.shopmitt.views.activities

import android.os.Bundle
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityFullScreenBinding
import com.ecommerce.shopmitt.views.adapters.FullScreenImageAdapter

class FullScreenActivity : BaseActivity() {

    private lateinit var binding: ActivityFullScreenBinding

    private lateinit var adapter: FullScreenImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        val img = i.getStringExtra("image")!!

        adapter = FullScreenImageAdapter(this, img)
        binding.pager.adapter = adapter

        // displaying selected image first
        binding.pager.currentItem = 0
    }

    override val fragmentContainer: Int
        get() = 0
}