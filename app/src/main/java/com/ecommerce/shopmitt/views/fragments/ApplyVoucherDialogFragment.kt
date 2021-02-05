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
import com.ecommerce.shopmitt.databinding.FragmentApplyVoucherDialogBinding
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.CartActivity
import com.ecommerce.shopmitt.views.activities.ShippingActivity
import com.google.gson.JsonObject

class ApplyVoucherDialogFragment: DialogFragment() {

    private lateinit var binding: FragmentApplyVoucherDialogBinding

    private lateinit var voucherBridge: VoucherBridge

    companion object {

        fun newInstance(): ApplyVoucherDialogFragment {
            val args = Bundle()
            val fragment = ApplyVoucherDialogFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentApplyVoucherDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpUi()
    }

    private fun setUpUi() {

        binding.close.setOnClickListener {
            dismiss()
        }

        binding.apply.setOnClickListener {
            if (isValidated()) {

                callApplyVoucher()
            }
        }
    }

    private fun callApplyVoucher() {

        (activity as CartActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as CartActivity).hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    onSetVoucher()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as CartActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, activity).applyVoucher(getVoucher())
    }

    private fun getVoucher(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("voucher", binding.etVoucher.text.toString())
        return obj
    }

    private fun onSetVoucher() {
        voucherBridge.onVoucherApplied()
        dismiss()
    }

    private fun isValidated(): Boolean {

        val coupon = binding.etVoucher.text.toString()

        if (coupon.isEmpty()) {
            ToastHelper.instance.show("Enter a voucher code")
            return false
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        voucherBridge = context as VoucherBridge
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    interface VoucherBridge {
        fun onVoucherApplied()
    }
}