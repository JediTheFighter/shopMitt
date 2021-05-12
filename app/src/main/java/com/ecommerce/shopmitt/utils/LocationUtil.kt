package com.ecommerce.shopmitt.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.ecommerce.shopmitt.utils.AlisApplication
import com.ecommerce.shopmitt.utils.Constants
import com.ecommerce.shopmitt.utils.LogHelper
import java.io.IOException
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class LocationUtil(private var activity: Activity?) {
    
    private val LOG = "LOCATION UTILS"
    private var listener: OneShotLocationListener? = null
    private val mFusedLocationClient: FusedLocationProviderClient
    private var mLocationCallback: LocationCallback? = null
    private val mServiceHandler: Handler?
    fun getLocation(locationListener: OneShotLocationListener?) {
        listener = locationListener
        prepare()
    }

    private fun prepare() {
        LogHelper.instance.printErrorLog("$LOG On prepare.")
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult.lastLocation
                LogHelper.instance.printDebugLog("onLocationResult : $lastLocation")
                //String provider = lastLocation.getProvider();
//com.shopping.alis.utils.LogHelper.instance..printInfoLog("provider: " + provider);
                if (listener != null) listener!!.onLocationSuccess(lastLocation)
            }
        }
        //getLastLocation();
        checkGPSAndRequestLocation()
    }

    private fun requestLocationUpdates() {
        LogHelper.instance.printInfoLog("Requesting Location Updates")
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            LogHelper.instance.printDebugLog("Lost location permission. " +
                    "Could not request updates." + unlikely)
        }
    }



    private fun checkGPSAndRequestLocation() {
        if (ActivityCompat.checkSelfPermission(
                AlisApplication.instance,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            LogHelper.instance.printErrorLog(LOG + "Location permission not granted...")
            listener!!.startLocationSettingsResolution(Constants.LOCATION_SETTINGS_TYPE_PERMISSION, null)
            return
        }
        // Enable GPS
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(activity!!)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(activity!!) {
            // All location settings are satisfied. The client can initialize
// location requests here.
            LogHelper.instance.printInfoLog("Location Resolution onSuccess")
            requestLocationUpdates()
        }
        task.addOnFailureListener(activity!!) { e ->
            LogHelper.instance.printErrorLog("Location Resolution onFailure")
            if (e is ResolvableApiException) { // Location settings are not satisfied, but this can be fixed
// by showing the user a dialog.
// Show the dialog by calling startResolutionForResult(),
// and check the result in onActivityResult().
                LogHelper.instance.printInfoLog("Location Resolution error show dialog")
                listener!!.startLocationSettingsResolution(Constants.LOCATION_SETTINGS_TYPE_GPS, e)
            }
        }
    }

    val lastLocation: Unit
        get() {
            LogHelper.instance.printInfoLog("Getting Last Location")
            if (ActivityCompat.checkSelfPermission(
                    AlisApplication.instance,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                LogHelper.instance.printErrorLog(LOG + "Location permission not granted...")
                listener!!.startLocationSettingsResolution(Constants.LOCATION_SETTINGS_TYPE_PERMISSION, null)
                return
            }
            if (activity == null) {
                return
            }
            try {
                mFusedLocationClient.lastLocation
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful && task.result != null) {
                                val result = task.result
                                LogHelper.instance.printInfoLog("onComplete : $result")
                                performOperationsWithLastLocation(result)
                            } else {
                                LogHelper.instance.printInfoLog("Failed to get location.")
                                checkGPSAndRequestLocation()
                            }
                        }
            } catch (unlikely: SecurityException) {
                LogHelper.instance.printInfoLog("Lost location permission.$unlikely")
            }
        }

    private fun performOperationsWithLastLocation(lastLocation: Location?) {
        val accuracy = lastLocation!!.accuracy
        LogHelper.instance.printInfoLog("Last Location Accuracy: $accuracy")
        if (accuracy < 50) { //if accuracy is less than 50 meters, take it as valid location
            if (listener != null) listener!!.onLocationSuccess(lastLocation)
        } else {
            LogHelper.instance.printErrorLog(LOG + " : Location accuracy ( " +
                    lastLocation.accuracy
                    + " ) is not satisfied, try to get a fresh location update.")
            checkGPSAndRequestLocation()
        }
    }

    fun dispose() {
        stop()
        listener = null
    }

    fun stop() {
        removeLocationUpdates()
        mServiceHandler?.removeCallbacksAndMessages(null)
    }

    private fun removeLocationUpdates() {
        LogHelper.instance.printDebugLog("Removing location updates")
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        } catch (unlikely: SecurityException) {
            LogHelper.instance.printDebugLog("Lost location permission. " +
                    "Could not remove updates. " + unlikely)
            listener!!.onLocationError("Lost location permission. Could not remove updates.$unlikely")
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    interface OneShotLocationListener {
        fun onLocationSuccess(location: Location?)
        fun onLocationError(error: String?)
        fun startLocationSettingsResolution(LocationSettingsType: Int, resolvableApiException: ResolvableApiException?)
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) { // A new location is always better than no location
            return true
        }
        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0
        // If it's been more than two minutes since the current location, use the new location
// because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }
        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200
        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(location.provider,
                currentBestLocation.provider)
        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    /**
     * Checks whether two providers are the same
     */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }

    companion object {
        private const val TWO_MINUTES = 1000 * 60 * 2
        private val TAG = LocationUtil::class.java.simpleName
        // 1 seconds, in milliseconds
        // 1 second, in milliseconds
        // 1 meter
        val locationRequest: LocationRequest
            get() = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1 * 1000.toLong()) // 1 seconds, in milliseconds
                    .setFastestInterval(1000) // 1 second, in milliseconds
                    .setSmallestDisplacement(1f) // 1 meter

        val isGPSEnabled: Boolean
            get() {
                val lm = AlisApplication.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }

        /**
         * Get the distance between two locations by giving their respective latitude and longitude
         */
        private fun distanceTo(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val theta = lon1 - lon2
            var dist = (sin(deg2rad(lat1))
                    * sin(deg2rad(lat2))
                    + (cos(deg2rad(lat1))
                    * cos(deg2rad(lat2))
                    * cos(deg2rad(theta))))
            dist = acos(dist)
            dist = rad2deg(dist)
            dist *= 60 * 1.1515
            return dist
        }

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }

        /**
         * Get latLng bounds within a specified location around given radius
         *
         * @param latLng            current location
         * @param mDistanceInMeters circle radius from given locations
         */
        @Throws(NullPointerException::class)
        fun getLatLngBounds(latLng: LatLng, mDistanceInMeters: Int): LatLngBounds {
            val latRadian = Math.toRadians(latLng.latitude)
            val degLatKm = 110.574235
            val degLongKm = 110.572833 * Math.cos(latRadian)
            val deltaLat = mDistanceInMeters / 1000.0 / degLatKm
            val deltaLong = mDistanceInMeters / 1000.0 / degLongKm
            val minLat = latLng.latitude - deltaLat
            val minLong = latLng.longitude - deltaLong
            val maxLat = latLng.latitude + deltaLat
            val maxLong = latLng.longitude + deltaLong
            LogHelper.instance.printInfoLog("Location Bias - Min: $minLat,$minLong")
            LogHelper.instance.printInfoLog("Location Bias - Max: $maxLat,$maxLong")
            return LatLngBounds(LatLng(minLat, minLong), LatLng(maxLat, maxLong))
        }
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double,
                               context: Context?, handler: Handler?) {
        val thread: Thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList = geocoder.getFromLocation(
                            latitude, longitude, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0]
                        val sb = StringBuilder()
                        for (i in 0..address.maxAddressLineIndex) {
                            sb.append(address.getAddressLine(i)).append(",")
                        }
                        //sb.append(address.getLocality()).append(",");
                        //sb.append(address.getPostalCode()).append(",");
                        //sb.append(address.getCountryName());
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    LogHelper.instance.printErrorLog("Unable to connect Geocode : $e")
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    if (result != null) {
                        message.what = 1
                        val bundle = Bundle()
                        // result = "Latitude: " + latitude + " Longitude: " + longitude +
//        "\n\nAddress:\n" + result;
                        bundle.putString("address", result)
                        message.data = bundle
                    } else {
                        message.what = 1
                        val bundle = Bundle()
                        //result = "Latitude: " + latitude + " Longitude: " + longitude +
//       "\n Unable to get address for this lat-long.";
                        bundle.putString("address", null)
                        message.data = bundle
                    }
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }

    init {
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AlisApplication.instance)
    }
}