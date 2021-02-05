package com.ecommerce.shopmitt.views.activities

import PreferencesManager
import android.content.Intent
import android.os.Bundle
import com.google.gson.JsonObject
import com.ecommerce.shopmitt.MainActivity
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.ActivityOtpBinding
import com.ecommerce.shopmitt.db.AppDatabase
import com.ecommerce.shopmitt.db.dao.CartDao
import com.ecommerce.shopmitt.models.RegisterModel
import com.ecommerce.shopmitt.models.ViewCart
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtpActivity : BaseActivity() {

    private var disposableCart: Disposable? = null
    private var disposableRegister: Disposable? = null
    private var disposableLoginOtp: Disposable? = null
    private var disposableGenerateOtp: Disposable? = null
    private var disposableResendOtp: Disposable? = null
    private var disposableValidateOtp: Disposable? = null

    private lateinit var cartDao: CartDao

    private lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartDao = AppDatabase.getDatabase(this).cartDao()

        binding.submit.setOnClickListener {
            if (isValidate()) {
                val from = intent.getStringExtra("from")

                if (from == "reg")
                    validateOtp()
                else
                    validateLoginOtp()
            }
        }

        binding.resendOtp.setOnClickListener {
            if (isValidate()) {
                resendOTP()
            }
        }
    }


    private fun validateLoginOtp() {
        showLoadingDialog()
        disposableLoginOtp = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as RegisterModel
                handleLoginOTP(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).validateLoginOTP(getOTPValidateParams())
    }

    private fun handleLoginOTP(model: RegisterModel) {
        PreferencesManager.instance.setString(
            PreferencesManager.KEY_CUSTOMER_ID,
            model.data?.customerId
        )
        PreferencesManager.instance.setString(
            PreferencesManager.KEY_USER_NAME,
            model.data?.firstName
        )

        callCart()
    }

    private fun callCart() {
        showLoadingDialog()
        disposableCart = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ViewCart
                handleCart(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).viewCart()
    }

    private fun handleCart(data: ViewCart.Data?) {
        if (data != null) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {

                    withContext(Dispatchers.IO) {
                        cartDao.addBulkProducts(data)
                    }
                }
            }

            moveToNext()
        } else {
            moveToNext()
        }
    }

    private fun validateOtp() {
        showLoadingDialog()
        disposableValidateOtp = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                getToast().show("OTP verified")
                callRegister()
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).validateOTP(getOTPValidateParams())
    }

    private fun callRegister() {

        showLoadingDialog()
        disposableRegister = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as RegisterModel
                handleRegister(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).register(getRegParams())
    }

    private fun getRegParams(): JsonObject {
        val body = JsonObject()

        val username = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val phone = intent.getStringExtra("mobile")
        val referralCode = intent.getStringExtra("rcode")

        body.addProperty("firstname", username)
        body.addProperty("lastname", "")
        if (!email.isNullOrEmpty())
            body.addProperty("email", email)
        else
            body.addProperty("email", "")

        if (intent.hasExtra("rcode")) {
            body.addProperty("rcode",referralCode)
        }

        body.addProperty("address_1", "")
        body.addProperty("address_2", "")
        body.addProperty("company", "")
        body.addProperty("telephone", phone)
        body.addProperty("password", "")
        body.addProperty("confirm", "")
        body.addProperty("city", "")
        body.addProperty("postcode", "")
        body.addProperty("country_id", "99")
        body.addProperty("zone_id", "1490")
        body.addProperty("agree", "1")

        return body
    }

    private fun handleRegister(model: RegisterModel) {

        PreferencesManager.instance.setString(
            PreferencesManager.KEY_CUSTOMER_ID,
            model.data?.customerId
        )
        PreferencesManager.instance.setString(
            PreferencesManager.KEY_USER_NAME,
            model.data?.firstName
        )

        moveToNext()

    }

    private fun moveToNext() {
        PreferencesManager.instance.setString(PreferencesManager.KEY_LOG_IN, "log_in")

        navigator.navigateByClear(this,MainActivity::class.java)
    }

    private fun getOTPValidateParams(): JsonObject {
        val body = JsonObject()
        body.addProperty("otp", binding.otpView.otp)
        body.addProperty("mobile", intent.getStringExtra("mobile"))
        return body
    }

    private fun resendOTP() {
        showLoadingDialog()
        disposableResendOtp = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                getToast().show("OTP sent again")
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).resendOTP(getOTPResendParams())
    }

    private fun getOTPResendParams(): JsonObject {
        val body = JsonObject()
        body.addProperty("otp", binding.otpView.otp)
        body.addProperty("mobile", intent.getStringExtra("mobile"))
        return body
    }

    private fun generateOtp() {

        showLoadingDialog()
        disposableGenerateOtp = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                getToast().show("OTP sent to your mobile")
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).generateOTP(getOTPParams())
    }

    private fun getOTPParams(): JsonObject {
        val body = JsonObject()
        body.addProperty("mobile", intent.getStringExtra("mobile"))
        return body
    }


    private fun isValidate(): Boolean {

        if (binding.otpView.otp!!.isEmpty()) {
            getToast().show("Enter otp")
            return false
        }

        if (binding.otpView.otp!!.length != 4) {
            getToast().show("Enter valid otp")
            return false
        }

        return true
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableGenerateOtp)
        disposeApiCall(disposableResendOtp)
        disposeApiCall(disposableValidateOtp)
        disposeApiCall(disposableLoginOtp)
        disposeApiCall(disposableRegister)
        disposeApiCall(disposableCart)
    }
}