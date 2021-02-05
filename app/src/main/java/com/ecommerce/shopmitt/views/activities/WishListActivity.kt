package com.ecommerce.shopmitt.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityWishListBinding
import com.ecommerce.shopmitt.models.WishListProductsModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.WishListProductsAdapter
import io.reactivex.disposables.Disposable

class WishListActivity : BaseActivity() {

    private var disposableWishList: Disposable? = null
    
    private lateinit var binding: ActivityWishListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("WishList / Shopping List")

    }

    override fun onResume() {
        super.onResume()
        callWishListedProducts()
    }

    private fun callWishListedProducts() {

        showLoadingDialog()
        disposableWishList = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as WishListProductsModel
                handleProducts(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getWishListedProducts()
    }

    private fun handleProducts(data: List<WishListProductsModel.Data>) {
        
        if (data.isEmpty()) {
            binding.tvError.visibility = View.VISIBLE
        } else {

            val adapter = WishListProductsAdapter(onItemClicked = {
                val intent = Intent(this, DetailsActivity::class.java)
                intent.putExtra("product_id", it.productID)
                startActivity(intent)
            })
            adapter.items = data as MutableList<WishListProductsModel.Data>

            binding.rvWishList.layoutManager = GridLayoutManager(this,2)
            binding.rvWishList.adapter = adapter
            binding.rvWishList.isNestedScrollingEnabled = false
            
        }
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableWishList)
    }
}