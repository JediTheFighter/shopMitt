package com.ecommerce.shopmitt.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.MainActivity
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.dialog.*
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.ActivityOrderBinding
import com.ecommerce.shopmitt.models.EditableTimeSlotModel
import com.ecommerce.shopmitt.models.OrderDetailsModel
import com.ecommerce.shopmitt.models.RazorPayModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.OrderDetailsBillAdapter
import com.ecommerce.shopmitt.views.adapters.OrderDetailsProductAdapter
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import io.reactivex.disposables.Disposable
import org.joda.time.DateTimeComparator
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OrderActivity : BaseActivity(), PaymentResultListener {

    private var timeSlotPos: Int = 0

    private var totalAmount: Double = 0.0

    private lateinit var orderId: String

    private var disposableOrders: Disposable? = null

    private lateinit var binding: ActivityOrderBinding

    private var order_status_id: String? = null
    private var order_status: String? = null
    private var delivery_date: String? = null

    private var time_slot: String? = null
    private var prev_time_slot: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("Order Details")

        getExtrasData()

        Checkout.preload(applicationContext)

        binding.reOrder.setOnClickListener {
            callReOrder()
        }

        binding.payMoney.setOnClickListener {
            callRazorPayApi(totalAmount.toString(), orderId)
        }

        binding.cancelOrder.setOnClickListener {
            callCancelOrder()
        }

        initTimeSlotSpinner()
    }

    private fun initTimeSlotSpinner() {

        val services = arrayListOf("Select TimeSlot")

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.custom_drop_down, services
        )

        binding.timeSlotSpinner.adapter = adapter

        binding.timeSlotSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long,
            ) {
                timeSlotPos = position

                when (position) {
                    0 -> {

                    }
                    else -> {
                        showConfirmSlotDialog()
                    }
                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun showConfirmSlotDialog() {

        val params: DialogParams = Builder()
            .cancelable(false).dgType(DialogType.DG_POS_NEG)
            .title(getString(R.string.change_delivery_slot))
            .message(getString(R.string.do_you_want_to_continue))
            .positive(getString(R.string.yes))
            .negative(getString(R.string.no))
            .build()


        DialogHelper(this, params, object : DialogCallback {
            override fun onButtonPositive(dialogId: Int) {
                callChangeTimeSlot()
            }

            override fun onButtonNegative(dialogId: Int) {
                binding.timeSlotSpinner.setSelection(0)
            }
        }).showDialog(true)

    }

    private fun callChangeTimeSlot() {
        showLoadingDialog()
        disposableOrders = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel

                if (model.success == 1) {
                    getToast().show("Slot changed successfully.")
                    getOrderDetails(orderId)
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).changeOrderTimeSlot(getChangeTimeSlotParams())
    }

    private fun getChangeTimeSlotParams(): JsonObject {
        val body = JsonObject()
        body.addProperty("order_id", orderId)
        body.addProperty("time_slot", binding.timeSlotSpinner.selectedItem.toString())
        return body
    }

    private fun callCancelOrder() {
        showLoadingDialog()
        disposableOrders = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel

                if (model.success == 1) {
                    getToast().show("Your Order has been cancelled successfully.")
                    getOrderDetails(orderId)
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).cancelOrder(getCancelParams())
    }

    private fun getCancelParams(): JsonObject {
        val body = JsonObject()
        body.addProperty("order_id", orderId)
        body.addProperty("order_status_id", "7")
        return body
    }

    private fun callReOrder() {
        showLoadingDialog()
        disposableOrders = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    handleReorder()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).reOrder(orderId)
    }

    private fun handleReorder() {

        ActivityCompat.finishAffinity(this)
        startActivity(Intent(this, MainActivity::class.java))

        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra("reorder", true)
        startActivity(intent)
    }

    private fun getExtrasData() {

        orderId = intent.getStringExtra("order_id")!!

        getOrderDetails(orderId)
    }

    private fun getOrderDetails(orderId: String) {
        showLoadingDialog()
        disposableOrders = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as OrderDetailsModel
                handleOrderDetails(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getOrderDetails(orderId)
    }

    private fun handleOrderDetails(data: OrderDetailsModel.Data?) {

        binding.orderId.text = "Order ID: #" + data?.orderId
        binding.paymentMethod.text = "Payment Method: " + data?.paymentMethod
        binding.placedOn.text = "Placed On: " + data?.dateAdded
        binding.timeSlot.text = "Time Slot: " + data?.time_slot
        binding.deliveryDate.text = "Delivery Date: " + data?.delivery_date
        binding.orderStatus.text = "Order Status: " + data?.order_status_final

        if (data?.comment != null) {
            binding.storeName.visibility = View.VISIBLE
            binding.storeName.text = data.comment
        }

        time_slot = data?.time_slot

        binding.reOrder.visibility = View.VISIBLE

        order_status_id = data?.orderStatusId
        order_status = data?.order_status_final
        delivery_date = data?.delivery_date

        totalAmount = data?.total?.toDouble()!!

        if (data?.paymentMethod == "Cash On Delivery") {
            binding.payMoney.visibility = View.VISIBLE
        }

        handleSlotVisibility(data?.delivery_date_only)

        handleVisibility()

        setProductsAdapter(data?.products)
        setBillsAdapter(data?.totals)
    }

    private fun handleSlotVisibility(deliveryDateOnly: String?) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val date = cal.time

        var del_date: Date? = null
        try {
            del_date = dateFormat.parse(deliveryDateOnly)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (time_slot?.isEmpty()!!) {
            binding.cancelOrder.visibility = View.GONE
            binding.llTimeSlot.visibility = View.GONE
        }

        val dateTimeComparator: DateTimeComparator = DateTimeComparator.getDateOnlyInstance()

        val dateReturn = dateTimeComparator.compare(date, del_date)

        // comparing below dates for hiding cancel btn and delivery slots.
        if (dateReturn > 0) { // delivery date gone past
            binding.cancelOrder.visibility = View.GONE
            binding.llTimeSlot.visibility = View.GONE
        } else if (dateReturn < 0) { // delivery date is greater
            callDeliverySlots()
        } else { // both are equal
            binding.cancelOrder.visibility = View.VISIBLE
            binding.llTimeSlot.visibility = View.VISIBLE
            callDeliverySlots()
        }
    }

    private fun callDeliverySlots() {
        showLoadingDialog()
        disposableOrders = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as EditableTimeSlotModel
                handleEditableTimeSlots(model.data.timeSlots)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getEditableTimeSlots(delivery_date!!)
    }

    private fun handleEditableTimeSlots(timeSlots: List<EditableTimeSlotModel.Data.TimeSlot>) {

        when(order_status_id) {
            "5" -> {
                binding.llTimeSlot.visibility = View.GONE
            }
            "7" -> {
                binding.llTimeSlot.visibility = View.GONE
            }
            "11" -> {
                binding.llTimeSlot.visibility = View.GONE
            }
            "12" -> {
                binding.llTimeSlot.visibility = View.GONE
            }
            else -> {
                binding.llTimeSlot.visibility = View.VISIBLE
            }
        }
        val services = arrayListOf<String>()

        services.add("Select TimeSlot")
        for (i in timeSlots.indices) {
            services.add(timeSlots[i].time_slot)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.custom_drop_down, services
        )

        binding.timeSlotSpinner.adapter = adapter
    }

    private fun handleVisibility() {

        if (order_status_id == "1") {
            binding.cancelOrder.visibility = View.VISIBLE
        }
        if (order_status?.contains("Processed")!!) {
            binding.cancelOrder.visibility = View.VISIBLE
        }
        if (order_status_id!!.contains("17")) { //Dispatched
            binding.cancelOrder.visibility = View.GONE
        }
        if (order_status_id == "2") { //Processing
            binding.cancelOrder.visibility = View.VISIBLE
        }
        if (order_status_id == "5") { //Delivered
            binding.cancelOrder.visibility = View.GONE
            binding.payMoney.visibility = View.GONE
            binding.llTimeSlot.visibility = View.GONE
        }
        if (order_status_id == "7") { //cancelled
            binding.cancelOrder.visibility = View.GONE
            binding.payMoney.visibility = View.GONE
            binding.llTimeSlot.visibility = View.GONE
        }
        if (order_status_id == "11") { //Refunded
            binding.cancelOrder.visibility = View.GONE
            binding.payMoney.visibility = View.GONE
            binding.llTimeSlot.visibility = View.GONE
        }
        if (order_status_id == "12") { //Out for delivery
            binding.cancelOrder.visibility = View.GONE
            binding.payMoney.visibility = View.GONE
            binding.llTimeSlot.visibility = View.GONE
        }
    }

    private fun setProductsAdapter(products: List<OrderDetailsModel.Data.Product>?) {
        val adapter = OrderDetailsProductAdapter(onItemClicked = {

        })

        adapter.items = products as ArrayList<OrderDetailsModel.Data.Product>
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.isNestedScrollingEnabled = false
    }

    private fun setBillsAdapter(totals: List<OrderDetailsModel.Data.Total>?) {
        val adapter = OrderDetailsBillAdapter(onItemClicked = {

        })

        adapter.items = totals as ArrayList<OrderDetailsModel.Data.Total>
        binding.rvBills.layoutManager = LinearLayoutManager(this)
        binding.rvBills.adapter = adapter
        binding.rvBills.isNestedScrollingEnabled = false
    }

    override val fragmentContainer: Int
        get() = 0

    private fun callRazorPayApi(totalAmt: String, orderId: String) {
        var total_: Double = totalAmt.toDouble()
        total_ = total_ * 100
        val totalToSend = total_.toString()

        showLoadingDialog()
        val finalTotal_amount: String = totalToSend

        showLoadingDialog()
        disposableOrders = RestHelper(object : RestResponseHandler {
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

            /*val prefill = JSONObject()
        prefill.put("contact", AppDataManager.instance.userName)

        options.put("prefill", prefill)*/ // add if needed

            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        callChangePaymentMethod()
    }

    private fun callChangePaymentMethod() {
        showLoadingDialog()
        disposableOrders = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                getOrderDetails(orderId)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).changePaymentMethod(orderId)
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

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableOrders)
    }
}