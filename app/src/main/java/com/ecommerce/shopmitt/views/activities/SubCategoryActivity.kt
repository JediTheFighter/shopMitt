package com.ecommerce.shopmitt.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivitySubCategoryBinding
import com.ecommerce.shopmitt.models.SubCategoryModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.SubCategoryAdapter
import io.reactivex.disposables.Disposable

class SubCategoryActivity : BaseActivity() {

    private lateinit var subCatAdapter: SubCategoryAdapter

    private var disposableSubCategories: Disposable? = null

    private lateinit var binding: ActivitySubCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getExtrasData()

    }

    private fun getExtrasData() {
        val catName = intent.getStringExtra("category_name")
        val catID = intent.getStringExtra("category_id")

        setToolbar(catName?.replace("&amp;","&")!!)

        callSubCategories(catID!!)
    }

    private fun callSubCategories(catID: String) {
        showLoadingDialog()
        disposableSubCategories = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as SubCategoryModel
                handleSubCategories(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getCategoryById(catID)
    }

    private fun handleSubCategories(model: SubCategoryModel) {

        val list = model.data?.subcategories

        subCatAdapter = SubCategoryAdapter(onItemClicked = {

            if (it.productType == "true") {
                val intent = Intent(this, ProductListActivity::class.java)
                intent.putExtra("sub_category_name", it.name)
                intent.putExtra("sub_category_id", it.subCategoryId)
                startActivity(intent)
            } else {
                val intent = Intent(this, SubCategoryActivity::class.java)
                intent.putExtra("category_id", it.subCategoryId)
                intent.putExtra("category_name", it.name)
                startActivity(intent)
            }
        })

        subCatAdapter.items = list as ArrayList<SubCategoryModel.SubCategory>

        val layoutManager = GridLayoutManager(this,2)
        binding.rvSubCategories.layoutManager = layoutManager
        binding.rvSubCategories.adapter = subCatAdapter
        binding.rvSubCategories.isNestedScrollingEnabled = false

        if (list.isEmpty()) {
            binding.tvNoCategories.visibility = View.VISIBLE
        }
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableSubCategories)
    }
}