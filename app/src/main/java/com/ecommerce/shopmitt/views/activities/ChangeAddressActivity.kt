package com.ecommerce.shopmitt.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.ActivityChangeAddressBinding
import com.ecommerce.shopmitt.models.ShippingAddressModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.ChangeAddressAdapter
import com.ecommerce.shopmitt.views.fragments.EditAddressDialogFragment
import io.reactivex.disposables.Disposable

class ChangeAddressActivity : BaseActivity(),EditAddressDialogFragment.AddressEdit,ChangeAddressAdapter.AddressBridge {

    private var disposableAddress: Disposable? = null

    private lateinit var binding: ActivityChangeAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("Change Address")

        callAddresses()
    }

    private fun callAddresses() {
        showLoadingDialog()
        disposableAddress = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ShippingAddressModel
                handleAddresses(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getShippingAddress()
    }

    private fun handleAddresses(data: ShippingAddressModel.Data?) {
        val adapter = ChangeAddressAdapter(onItemClicked = {

        }, this, data?.addressId!!)

        adapter.items = data?.addresses as ArrayList<ShippingAddressModel.Data.Address>

        binding.rvAddresses.layoutManager = LinearLayoutManager(this)
        binding.rvAddresses.adapter = adapter
        binding.rvAddresses.isNestedScrollingEnabled = false
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableAddress)
    }

    override fun onAddressEdit(address: Bundle) {

        val obj = JsonObject()
        obj.addProperty("firstname", address.getString("name"))
        obj.addProperty("lastname", "")
        obj.addProperty("city", address.getString("city"))
        obj.addProperty("telephone", address.getString("mobile"))
        obj.addProperty("address_1", address.getString("address"))
        obj.addProperty("address_2", "")
        obj.addProperty("company", "")
        obj.addProperty("country_id", "99")
        obj.addProperty("postcode", address.getString("pincode"))
        obj.addProperty("zone_id", "1490")
        obj.addProperty("prev_addr", address.getString("map_location"))
        obj.addProperty("latitude", address.getString("lat"))
        obj.addProperty("longitude", address.getString("lng"))

        val addressId = address.getString("address_id")!!

        callEditAddress(addressId, obj)
    }

    private fun callEditAddress(addressId: String, params: JsonObject) {
        showLoadingDialog()
        disposableAddress = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    getToast().show("Address updated")
                    setResult(Activity.RESULT_OK)
                    callAddresses()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).updateAddress(addressId, params)
    }

    override fun onDeleteAddress(addressId: String?) {
        callDeleteAddress(addressId)
    }

    override fun onSetAddress(addressId: String?, pos: Int) {
        callSetShippingAddress(addressId!!,pos)
    }

    private fun callSetShippingAddress(addressId: String, pos: Int) {
        showLoadingDialog()
        disposableAddress = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).setShippingAddress(getAddressID(pos,addressId))
    }

    private fun getAddressID(position: Int, addressId: String): JsonObject {
        val obj = JsonObject()
        obj.addProperty("address_id", addressId)
        return obj
    }



    private fun callDeleteAddress(addressId: String?) {

        showLoadingDialog()
        disposableAddress = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                if (model.success == 1) {
                    getToast().show("Address removed")
                    setResult(Activity.RESULT_OK)
                    callAddresses()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).deleteAddress(addressId!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            fragment.onActivityResult(requestCode, resultCode, data)
        }

    }
}