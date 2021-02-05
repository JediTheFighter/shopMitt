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
import com.ecommerce.shopmitt.databinding.FragmentApplyCouponDialogBinding
import com.ecommerce.shopmitt.models.GenericModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.CartActivity
import com.ecommerce.shopmitt.views.activities.ShippingActivity
import com.google.gson.JsonObject

class ApplyCouponDialogFragment: DialogFragment() {

    private lateinit var binding: FragmentApplyCouponDialogBinding

    private lateinit var couponBridge: CouponBridge

    companion object {

        fun newInstance(): ApplyCouponDialogFragment {
            val args = Bundle()
            val fragment = ApplyCouponDialogFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentApplyCouponDialogBinding.inflate(layoutInflater)
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
                callApplyCoupon()
            }
        }
    }

    private fun callApplyCoupon() {

        (activity as CartActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as CartActivity).hideLoadingDialog()
                val model = `object` as GenericModel
                if (model.success == 1) {
                    onSetCoupon()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as CartActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, activity).applyCoupon(getCoupon())
    }

    private fun getCoupon(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("coupon", binding.etCoupon.text.toString())
        return obj
    }

    private fun onSetCoupon() {
        couponBridge.onCouponApplied()
        dismiss()
    }

    private fun isValidated(): Boolean {

        val coupon = binding.etCoupon.text.toString()

        if (coupon.isEmpty()) {
            ToastHelper.instance.show("Enter a coupon")
            return false
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        couponBridge = context as CouponBridge
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    interface CouponBridge {
        fun onCouponApplied()
    }
}