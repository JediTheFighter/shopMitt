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
import com.google.gson.JsonObject
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.FragmentDialogUpdateProfileBinding
import com.ecommerce.shopmitt.models.ProfileActivity
import com.ecommerce.shopmitt.models.ProfileModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.ToastHelper

class UpdateProfileDialogFragment: DialogFragment() {

    private lateinit var binding: FragmentDialogUpdateProfileBinding

    private lateinit var profileBridge: ProfileBridge

    private lateinit var profile: ProfileModel.Data

    companion object {

        const val PROFILE = "profile"

        fun newInstance(profile: ProfileModel.Data): UpdateProfileDialogFragment {
            val args = Bundle()
            args.putParcelable(PROFILE, profile)
            val fragment = UpdateProfileDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDialogUpdateProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profile = arguments?.getParcelable(PROFILE)!!
        setUpUi()
    }

    private fun setUpUi() {

        val name = profile.firstname
        val mobile = profile.telephone
        val email = profile.email

        binding.fullName.setText(name)
        binding.mobileNumber.setText(mobile)
        binding.email.setText(email)

        binding.addAddress.setOnClickListener {
            if (isValidated()) {

                callUpdateProfile()

            }
        }
    }

    private fun callUpdateProfile() {

        (activity as ProfileActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as ProfileActivity).hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    ToastHelper.instance.show("Profile Updated")
                    profileBridge.onEditProfile()
                    dismiss()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as ProfileActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, activity).updateProfile(getUpdateParams())
    }

    private fun getUpdateParams(): JsonObject {
        val body = JsonObject()
        val fullname = binding.fullName.text.toString()
        val phone = binding.mobileNumber.text.toString()
        val email = binding.email.text.toString()

        body.addProperty("firstname", fullname)
        body.addProperty("lastname", "")
        body.addProperty("email", email)
        body.addProperty("telephone", phone)
        return body
    }



    private fun isValidated(): Boolean {

        val name = binding.fullName.text.toString()
        val mobile = binding.mobileNumber.text.toString()
        val email = binding.email.text.toString()

        if (name.isEmpty()) {
            ToastHelper.instance.show("Enter your name")
            return false
        }
        if (mobile.isEmpty()) {
            ToastHelper.instance.show("Enter your mobile")
            return false
        }
        if (mobile.length != 10) {
            ToastHelper.instance.show("Enter valid mobile")
            return false
        }
        if (email.isEmpty()) {
            ToastHelper.instance.show("Enter your email")
            return false
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        profileBridge = context as ProfileBridge
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    interface ProfileBridge {
        fun onEditProfile()
    }
}