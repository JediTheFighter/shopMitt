package com.ecommerce.shopmitt.views.activities

import android.content.Intent
import android.os.Bundle
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.ActivityRegistrationBinding
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable

class RegistrationActivity : BaseActivity() {

    private var disposableRegister: Disposable? = null
    private var disposableRefer: Disposable? = null
    private var disposableGenerateOtp: Disposable? = null

    private lateinit var binding: ActivityRegistrationBinding

    private var isRefApplied: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submit.setOnClickListener {
            if (isValidated()) {
                generateOtp()
            }
        }

        binding.applyRefer.setOnClickListener {
            if (validateRef()) {
                callApplyRefer()
            }
        }

    }

    private fun callApplyRefer() {
        showLoadingDialog()
        disposableRefer = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    isRefApplied = true
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).verifyRefCode(getReferParams())
    }

    private fun getReferParams(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("rcode", binding.referralCode.text.toString())
        return obj
    }

    private fun validateRef(): Boolean {
        val referral = binding.referralCode.text.toString()

        if (referral.isEmpty()) {
            getToast().show("Enter referral code")
            return false
        }

        return true
    }


    private fun generateOtp() {

        showLoadingDialog()
        disposableGenerateOtp = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    moveToNext()
                } else {
                    getToast().show(model.error[0])
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).generateOTP(getOTPParams())
    }

    private fun moveToNext() {

        getToast().show("OTP sent to your mobile")

        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra("from", "reg")
        intent.putExtra("mobile", binding.userMobile.text.toString())
        intent.putExtra("name", binding.userName.text.toString())
        intent.putExtra("mail", binding.userMail.text.toString())

        if (isRefApplied)
            intent.putExtra("rcode", binding.referralCode.text.toString())
        startActivity(intent)
        finish()
    }

    private fun getOTPParams(): JsonObject {
        val body = JsonObject()
        body.addProperty("mobile", binding.userMobile.text.toString())
        return body
    }



    private fun isValidated(): Boolean {

        if (binding.userName.text.toString().isEmpty()) {
            getToast().show("Enter name")
            return false
        }

        if (binding.userMobile.text.toString().isEmpty()) {
            getToast().show("Enter mobile number")
            return false
        }

        if (binding.userMail.text.toString().isNotEmpty()) {
             if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.userMail.text.toString()).matches()) {
                 getToast().show("Enter valid email")
                 return false
             }
        }

        return true
    }

    override val fragmentContainer: Int
        get() = 0


    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableRegister)
        disposeApiCall(disposableRefer)
        disposeApiCall(disposableGenerateOtp)
    }
}