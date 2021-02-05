package com.ecommerce.shopmitt.views.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.FragmentAddMoneyDialogBinding
import com.ecommerce.shopmitt.models.RazorPayModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.WalletActivity
import com.google.gson.JsonObject
import java.util.*

class AddMoneyDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentAddMoneyDialogBinding

    private lateinit var addMoney: AddMoneyBridge

    companion object {

        fun newInstance(): AddMoneyDialogFragment {
            val args = Bundle()
            return AddMoneyDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddMoneyDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpUi()
    }

    private fun setUpUi() {


        binding.addMoney.setOnClickListener {
            if (isValidated()) {
                callAddWallet()
            }
        }
    }

    private fun callAddWallet() {

        (activity as WalletActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as WalletActivity).hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    val random = Random()
                    val orderId = "wallet" + random.nextInt() * 100
                    callRazorPayApi(binding.etAmount.text.toString(), orderId)
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as WalletActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, activity).addWallet(getWalletParams())
    }

    private fun getWalletParams(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("amount", binding.etAmount.text.toString())
        return obj
    }


    private fun isValidated(): Boolean {

        val coupon = binding.etAmount.text.toString()

        if (coupon.isEmpty()) {
            ToastHelper.instance.show("Enter amount")
            return false
        }
        return true
    }

    private fun callRazorPayApi(totalAmt: String, orderId: String) {
        var total_: Double = totalAmt.toDouble()
        total_ = total_ * 100
        val totalToSend = total_.toString()

        val finalTotal_amount: String = totalToSend

        (activity as WalletActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as WalletActivity).hideLoadingDialog()

                val model = `object` as RazorPayModel
                addMoney.onOrderIdGenerated(finalTotal_amount, model.id)
                dismiss()
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as WalletActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
                dismiss()
            }

        }, activity).getRazorPayOrderId(totalAmt, orderId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addMoney = context as AddMoneyBridge
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    interface AddMoneyBridge {
        fun onOrderIdGenerated(totalAmount: String, orderId: String)
    }
}