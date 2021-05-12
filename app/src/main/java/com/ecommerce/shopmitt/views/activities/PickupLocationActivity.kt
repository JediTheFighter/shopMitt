package com.ecommerce.shopmitt.views.activities

import PreferencesManager
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.MainActivity
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.extentions.setSafeOnClickListener
import com.ecommerce.shopmitt.databinding.ActivityPickupLocationBinding
import com.ecommerce.shopmitt.models.GenericModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.FROM_PICKUP
import com.ecommerce.shopmitt.utils.LocationUtil
import com.ecommerce.shopmitt.utils.LogHelper
import com.ecommerce.shopmitt.utils.ToastHelper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import permissions.dispatcher.*
import java.io.IOException
import java.util.*

@RuntimePermissions
class PickupLocationActivity : BaseActivity(), LocationUtil.OneShotLocationListener {

    private lateinit var binding: ActivityPickupLocationBinding

    private var isLocationAssigned: Boolean = false

    private val REQUEST_CHECK_LOCATION_SETTINGS = 1
    private val PERMISSION_CHECK = 212
    private val MAP_RESULT = 321

    var permission = Manifest.permission.ACCESS_FINE_LOCATION

    private var locationUtil: LocationUtil? = null
    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickupLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationUtil = LocationUtil(this)

        checkGPSAndGetLocationWithPermissionCheck()


        // Listeners
        binding.btnGps.setSafeOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
                    if (isLocationAssigned)
                        locationUtil?.getLocation(this)
                    else {
                        getToast().show("Fetching location...")
                        locationUtil?.getLocation(this)
                    }
                } else Toast.makeText(
                    this@PickupLocationActivity,
                    getString(R.string.unable_to_get_location),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_CHECK)
            }
        }

        binding.btnManualLocation.setOnClickListener {
            val intent = Intent(this@PickupLocationActivity, MapActivity::class.java)
            intent.putExtra("from", FROM_PICKUP)
            startActivityForResult(intent, MAP_RESULT)
        }
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_LOCATION_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {

                }
                Activity.RESULT_CANCELED -> {
                    checkGPSAndGetLocationWithPermissionCheck()
                }
            }

            MAP_RESULT -> {
                if (resultCode == Activity.RESULT_OK) {

                    if (data?.hasExtra("lat") == true) {

                        isLocationAssigned = true

                        val latitude = data.getStringExtra("lat")!!.toDouble()
                        val longitude = data.getStringExtra("lng")!!.toDouble()

                        val addressFromLatLng = getAddress(latitude, longitude)

                        if (getAddress(latitude, longitude) != "") {

                            val pincode: String?
                            try {
                                pincode = getPostcodeByCoordinates(latitude, longitude)
                                if (pincode != null) {
                                    Log.i("PAST MAP","CHECK")
                                    callCheckPincode(pincode, addressFromLatLng)
                                } else {
                                    getToast().show("Location not found")
                                    goToDeliveryPlaceList(null, addressFromLatLng)
                                }
                            } catch (e: IOException) {
                                getToast().show("Location not found")
                                goToDeliveryPlaceList(null, addressFromLatLng)
                            }
                        }
                    }
                }

            }
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun checkGPSAndGetLocation() {

        // Enable GPS
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationUtil?.locationRequest)
        builder.setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this,
            OnSuccessListener<LocationSettingsResponse?> { // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                LogHelper.instance.printInfoLog("Location Resolution onSuccess")

            })
        task.addOnFailureListener(this, OnFailureListener { e ->
            LogHelper.instance.printErrorLog("Location Resolution onFailure")
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        this,
                        REQUEST_CHECK_LOCATION_SETTINGS
                    )
                    LogHelper.instance.printInfoLog("Location Resolution error show dialog")
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        })
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationaleForLocation(request: PermissionRequest) {

    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showDeniedForLocation() {
        getToast().show(getString(R.string.location_permission_denied))
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showNeverAskForLocation() {
        Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated method
        onRequestPermissionsResult(requestCode, grantResults)

    }

    override fun onLocationSuccess(location: Location?) {
        isLocationAssigned = true

        val latitude = location!!.latitude
        val longitude = location.longitude

        locationUtil?.dispose()

        val addressFromLatLng = getAddress(latitude, longitude)

        if (getAddress(latitude, longitude) != "") {

            val pincode: String?
            try {
                pincode = getPostcodeByCoordinates(latitude, longitude)
                if (pincode != null) {
                    Log.i("PAST LOCN","CHECK")
                    callCheckPincode(pincode, addressFromLatLng)
                } else {
                    getToast().show("Location not found")
                    goToDeliveryPlaceList(null, addressFromLatLng)
                }
            } catch (e: IOException) {
                getToast().show("Location not found")
                goToDeliveryPlaceList(null, addressFromLatLng)
            }
        }

    }

    private fun callCheckPincode(pinCode: String, addressFromLatLng: String?) {
        showLoadingDialog()
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as GenericModel
                if (model.success == 1) {

                    PreferencesManager.instance.setString(
                        PreferencesManager.KEY_CURR_LOCATION,
                        addressFromLatLng
                    )
                    getToast().show("Great!, Our service is available at your location")
                    launchHomeScreen()
                } else
                    goToDeliveryPlaceList(pinCode, addressFromLatLng)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                ToastHelper.instance.show(statusMessage)
                goToDeliveryPlaceList(pinCode, addressFromLatLng)
            }

        }, this).checkPinCode(pinCode)
    }

    private fun goToDeliveryPlaceList(pinCode: String?, placeName: String?) {
        val `in` = Intent(this@PickupLocationActivity, DeliveryPlacesActivity::class.java)
        `in`.putExtra("pin_code", pinCode)
        if (placeName != null) `in`.putExtra("place_name", placeName)
//        `in`.putExtra("is_from_splash", isFromSplash)
        startActivity(`in`)
    }

    override fun onLocationError(error: String?) {
        getToast().show("Failed to fetch location")
        isLocationAssigned = false
    }

    override fun startLocationSettingsResolution(
        LocationSettingsType: Int,
        resolvableApiException: ResolvableApiException?
    ) {

    }

    private fun launchHomeScreen() {

        if (AppDataManager.instance.isLoggedIn) {
            val `in` = Intent(this@PickupLocationActivity, MainActivity::class.java)
            `in`.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(`in`)
            finish()
        } else {
            val `in` = Intent(this@PickupLocationActivity, LoginActivity::class.java)
            `in`.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(`in`)
            finish()
        }
    }

    private fun getAddress(lat: Double, lng: Double): String? {
        var city = ""
        var address = ""
        val addresses: List<Address>
        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geoCoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName // Only if available else return NULL
        } catch (e: Exception) {
            Log.i("LOCATION", e.message.toString() + " is message")
        }
        return address
    }

    @Throws(IOException::class)
    private fun getPostcodeByCoordinates(lat: Double, lon: Double): String? {
        val mGeoCoder = Geocoder(this@PickupLocationActivity, Locale.getDefault())
        val addresses = mGeoCoder.getFromLocation(lat, lon, 1)
        return if (addresses != null && addresses.size > 0) {
            addresses[0].postalCode
        } else null
    }
}