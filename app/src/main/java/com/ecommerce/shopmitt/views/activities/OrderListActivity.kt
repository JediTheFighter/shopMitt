package com.ecommerce.shopmitt.views.activities

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityOrderListBinding
import com.ecommerce.shopmitt.models.OrdersModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.OrdersAdapter
import io.reactivex.disposables.Disposable

class OrderListActivity : BaseActivity() {

    private var disposableOrders: Disposable? = null

    private lateinit var binding: ActivityOrderListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("My Orders")
    }

    override fun onResume() {
        super.onResume()
        callOrdersList()
    }

    private fun callOrdersList() {

        showLoadingDialog()
        disposableOrders = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as OrdersModel
                handleOrders(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getMyOrders()
    }

    private fun handleOrders(data: List<OrdersModel.Data>) {

        if (data.isNotEmpty()) {
            val adapter = OrdersAdapter(onItemClicked = { model: OrdersModel.Data ->


            }, this)

            adapter.items = data as ArrayList<OrdersModel.Data>

            val layoutManager = LinearLayoutManager(this)
            binding.rvOrderList.layoutManager = layoutManager
            binding.rvOrderList.adapter = adapter
            binding.rvOrderList.isNestedScrollingEnabled = false
        } else {
            binding.tvError.visibility = View.VISIBLE
        }
    }


    override val fragmentContainer: Int
        get() = 0
}