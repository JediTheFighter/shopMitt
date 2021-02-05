package com.ecommerce.shopmitt.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.activity.CountMenuActivity
import com.ecommerce.shopmitt.databinding.ActivityProductListBinding
import com.ecommerce.shopmitt.db.AppDatabase
import com.ecommerce.shopmitt.db.dao.CartDao
import com.ecommerce.shopmitt.models.ProductModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.ProductsAdapter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class ProductListActivity : CountMenuActivity(),ProductsAdapter.ItemUpdate {

    private lateinit var adapter: ProductsAdapter

    private var disposableProducts: Disposable? = null

    private lateinit var binding: ActivityProductListBinding

    private lateinit var cartDao: CartDao

    private var isApiCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartDao = AppDatabase.getDatabase(this).cartDao()

        getExtrasData()
    }

    private fun getExtrasData() {
        if (intent.hasExtra("from_featured")) {
            val model = intent.getParcelableArrayListExtra<ProductModel.Data>("data")
            setToolbar("Featured Products")
            handleProductsFromIntent(model)
        } else {
            val sub_cat_id = intent.getStringExtra("sub_category_id")
            val sub_cat_name = intent.getStringExtra("sub_category_name")

            setToolbar(sub_cat_name?.replace("&amp;","&")!!)

            callProducts(sub_cat_id!!)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isApiCalled) {
            invalidateOptionsMenu()
            adapter.notifyDataSetChanged()
        }
    }

    private fun callProducts(subCatId: String) {

        showLoadingDialog()
        disposableProducts = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ProductModel
                isApiCalled = true
                if (model.data.isEmpty()) {
                    binding.tvNoProducts.visibility = View.VISIBLE
                } else {
                    handleProducts(model)
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getProductsByCategory(subCatId)
    }

    private fun handleProducts(model: ProductModel) {

        adapter = ProductsAdapter(onItemClicked = {
            val intent = Intent(this,DetailsActivity::class.java)
            intent.putExtra("product_id",it.productId)
            startActivity(intent)
        },this)

        adapter.items = model.data as MutableList<ProductModel.Data>

        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.isNestedScrollingEnabled = false


        if(adapter.itemCount == 0) {
            binding.tvNoProducts.visibility = View.VISIBLE
        }
    }

    private fun handleProductsFromIntent(model: ArrayList<ProductModel.Data>?) {

        val adapter = ProductsAdapter(onItemClicked = {
            val intent = Intent(this,DetailsActivity::class.java)
            intent.putExtra("product_id",it.productId)
            startActivity(intent)
        },this)

        adapter.items = model as MutableList<ProductModel.Data>

        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.isNestedScrollingEnabled = false

        if(adapter.itemCount == 0) {
            binding.tvNoProducts.visibility = View.VISIBLE
        }
    }

    override suspend fun getCartItemCount(): Int {
        return cartDao.getTotalProductCount()
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableProducts)
    }

    override fun onUpdate() {
        Handler().postDelayed({
            invalidateOptionsMenu()
        },500)
    }
}