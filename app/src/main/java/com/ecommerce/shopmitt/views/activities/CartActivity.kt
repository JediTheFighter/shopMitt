package com.ecommerce.shopmitt.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.dialog.*
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.ActivityCartBinding
import com.ecommerce.shopmitt.db.AppDatabase
import com.ecommerce.shopmitt.db.dao.CartDao
import com.ecommerce.shopmitt.models.ViewCart
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.CURRENCY
import com.ecommerce.shopmitt.utils.DividerItemDecorator
import com.ecommerce.shopmitt.views.adapters.CartAdapter
import com.ecommerce.shopmitt.views.adapters.CartBillAdapter
import com.ecommerce.shopmitt.views.fragments.ApplyCouponDialogFragment
import com.ecommerce.shopmitt.views.fragments.ApplyRewardDialogFragment
import com.ecommerce.shopmitt.views.fragments.ApplyVoucherDialogFragment
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

class CartActivity : BaseActivity(), CartAdapter.CartBridge,
    ApplyCouponDialogFragment.CouponBridge,
    ApplyVoucherDialogFragment.VoucherBridge,
    ApplyRewardDialogFragment.RewardBridge {

    private var bills: ArrayList<ViewCart.Data.Total> = arrayListOf()

    private var totalAmount: Double = 0.0
    private lateinit var cartAdapter: CartAdapter
    private lateinit var billAdapter: CartBillAdapter

    private var disposableCallCart: Disposable? = null
    private var disposableDropCart: Disposable? = null

    private lateinit var binding: ActivityCartBinding

    private lateinit var cartDao: CartDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("My Cart")

        cartDao = AppDatabase.getDatabase(this).cartDao()

        binding.selectDelivery.setOnClickListener {
            if (AppDataManager.instance.isLoggedIn) {
                moveToSlots()
            } else {
                getToast().show("You need to login to continue.")
            }
        }

        binding.headerLayout.setOnClickListener {

            if (binding.expandableLayout.isExpanded) {
                binding.expandableLayout.collapse()
                binding.expandIndicator.setImageResource(R.drawable.arrow_down)
            } else {
                binding.expandableLayout.expand()
                binding.expandIndicator.setImageResource(R.drawable.arrow_up)
            }
        }

        binding.applyCoupon.setOnClickListener {
            ApplyCouponDialogFragment.newInstance().show(supportFragmentManager, "apply_coupon")
        }

        binding.applyVoucher.setOnClickListener {
            ApplyVoucherDialogFragment.newInstance().show(supportFragmentManager, "apply_voucher")
        }

        binding.applyReward.setOnClickListener {
            ApplyRewardDialogFragment.newInstance().show(supportFragmentManager, "apply_voucher")
        }
    }

    private fun moveToSlots() {
        val intent = Intent(this, DeliverySlotActivity::class.java)
        intent.putExtra("count", cartAdapter.itemCount.toString())
        intent.putExtra("total", totalAmount.toString())
        intent.putParcelableArrayListExtra("bill", bills)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        getCartItems()
    }

    private fun getCartItems() {

        showLoadingDialog()
        disposableCallCart = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ViewCart

                if (model.data != null) {
                    setCartItems(model.data!!)
                    showCartItems()
                    if (intent.hasExtra("reorder")) {
                        addProductsByBulkToLocalDB(model.data!!)
                    }
                } else {

                    // cart empty
                    showEmptyCart()
                    clearLocalCart()

                }

            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).viewCart()


    }

    private fun addProductsByBulkToLocalDB(data: ViewCart.Data) {

        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                withContext(Dispatchers.IO) {
                    cartDao.addBulkProducts(data)
                }
            }
        }
    }

    private fun showEmptyCart() {
        binding.itemCount.visibility = View.GONE
        binding.llSummary.visibility = View.GONE
        binding.rlAmount.visibility = View.GONE
        binding.selectDelivery.visibility = View.GONE
        binding.nestedScroll.visibility = View.GONE
        binding.llGift.visibility = View.GONE

        binding.llEmptyCart.visibility = View.VISIBLE
    }

    private fun showCartItems() {
        binding.itemCount.visibility = View.VISIBLE
        binding.llSummary.visibility = View.VISIBLE
        binding.rlAmount.visibility = View.VISIBLE
        binding.selectDelivery.visibility = View.VISIBLE
        binding.nestedScroll.visibility = View.VISIBLE
        binding.llGift.visibility = View.VISIBLE

        binding.llEmptyCart.visibility = View.GONE
    }

    private fun setCartItems(data: ViewCart.Data) {

        if (data.totalProductCount == 1) {
            binding.itemCount.text = "Item (1)"
        } else {
            binding.itemCount.text = "Items (" + data.products?.size.toString() + ")"
        }

        setCartTotal(data)

        bills.clear()
        bills.addAll(data.totals)


        cartAdapter = CartAdapter(onItemClicked = { cartModel: ViewCart.Data.Product ->


        }, this)

        cartAdapter.items = data.products as ArrayList<ViewCart.Data.Product>

        val layoutManager = LinearLayoutManager(this)
        binding.rvCart.layoutManager = layoutManager
        binding.rvCart.adapter = cartAdapter
        binding.rvCart.isNestedScrollingEnabled = false

        /////// bill adapter

        billAdapter = CartBillAdapter(this, data.totals as ArrayList<ViewCart.Data.Total>)

        val layoutManagerBills = LinearLayoutManager(this)
        binding.rvBills.layoutManager = layoutManagerBills
        binding.rvBills.adapter = billAdapter
        binding.rvBills.isNestedScrollingEnabled = false

        val line = ContextCompat.getDrawable(this, R.drawable.line_divider)
        binding.rvBills.addItemDecoration(DividerItemDecorator(line))

        //setting msg
        if (!data.message.isNullOrEmpty()) {
            binding.tvShippingMsg.text = data.message
            binding.rlMsg.visibility = View.VISIBLE
        } else
            binding.rlMsg.visibility = View.GONE
    }

    private fun setCartTotal(data: ViewCart.Data) {
        totalAmount = data.totalRaw
        val bd = BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP)
        val totalAmount_new = bd.toDouble()
        binding.finalAmount.text = "$CURRENCY $totalAmount_new"
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        var drawable = menu.findItem(R.id.action_drop_cart).icon
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.quantum_white_100))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_drop_cart -> callDropCart()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun callDropCart() {
        val params: DialogParams = Builder()
            .cancelable(false).dgType(DialogType.DG_POS_NEG)
            .title(getString(R.string.clear_cart))
            .message(getString(R.string.are_you_sure_to_drop_cart))
            .positive(getString(R.string.yes))
            .negative(getString(R.string.no))
            .build()


        DialogHelper(this, params, object : DialogCallback {
            override fun onButtonPositive(dialogId: Int) {
                dropCart()
            }

            override fun onButtonNegative(dialogId: Int) {

            }
        }).showDialog(false)

    }

    private fun dropCart() {
        showLoadingDialog()
        disposableDropCart = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                clearLocalCartAndRestart()

            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).dropCart()
    }

    private fun clearLocalCartAndRestart() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                withContext(Dispatchers.IO) {
                    cartDao.clearTable()
                }
                AppDataManager.instance.clearCartCount()
                finish()
                startActivity(intent)
            }
        }
    }

    private fun clearLocalCart() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                withContext(Dispatchers.IO) {
                    cartDao.clearTable()
                }
            }
        }
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableDropCart)
        disposeApiCall(disposableCallCart)
    }

    override fun updateItemCount(itemCount: Int, productCount: Int, isAdded: Boolean) {
        if (productCount == 1) {

            binding.itemCount.text = "Item (1)"
        } else {
            binding.itemCount.text = "Items (" + productCount + ") "
        }
    }

    override fun onEmptyProducts() {
        clearLocalCartAndRestart()
    }

    override fun refreshCart() {
        getCartItems()
    }

    override fun onCouponApplied() {
        getCartItems()
    }

    override fun onVoucherApplied() {
        getCartItems()
    }

    override fun onRewardApplied() {
        getCartItems()
    }
}