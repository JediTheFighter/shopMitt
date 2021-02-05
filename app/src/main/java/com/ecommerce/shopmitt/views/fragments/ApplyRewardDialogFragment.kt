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
import com.ecommerce.shopmitt.databinding.FragmentApplyRewardDialogBinding
import com.ecommerce.shopmitt.models.GenericModel
import com.ecommerce.shopmitt.models.ProfileModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.CURRENCY
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.CartActivity
import com.google.gson.JsonObject

class ApplyRewardDialogFragment: DialogFragment() {

    private lateinit var binding: FragmentApplyRewardDialogBinding

    private lateinit var rewardBridge: RewardBridge

    companion object {

        fun newInstance(): ApplyRewardDialogFragment {
            val args = Bundle()
            val fragment = ApplyRewardDialogFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentApplyRewardDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpUi()
    }

    private fun setUpUi() {

        getRewardPoints()

        binding.close.setOnClickListener {
            dismiss()
        }

        binding.apply.setOnClickListener {
            if (isValidated()) {
                callApplyReward()
            }
        }
    }

    private fun getRewardPoints() {
        (activity as CartActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as CartActivity).hideLoadingDialog()
                val model = `object` as ProfileModel
                if (model.success == 1) {
                    setRewards(model.data)
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as CartActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, activity).getProfile()
    }

    private fun setRewards(data: ProfileModel.Data?) {

        if (!data?.reward_total.equals("0")) {
            var sum = 0
            for (i in 0 until data?.rewards?.size!!) {
                sum += data.rewards[i].points!!.toInt()
            }

            binding.rewardBalance.text = "$sum"
        } else {
            binding.rewardBalance.text = "0"
        }
    }

    private fun callApplyReward() {

        (activity as CartActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as CartActivity).hideLoadingDialog()
                val model = `object` as GenericModel
                if (model.success == 1) {
                    onSetReward()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as CartActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, activity).applyReward(getReward())
    }

    private fun getReward(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("reward", binding.etPoints.text.toString())
        return obj
    }

    private fun onSetReward() {
        rewardBridge.onRewardApplied()
        dismiss()
    }

    private fun isValidated(): Boolean {

        val coupon = binding.etPoints.text.toString()

        if (coupon.isEmpty()) {
            ToastHelper.instance.show("Enter points")
            return false
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        rewardBridge = context as RewardBridge
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    interface RewardBridge {
        fun onRewardApplied()
    }
}