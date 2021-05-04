package com.ecommerce.shopmitt.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.ActivityShippingBinding
import com.ecommerce.shopmitt.models.ShippingAddressModel
import com.ecommerce.shopmitt.models.ViewCart
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.fragments.AddAddressDialogFragment
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable

class ShippingActivity : BaseActivity(), AddAddressDialogFragment.AddressAdd {

    private val CHANGE_ADDRESS: Int = 1022

    private var lat_addr: String? = null
    private var lon_addr: String? = null
    private var addresses: List<ShippingAddressModel.Data.Address> = mutableListOf()

    private var date: String? = null
    private var timeSlot: String? = null


    private var disposableAddress: Disposable? = null

    private lateinit var binding: ActivityShippingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("Shipping Address")

        callApis()

        binding.addNewAddress.setOnClickListener {
            AddAddressDialogFragment.newInstance().show(supportFragmentManager, "add_address")
        }

        binding.changeAddress.setOnClickListener {
            val intent = Intent(this, ChangeAddressActivity::class.java)
            startActivityForResult(intent, CHANGE_ADDRESS)
        }

        binding.proceed.setOnClickListener {

            val intent = Intent(this, PaymentActivity::class.java)
            val list = getIntent().getParcelableArrayListExtra<ViewCart.Data.Total>("bill")
            intent.putExtra("address", binding.userAddress.text.toString())
            intent.putExtra("date", date)
            intent.putExtra("time_slot", timeSlot)
            intent.putExtra("lat", lat_addr)
            intent.putExtra("lng", lon_addr)
            intent.putParcelableArrayListExtra("bill",list)
            startActivity(intent)
        }

        getExtrasData()
    }

    private fun getExtrasData() {
        if (intent.hasExtra("date"))
            date = intent.getStringExtra("date")
        if (intent.hasExtra("time_slot"))
            timeSlot = intent.getStringExtra("time_slot")

        binding.timeSlot.text = timeSlot
        binding.date.text = date
    }

    private fun callApis() {
        getAddresses()
    }


    private fun getAddresses() {

        showLoadingDialog()
        disposableAddress = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()

                val model = `object` as ShippingAddressModel

                if (model.data != null) {
                    if (model.data?.addresses?.isNotEmpty()!!) {
                        addresses = model.data?.addresses!!
                        handleShippingAddress(model.data)
                    }
                } else {
                    getToast().show("Add address to continue")
                    AddAddressDialogFragment.newInstance().show(
                        supportFragmentManager,
                        "add_address"
                    )
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getShippingAddress()
    }

    private fun setShippingAddress(pos: Int) {
        showLoadingDialog()
        disposableAddress = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).setShippingAddress(getAddressID(pos))
    }

    private fun getAddressID(pos: Int): JsonObject {
        Log.d("Shipping", "getAddressID: $pos, object: ${addresses[pos]}")
        val obj = JsonObject()
        obj.addProperty("address_id", addresses[pos].addressId)
        if (addresses[pos].latitude != null)
            lat_addr = addresses[pos].latitude
        if (addresses[pos].longitude != null)
            lon_addr = addresses[pos].longitude
        return obj
    }

    private fun setPaymentAddress(pos: Int) {
        showLoadingDialog()
        disposableAddress = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                setShippingAddress(pos)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).setPaymentAddress(getAddressID(pos))
    }

    private fun handleShippingAddress(data: ShippingAddressModel.Data?) {

        binding.cvCurrentAddress.visibility = View.VISIBLE
        binding.proceed.visibility = View.VISIBLE
        binding.textOr.visibility = View.VISIBLE

        val def_address: String = data?.addressId!!
        val count: Int = data.addresses.size
        Log.v("response", " adrs count: $count")
        var pos = 0
        for (i in 0 until count) {
            if (def_address == data.addresses[i].addressId)
                pos = i
        }
        val address: String = getDeliveryAddress(data, pos)
        binding.userAddress.text = address

        setPaymentAddress(pos)
    }

    private fun getDeliveryAddress(data: ShippingAddressModel.Data, pos: Int): String {
        return if (data.addresses.isNotEmpty()) {
            val sb = StringBuilder("")
            Log.d("tag", "getAddress: " + data.addresses)
            sb.append(data.addresses[pos].firstname)
            sb.append("\n")
            sb.append(data.addresses[pos].address1)
            sb.append("\n")
            sb.append(data.addresses[pos].city)
            sb.append(" - ")
            sb.append(data.addresses[pos].postcode)
            sb.append("\n")
            sb.append(data.addresses[pos].zone)
            sb.append(", ")
            sb.append(data.addresses[pos].country)
            sb.append("\n")
            sb.toString()
        } else "No Address Available"
    }


    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableAddress)
    }

    override fun onAddressAdded(address: Bundle) {

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

        callAddAddress(obj)

    }

    private fun callAddAddress(params: JsonObject) {
        showLoadingDialog()
        disposableAddress = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                getAddresses()
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).addShippingAddress(params)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            fragment.onActivityResult(requestCode, resultCode, data)
        }


        if (requestCode == CHANGE_ADDRESS && resultCode == Activity.RESULT_OK) {
            getAddresses()
        }
    }
}