package com.ecommerce.shopmitt.views.activities

import PreferencesManager
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.MainActivity
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityDeliveryPlacesBinding
import com.ecommerce.shopmitt.models.DeliveryPlacesModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.ToastHelper
import java.util.*

class DeliveryPlacesActivity : BaseActivity() {

    private lateinit var binding: ActivityDeliveryPlacesBinding

    var dataList: List<DeliveryPlacesModel.Data> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (dataList != null) {
                val cityName: String = dataList[position].cityName
                val zipCode: String = dataList[position].zipCode
                proceedToNext(cityName, zipCode)
            }
        }

        callDeliveryPlaces()
    }

    private fun callDeliveryPlaces() {
        showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as DeliveryPlacesModel
                if (model.success == 1) {
                    dataList = model.data


                    val arrayList = ArrayList<String>()
                    for (i in dataList.indices) {
                        arrayList.add(dataList[i].cityName)
                    }

                    val adapter: ArrayAdapter<*> = ArrayAdapter(this@DeliveryPlacesActivity,
                        R.layout.row_delivery_place, arrayList)

                    binding.listView.adapter = adapter
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
            }

        }, this).getDeliveryPlaces()
    }

    private fun proceedToNext(cityName: String, zipCode: String) {
        PreferencesManager.instance.setString(PreferencesManager.KEY_CURR_LOCATION, cityName)

        launchHomeScreen()
    }

    private fun launchHomeScreen() {
        val `in` = Intent(this, MainActivity::class.java)
        `in`.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(`in`)
        finish()
    }

    override val fragmentContainer: Int
        get() = 0
}