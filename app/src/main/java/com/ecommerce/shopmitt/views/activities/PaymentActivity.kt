package com.ecommerce.shopmitt.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.MainActivity
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.dialog.*

import com.ecommerce.shopmitt.databinding.ActivityPaymentBinding
import com.ecommerce.shopmitt.db.AppDatabase
import com.ecommerce.shopmitt.db.dao.CartDao
import com.ecommerce.shopmitt.models.*
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.DividerItemDecorator
import com.ecommerce.shopmitt.views.adapters.*
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaymentActivity : BaseActivity(), PaymentResultListener,ShippingMethodsAdapter.ShippingMethodBridge, StoreAdapter.StoreBridge {

    private var isStoreSelected: Boolean = false

    private var selectedStore: String = ""
    private var storePos: Int = 0

    private lateinit var paymentAdapter: PaymentMethodsAdapter

    private var disposableShippingMethods: Disposable? = null
    private var disposablePayment: Disposable? = null
    private var disposableConfirm: Disposable? = null

    private var date: String? = null
    private var timeSlot: String? = null

    private var lat_addr: String? = null
    private var lon_addr: String? = null

    private var isShippingMethodSet: Boolean = false
    private lateinit var shippingAdapter: ShippingMethodsAdapter

    private lateinit var cartDao: CartDao

    private lateinit var binding: ActivityPaymentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("Payment")

        cartDao = AppDatabase.getDatabase(this).cartDao()

        Checkout.preload(applicationContext)

        getShippingMethods()

        binding.checkout.setOnClickListener {
            callConfirmOrder()
        }

        getExtrasData()
    }

    private fun getExtrasData() {

        if (intent.hasExtra("date"))
            date = intent.getStringExtra("date")
        if (intent.hasExtra("time_slot"))
            timeSlot = intent.getStringExtra("time_slot")

        if (intent.hasExtra("lat"))
            lat_addr = intent.getStringExtra("lat")

        if (intent.hasExtra("lng"))
            lon_addr = intent.getStringExtra("lng")


    }

    private fun callCartTotals() {
        showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()

                val model = `object` as ViewCart
                handleCartTotals(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getCartTotals()
    }

    private fun handleCartTotals(data: ViewCart.Data?) {
        val list = data?.totals as ArrayList<ViewCart.Data.Total>
        setBillsAdapter(list)
    }

    private fun setBillsAdapter(totals: ArrayList<ViewCart.Data.Total>) {
        val adapter = CartBillAdapter(this,totals)

        binding.rvBills.layoutManager = LinearLayoutManager(this)
        binding.rvBills.adapter = adapter
        binding.rvBills.isNestedScrollingEnabled = false

        val line = ContextCompat.getDrawable(this,R.drawable.line_divider)
        binding.rvBills.addItemDecoration(DividerItemDecorator(line))
    }

    private fun getShippingMethods() {
        showLoadingDialog()
        disposableShippingMethods = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ShippingMethodsModel

                if (model.data != null)
                    handleShippingMethods(model.data?.shippingMethods!!)

                callPaymentMethods()
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getShippingMethods()
    }

    private fun handleShippingMethods(shippingMethods: List<ShippingMethodsModel.Data.ShippingMethod>) {

        isShippingMethodSet = true
        binding.shippingMethodTitle.visibility = View.VISIBLE

        shippingAdapter = ShippingMethodsAdapter(onItemClicked = { model: ShippingMethodsModel.Data.ShippingMethod ->


        }, this)

        shippingAdapter.items = shippingMethods as MutableList<ShippingMethodsModel.Data.ShippingMethod>

        val layoutManager = LinearLayoutManager(this)
        binding.rvShippingMethods.layoutManager = layoutManager
        binding.rvShippingMethods.adapter = shippingAdapter
        binding.rvShippingMethods.isNestedScrollingEnabled = false
    }

    private fun callConfirmOrder() {
        showLoadingDialog()
        disposableConfirm = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()

                val model = `object` as ConfirmOrderModel
                handleConfirm(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).confirmOrder(getConfirmParams())
    }

    private fun handleConfirm(data: ConfirmOrderModel.Data?) {

        if (paymentAdapter.getSelectedPaymentMethod()?.toLowerCase(Locale.ENGLISH)?.contains("razorpay")!!) {
            val size = data?.totals?.size!!
            callRazorPayApi(data.totals[size - 1].total_amount.toString(), data.orderId!!)
        } else
            callConfirmPUT()
    }

    private fun callRazorPayApi(totalAmt: String, orderId: String) {
        var total_: Double = totalAmt.toDouble()
        total_ = total_ * 100
        val totalToSend = total_.toString()

        showLoadingDialog()
        val finalTotal_amount: String = totalToSend

        showLoadingDialog()
        disposableConfirm = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()

                val model = `object` as RazorPayModel
                startPayment(finalTotal_amount, model.id)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getRazorPayOrderId(totalAmt, orderId)
    }

    private fun getConfirmParams(): JsonObject {
        val body = JsonObject()

        val form = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        var dateParsed: Date? = null
        try {
            if (date != null) dateParsed = form.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        var newDateStr = ""
        val postFormater = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        if (dateParsed != null) newDateStr = postFormater.format(dateParsed)
        if (timeSlot == null) timeSlot = ""


        body.addProperty("date", newDateStr)
        body.addProperty("time_slot", timeSlot)
        body.addProperty("latitude", lat_addr)
        body.addProperty("longitude", lon_addr)

        if (isStoreSelected) {
            val store = "Pickup Shop Name : $selectedStore"
            body.addProperty("comment", store)
        }

        return body
    }

    private fun callConfirmPUT() {
        showLoadingDialog()
        disposableConfirm = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()

                val model = `object` as ConfirmOrderModel
                complete(model.data?.orderId)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).confirmPUT()
    }

    private fun complete(orderId: String?) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                withContext(Dispatchers.IO) {
                    cartDao.clearTable()
                }
            }
        }

        ActivityCompat.finishAffinity(this)
        startActivity(Intent(this, MainActivity::class.java))

        val intent = Intent(this, OrderActivity::class.java)
        intent.putExtra("order_id", orderId)
        intent.putExtra("order_status", "Confirmed")
        startActivity(intent)
    }

    private fun callPaymentMethods() {

        showLoadingDialog()
        disposablePayment = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()

                val model = `object` as PaymentMethodsModel

                if (model.data != null)
                    handlePaymentMethods(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getPaymentMethods()
    }

    private fun handlePaymentMethods(paymentMethods: PaymentMethodsModel.Data?) {

        binding.paymentMethodTitle.visibility = View.VISIBLE

        paymentAdapter = PaymentMethodsAdapter(onItemClicked = { model: PaymentMethodsModel.Data.PaymentMethod ->


        }, paymentMethods!!, this)

        paymentAdapter.items = paymentMethods.paymentMethods as MutableList<PaymentMethodsModel.Data.PaymentMethod>

        val layoutManager = LinearLayoutManager(this)
        binding.rvPaymentMethods.layoutManager = layoutManager
        binding.rvPaymentMethods.adapter = paymentAdapter
        binding.rvPaymentMethods.isNestedScrollingEnabled = false

        callCartTotals()
    }

    private fun startPayment(finalTotal_amount: String, orderId: String) {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()


        co.setImage(R.drawable.app_logo)

        try {
            val options = JSONObject()
            options.put("name", "ShopMitt")
            options.put("description", "Payment")
            options.put("key", "GDgLxRo3j7RS5V")

            options.put("currency", "INR")
            options.put("order_id", orderId)
            options.put("amount", finalTotal_amount)//pass amount in currency subunits


           /* val method = JSONObject()

            method.put("netbanking","true")
            method.put("card","true")
            method.put("upi","false")
            method.put("wallet","true")


            options.put("method",method)*/  // control which methods to show/hide

            /*val prefill = JSONObject()
            prefill.put("contact", AppDataManager.instance.userName)

            options.put("prefill", prefill)*/ // add if needed

            co.open(activity, options)
        }catch (e: Exception){
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableShippingMethods)
        disposeApiCall(disposablePayment)
        disposeApiCall(disposableConfirm)
    }

    override fun onPaymentSuccess(p0: String?) {
        callConfirmPUT()
    }

    override fun onPaymentError(i: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
        when (i) {
            Checkout.NETWORK_ERROR -> Log.i("RPAY", "Network Error")
            Checkout.INVALID_OPTIONS -> Log.i("RPAY", "Invalid Payment Options $p1")
            Checkout.PAYMENT_CANCELED -> Log.i("RPAY", "Payment cancelled")
            Checkout.TLS_ERROR -> Log.i("RPAY", "TLS Error")
        }
        showFailedDialog()
    }

    private fun showFailedDialog() {

        val params: DialogParams = Builder()
            .cancelable(false).dgType(DialogType.DG_POS_ONLY)
            .title("Transaction Failed")
            .message("Your transaction has failed. Please retry")
            .positive(getString(R.string.ok))
            .build()


        DialogHelper(this, params, object : DialogCallback {
            override fun onButtonPositive(dialogId: Int) {

            }

            override fun onButtonNegative(dialogId: Int) {

            }
        }).showDialog(true)
    }

    override fun onSelectStore(adapterPosition: Int) {
        binding.cvStores.visibility = View.VISIBLE
        binding.storeTitle.visibility = View.VISIBLE

        storePos = adapterPosition

        callGetStores()

        isStoreSelected = true
    }

    override fun onDeselectStore() {
        binding.cvStores.visibility = View.GONE
        binding.storeTitle.visibility = View.GONE

        isStoreSelected = false
    }

    override fun onSelectMethod() {
        callCartTotals()
    }

    override fun onClickStore(storeName: String) {
        selectedStore = storeName
        Log.i("TEst store",storeName + " is store")
    }

    private fun callGetStores() {
        showLoadingDialog()
        disposablePayment = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()

                val model = `object` as StoresModel

                handleStores(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getStores()
    }

    private fun handleStores(data: List<String>) {
        val adapter = StoreAdapter(onItemClicked = {

        },this)

        adapter.items = data as ArrayList<String>

        binding.rvStores.layoutManager = LinearLayoutManager(this)
        binding.rvStores.adapter = adapter
        binding.rvStores.isNestedScrollingEnabled = false

        val line = ContextCompat.getDrawable(this,R.drawable.line_divider)
        binding.rvStores.addItemDecoration(DividerItemDecorator(line))

        adapter.setFirstItem()
    }

}