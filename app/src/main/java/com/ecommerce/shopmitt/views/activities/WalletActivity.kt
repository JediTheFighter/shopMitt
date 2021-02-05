package com.ecommerce.shopmitt.views.activities

import PreferencesManager
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.dialog.*
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.ActivityWalletBinding
import com.ecommerce.shopmitt.models.TransactionModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.WalletAdapter
import com.ecommerce.shopmitt.views.fragments.AddMoneyDialogFragment
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_wallet.*
import org.json.JSONObject

class WalletActivity : BaseActivity(),AddMoneyDialogFragment.AddMoneyBridge, PaymentResultListener {

    private var amountSelected: String? = null

    private var disposableWallet: Disposable? = null

    private lateinit var binding: ActivityWalletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTransactions()

        Checkout.preload(applicationContext)

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.fabAddMoney.setOnClickListener {
            AddMoneyDialogFragment.newInstance().show(supportFragmentManager,"add_money")
        }
    }

    private fun getTransactions() {

        val customerId = PreferencesManager.instance.getString(PreferencesManager.KEY_CUSTOMER_ID)!!

        showLoadingDialog()
        disposableWallet = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as TransactionModel
                handleWalletList(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getWalletTransactions(customerId)


    }

    private fun handleWalletList(model: TransactionModel) {

        binding.userName.text = "Name: ${AppDataManager.instance.userName}"
        binding.walletBalance.text = model.data.balance

        val adapter = WalletAdapter(onItemClicked = {

        })

        adapter.items = model.data.history as ArrayList<TransactionModel.Data.History>

        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = adapter
        binding.rvTransactions.isNestedScrollingEnabled = false

        if (model.data.history.isEmpty()) {
            binding.tvError.visibility = View.VISIBLE
        } else {
            binding.tvError.visibility = View.GONE
        }
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableWallet)
    }

    override fun onOrderIdGenerated(totalAmount: String, orderId: String) {
        amountSelected = totalAmount
        startPayment(totalAmount, orderId)
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
        }catch (e: Exception){
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        confirmWalletPay()
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

    private fun confirmWalletPay() {

        showLoadingDialog()
        disposableWallet = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                getToast().show("Transaction success")
                getTransactions()
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).confirmWalletPayment(getConfirmWalletParams())


    }

    private fun getConfirmWalletParams(): JsonObject {
        val amt = amountSelected?.toDouble()!! / 100
        val obj = JsonObject()
        obj.addProperty("amount", amt.toString())
        return obj
    }
}