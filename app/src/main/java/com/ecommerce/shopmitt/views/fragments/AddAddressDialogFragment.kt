package com.ecommerce.shopmitt.views.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.ecommerce.shopmitt.databinding.FragmentAddAddressDialogBinding
import com.ecommerce.shopmitt.models.GenericModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.FROM_SHIPPING
import com.ecommerce.shopmitt.utils.Constants.GET_LOCATION
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.MapActivity
import com.ecommerce.shopmitt.views.activities.ShippingActivity

class AddAddressDialogFragment: DialogFragment() {

    private lateinit var binding: FragmentAddAddressDialogBinding

    private lateinit var addressBridge: AddressAdd

    var latitude = 0.0
    var longitude = 0.0

    companion object {

        fun newInstance(): AddAddressDialogFragment {
            val args = Bundle()
            val fragment = AddAddressDialogFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddAddressDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpUi()
    }


    private fun setUpUi() {

        binding.addAddress.setOnClickListener {
            if (isValidated()) {

                callCheckPinCode(binding.pinCode.text.toString())
            }
        }

        binding.cvLocation.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra("from", FROM_SHIPPING)
            startActivityForResult(intent, GET_LOCATION)
        }

    }

    private fun callCheckPinCode(pinCode: String) {

        (activity as ShippingActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as ShippingActivity).hideLoadingDialog()
                val model = `object` as GenericModel
                if (model.success == 1) {
                    addAddress()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as ShippingActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, activity).checkPinCode(pinCode)
    }

    private fun addAddress() {

        val name = binding.fullName.text.toString()
        val mobile = binding.mobileNumber.text.toString()
        val pincode = binding.pinCode.text.toString()
        val address = binding.address.text.toString()
        val city = binding.city.text.toString()

        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("mobile", mobile)
        bundle.putString("pincode", pincode)
        bundle.putString("address", address)
        bundle.putString("city", city)
        bundle.putString("map_location",binding.textLocation.text.toString())
        bundle.putString("lat",latitude.toString())
        bundle.putString("lng",longitude.toString())
        addressBridge.onAddressAdded(bundle)

        dismiss()
    }

    private fun isValidated(): Boolean {

        val name = binding.fullName.text.toString()
        val mobile = binding.mobileNumber.text.toString()
        val pincode = binding.pinCode.text.toString()
        val address = binding.address.text.toString()
        val city = binding.city.text.toString()

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
        if (pincode.isEmpty()) {
            ToastHelper.instance.show("Enter your pincode")
            return false
        }
        if (address.isEmpty()) {
            ToastHelper.instance.show("Enter your address")
            return false
        }
        if (city.isEmpty()) {
            ToastHelper.instance.show("Enter your city")
            return false
        }

        /*if (latitude == 0.0) {
            ToastHelper.instance.show("Add your location from map")
            return false
        }*/
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addressBridge = context as AddressAdd
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                val place = data!!.getStringExtra("location")
                val lat = data.getStringExtra("lat")
                val lng = data.getStringExtra("lng")
                latitude = lat?.toDouble()!!
                longitude = lng?.toDouble()!!

                binding.textLocation.text = place
            }
        }
    }


    interface AddressAdd {
        fun onAddressAdded(address: Bundle)
    }
}