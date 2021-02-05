package com.ecommerce.shopmitt.views.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.ecommerce.shopmitt.databinding.FragmentEditAddressDialogBinding
import com.ecommerce.shopmitt.models.GenericModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.ChangeAddressActivity
import com.ecommerce.shopmitt.views.activities.MapActivity

class EditAddressDialogFragment: DialogFragment() {

    private lateinit var binding: FragmentEditAddressDialogBinding

    private lateinit var addressBridge: AddressEdit

    private lateinit var currAddress: Bundle

    var latitude = 0.0
    var longitude = 0.0

    companion object {

        const val CURR_ADDR = "curr_address"

        fun newInstance(address: Bundle): EditAddressDialogFragment {
            val args = Bundle()
            args.putBundle(CURR_ADDR,address)
            val fragment = EditAddressDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditAddressDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        currAddress = arguments?.getBundle(CURR_ADDR)!!
        setUpUi()
    }

    private fun setUpUi() {

        val name = currAddress.getString("name")
        val mobile = currAddress.getString("mobile")
        val pincode = currAddress.getString("pincode")
        val address = currAddress.getString("address")
        val city = currAddress.getString("city")
        val prevAddress = currAddress.getString("prev_address")
        val lat = currAddress.getString("latitude")
        val lng = currAddress.getString("longitude")

        if (!lat.isNullOrEmpty()) {
            latitude = lat.toDouble()
        }

        if (!lng.isNullOrEmpty()) {
            longitude = lng.toDouble()
        }

        binding.fullName.setText(name)
        binding.mobileNumber.setText(mobile)
        binding.pinCode.setText(pincode)
        binding.address.setText(address)
        binding.city.setText(city)

        if (!prevAddress.isNullOrEmpty()) {
            binding.textLocation.text = prevAddress
        }

        binding.addAddress.setOnClickListener {
            if (isValidated()) {

                callCheckPinCode(binding.pinCode.text.toString())

            }
        }

        binding.cvLocation.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra("from", Constants.FROM_CHANGE_ADDRESS)
            startActivityForResult(intent, Constants.GET_LOCATION)
        }

    }

    private fun callCheckPinCode(pinCode: String) {

        (activity as ChangeAddressActivity).showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                (activity as ChangeAddressActivity).hideLoadingDialog()
                val model = `object` as GenericModel
                if(model.success == 1) {
                    editAddress()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                (activity as ChangeAddressActivity).hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, activity).checkPinCode(pinCode)
    }

    private fun editAddress() {

        val name = binding.fullName.text.toString()
        val mobile = binding.mobileNumber.text.toString()
        val pincode = binding.pinCode.text.toString()
        val address = binding.address.text.toString()
        val city = binding.city.text.toString()

        val bundle = Bundle()
        bundle.putString("name",name)
        bundle.putString("mobile",mobile)
        bundle.putString("pincode",pincode)
        bundle.putString("address",address)
        bundle.putString("city",city)
        bundle.putString("address_id",currAddress.getString("address_id"))
        bundle.putString("map_location",binding.textLocation.text.toString())
        bundle.putString("lat",latitude.toString())
        bundle.putString("lng",longitude.toString())
        addressBridge.onAddressEdit(bundle)

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

        if (latitude == 0.0) {
            ToastHelper.instance.show("Add your location from map")
            return false
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addressBridge = context as AddressEdit
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
        if (requestCode == Constants.GET_LOCATION) {
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

    interface AddressEdit {
        fun onAddressEdit(address: Bundle)
    }
}