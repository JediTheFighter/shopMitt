package com.ecommerce.shopmitt.views.activities

import android.content.Intent
import android.os.Bundle
import com.google.gson.JsonObject
import com.ecommerce.shopmitt.MainActivity
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.base.model.eventbus.MessageEvent
import com.ecommerce.shopmitt.databinding.ActivityLoginBinding
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : BaseActivity() {

    private var disposableGenerateOtp: Disposable? = null

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signIn.setOnClickListener {
            if (isValidate())
                generateOtp()
        }

        binding.signUp.setOnClickListener {
            navigator.navigate(this,RegistrationActivity::class.java)
        }

        binding.skipBtn.setOnClickListener {
            navigator.navigate(this,MainActivity::class.java)
        }
    }

    private fun isValidate(): Boolean {
        if (binding.userName.text.toString().isEmpty()) {
            getToast().show("Enter mobile number")
            return false
        }

        if (binding.userName.text.toString().length != 10) {
            getToast().show("Enter valid mobile number")
            return false
        }

        return true
    }

    override val fragmentContainer: Int
        get() = 0

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(message: MessageEvent) {

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

        }, this).loginOTPGen(getOTPParams())
    }

    private fun getOTPParams(): JsonObject {
        val body = JsonObject()
        body.addProperty("mobile",binding.userName.text.toString())
        return body
    }

    private fun moveToNext() {
        getToast().show("OTP sent to your mobile")

        val intent = Intent(this,OtpActivity::class.java)
        intent.putExtra("from","login")
        intent.putExtra("mobile",binding.userName.text.toString())
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}