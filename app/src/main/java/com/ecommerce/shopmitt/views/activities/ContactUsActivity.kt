package com.ecommerce.shopmitt.views.activities

import android.os.Bundle
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.ActivityContactUsBinding
import com.ecommerce.shopmitt.models.StoreDetailModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable

class ContactUsActivity : BaseActivity() {

    private var disposableStore: Disposable? = null

    private lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("Contact Us")

        getStoreDetails()

        binding.submit.setOnClickListener {
            if (isValidated()) {
                callSubmitEnquiry()
            }
        }
    }

    private fun callSubmitEnquiry() {
        showLoadingDialog()
        disposableStore = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).contactUs(getContactParams())
    }

    private fun getContactParams(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("name", binding.etName.text.toString())
        obj.addProperty("email", binding.etMail.text.toString())
        obj.addProperty("enquiry", binding.etEnquiry.text.toString())
        return obj
    }

    private fun isValidated(): Boolean {

        if (binding.etName.text.toString().isEmpty()) {
            getToast().show("Enter name")
            return false
        }

        if (binding.etMail.text.toString().isEmpty()) {
            getToast().show("Enter email ")
            return false
        }

        if (binding.etMail.text.toString().isNotEmpty()) {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etMail.text.toString()).matches()) {
                getToast().show("Enter valid email")
                return false
            }
        }

        if (binding.etEnquiry.text.toString().isEmpty()) {
            getToast().show("Enter your enquiry")
            return false
        }

        return true
    }

    private fun getStoreDetails() {
        showLoadingDialog()
        disposableStore = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as StoreDetailModel
                getToast().show("Enquiry Sent Successfully")
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getStoreDetails()
    }


    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableStore)
    }
}