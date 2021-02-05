package com.ecommerce.shopmitt.models

import android.os.Bundle
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityProfileBinding
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.fragments.UpdateProfileDialogFragment
import io.reactivex.disposables.Disposable

class ProfileActivity : BaseActivity(),UpdateProfileDialogFragment.ProfileBridge {

    private var profileData: ProfileModel.Data? = null

    private var disposableProfile: Disposable? = null

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.updateProfile.setOnClickListener {
            if (profileData != null) {
                UpdateProfileDialogFragment.newInstance(profileData!!).show(supportFragmentManager,"update")
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        getProfileDetails()
    }

    private fun getProfileDetails() {
        showLoadingDialog()
        disposableProfile = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ProfileModel
                handleProfileDetails(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getProfile()
    }

    private fun handleProfileDetails(data: ProfileModel.Data?) {
        binding.userName.text = data?.firstname
        binding.userPhone.text = data?.telephone

        profileData = data
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableProfile)
    }

    override fun onEditProfile() {
        getProfileDetails()
    }
}