package com.ecommerce.shopmitt.views.activities


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.BuildConfig
import com.ecommerce.shopmitt.MainActivity
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityMapBinding
import com.ecommerce.shopmitt.utils.LocationUtil
import com.ecommerce.shopmitt.views.adapters.DestinationPlacePickerAdapter
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.dialog.*
import com.ecommerce.shopmitt.models.GooglePlaceAddressModel
import com.ecommerce.shopmitt.models.PlaceData
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.FROM_CHANGE_ADDRESS
import com.ecommerce.shopmitt.utils.Constants.FROM_DASH
import com.ecommerce.shopmitt.utils.Constants.FROM_PICKUP
import com.ecommerce.shopmitt.utils.Constants.FROM_SHIPPING
import com.ecommerce.shopmitt.utils.LogHelper
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.list_places.*
import kotlinx.android.synthetic.main.toolbar.*
import permissions.dispatcher.*
import java.util.*
import kotlin.math.ln

@RuntimePermissions
class MapActivity : BaseActivity(), OnMapReadyCallback, SearchView.OnQueryTextListener, LocationUtil.OneShotLocationListener {

    private var isLocationAssigned: Boolean = false

    private var disposableGetPlaceAddress: Disposable? = null
    private lateinit var adapter: DestinationPlacePickerAdapter
    private lateinit var sourceLatLng: LatLng
    private lateinit var mMap: GoogleMap
    private var locationUtil: LocationUtil? = null
    private var location: String? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var searchView: SearchView
    lateinit var mapFragment: SupportMapFragment
    private val REQUEST_CHECK_LOCATION_SETTINGS = 1
    private val PERMISSION_CHECK = 212

    var lat = 0.0
    var lng = 0.0

    var permission = Manifest.permission.ACCESS_FINE_LOCATION

    var locationManager: LocationManager? = null

    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setUpToolbar(R.id.toolbar)
        setUpHomeUpBackNavigation(R.drawable.arrow_back)
        tvToolbarTitle.text = getString(R.string.select_location)

        Places.initialize(applicationContext, BuildConfig.MAPS_KEY)
        placesClient = Places
                .createClient(this)

        getCurrentLocationWithPermissionCheck()

        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)


        btnCurrLocation.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
                    if (isLocationAssigned)
                        locationUtil?.getLocation(this)
                    else {
                        getToast().show("Fetching location...")
                        locationUtil?.getLocation(this)
                    }
                } else Toast.makeText(this@MapActivity,
                    getString(R.string.unable_to_get_location),
                    Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_CHECK)
            }

        }

        btnSetLocation.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                getAddressFromLatLng(sourceLatLng.latitude, sourceLatLng.longitude)
                if(location.isNullOrEmpty())
                    getToast().show(getString(R.string.fetching_location))
                else
                    showPlacePickerDialog(sourceLatLng.latitude.toString(),
                        sourceLatLng.longitude.toString(),
                        location)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_CHECK)
            }

        }

    }

    override fun onResume() {
        super.onResume()
        locationUtil = LocationUtil(this)
    }

    private fun isPermissionGranted(permission: Boolean) {
        if (!permission) {
            Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        } else {
            checkGPSAndGetLocationWithPermissionCheck()
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getCurrentLocation() {

        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)

        checkGPSAndGetLocationWithPermissionCheck()
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
                if(lat > 0.0)
                    animateCameraToLocationLATLNG(lat,lng)
                else
                    locationUtil?.getLocation(this)

            })
        task.addOnFailureListener(this, OnFailureListener { e ->
            LogHelper.instance.printErrorLog("Location Resolution onFailure")
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this,
                        REQUEST_CHECK_LOCATION_SETTINGS)
                    LogHelper.instance.printInfoLog("Location Resolution error show dialog")
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_LOCATION_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    locationUtil?.getLocation(this)
                }
                Activity.RESULT_CANCELED -> {
                    checkGPSAndGetLocationWithPermissionCheck()
                }
            }
        }
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
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)))
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


    private fun showPlacePickerDialog(lat: String, lng: String, place_name: String?) {

        val params: DialogParams = Builder()
            .cancelable(true).dgType(DialogType.DG_POS_NEG)
            .dialogId(DialogConstants.DIALOG_ADD_LOCATION)
            .title(getString(R.string.set_location))
            .message(place_name.toString())
            .positive(getString(R.string.ok_text))
            .negative(getString(R.string.cancel))
            .build()

        DialogHelper(this, params, object : DialogCallback {
            override fun onButtonPositive(dialogId: Int) {
                LogHelper.instance.printInfoLog("onButtonPositive : Dialog Id : $dialogId")

                handleRouting(place_name,lat,lng)
            }

            override fun onButtonNegative(dialogId: Int) {
                LogHelper.instance.printInfoLog("onButtonNegative : Dialog Id : $dialogId")
            }
        }).showDialog(true)
    }

    private fun handleRouting(place_name: String?, lat: String, lng: String) {

        val from = intent.getIntExtra("from",0)

        if (from == FROM_SHIPPING) {
            val intent = Intent(this, ShippingActivity::class.java)
            intent.putExtra("location", place_name)
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (from == FROM_DASH) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("location", place_name)
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (from == FROM_CHANGE_ADDRESS) {
            val intent = Intent(this, ChangeAddressActivity::class.java)
            intent.putExtra("location", place_name)
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (from == FROM_PICKUP) {
            val intent = Intent(this, PickupLocationActivity::class.java)
            intent.putExtra("location", place_name)
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }


    override val fragmentContainer: Int
        get() = 0

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = false

        mMap.uiSettings.isMyLocationButtonEnabled = false

        if(lat > 0.0) {
            animateCameraToLocationLATLNG(lat,lng)
        }

        sourceLatLng = mMap.cameraPosition.target
        mMap.setOnCameraIdleListener {
            val latLng = mMap.cameraPosition.target
            if (isLocationChanged(sourceLatLng, latLng, 1)) {
                sourceLatLng = latLng
                getAddressFromLatLng(latLng.latitude,
                    latLng.longitude)
            }
        }
    }

    private fun isLocationChanged(oldLatLng: LatLng?, newLatLng: LatLng?, radius: Int): Boolean { //LogHelper.instance.printInfoLog("isLocationChanged");
        //LogHelper.instance.printInfoLog("sourceLatLng : " + oldLatLng);
        //LogHelper.instance.printInfoLog("newLatLng : " + newLatLng);
        val distanceMoved = FloatArray(1)
        return run {
            Location.distanceBetween(oldLatLng!!.latitude, oldLatLng.longitude,
                newLatLng!!.latitude, newLatLng.longitude, distanceMoved)
            //LogHelper.instance.printWarningLog("Distance Moved : " + distanceMoved[0]);
            distanceMoved[0] > radius
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double) {
        Log.i("INFO", "reached")
        val handler = Handler(Handler.Callback { msg: Message ->
            if (!msg.data.isEmpty) {
                val addressToPrint = msg.data.getString("address")
                if (addressToPrint == null) {
//                    getAddressFromApi(sourceLatLng.latitude,sourceLatLng.longitude)
                } else {
                    location = addressToPrint
                }
            }
            true
        })
        locationUtil?.getAddressFromLocation(latitude, longitude, this, handler)
    }

    private fun getAddressFromApi(lat: Double, lng: Double) {
        val base = getString(R.string.google_place_url)
        val latLng = "latlng=$lat,$lng"
        val keyHint = "&key="
        val key = BuildConfig.MAPS_KEY
        val url = base + latLng + keyHint + key
        val address = arrayOfNulls<String>(1)
        LogHelper.instance.printInfoLog("getAddressFromApi")
        LogHelper.instance.printInfoLog("url : $url")

        disposableGetPlaceAddress = RestHelper(object : RestResponseHandler {

            override fun onSuccess(`object`: Any?) {
                LogHelper.instance.printInfoLog("onSuccess")
                val model: GooglePlaceAddressModel = `object` as GooglePlaceAddressModel
                val listResults: List<GooglePlaceAddressModel.Result?>? = model.getResults()
                if (listResults?.isNotEmpty()!!) {
                    val result: GooglePlaceAddressModel.Result = listResults[0]!!
                    address[0] = result.formattedAddress
                    LogHelper.instance.printInfoLog("Address Got From Api: " + address[0])
                    location = address[0]
                } else {
                    getToast().show("address not available")
                }
            }

            override fun onError(
                statusCode: Int,
                statusMessage: String?, retry: Boolean,
            ) { //hideLoadingDialog();
                LogHelper.instance.printInfoLog("onError")
                getToast().show("address not available")
            }
        }, this).getPlaceAddress(url)
    }

    private fun animateCameraToLocation(location: Location?) {
        if (location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
            mMap.animateCamera(cameraUpdate)
        }
    }

    private fun animateCameraToLocationLATLNG(lat: Double, lng: Double) {
        val latLng = LatLng(lat, lng)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
        mMap.animateCamera(cameraUpdate)
    }

    override fun onLocationSuccess(location: Location?) {

        locationUtil?.dispose()

        if (location != null) {
            sourceLatLng = LatLng(location.latitude, location.longitude)
            isLocationAssigned = true
        }


        location?.latitude?.let { animateCameraToLocationLATLNG(it, location.longitude) }
    }

    override fun onLocationError(error: String?) {
        getToast().show("Failed to fetch location")
        isLocationAssigned = false
    }

    override fun startLocationSettingsResolution(
        LocationSettingsType: Int,
        resolvableApiException: ResolvableApiException?,
    ) {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.searchBar)
        searchView = searchItem.actionView as SearchView
        try {
            val searchIcon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
            searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite),
                PorterDuff.Mode.SRC_IN)
            val searchText: TextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            val myCustomFont = ResourcesCompat.getFont(this, R.font.regular)
            searchText.typeface = myCustomFont
            searchText.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            searchText.inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            val mCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            mCursorDrawableRes.isAccessible = true
            mCursorDrawableRes[searchText] = 0 //This sets the cursor resource ID to 0 or @null which will make it visible on white background

            // Get the search close button image view
            val closeBtn: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView

            // Set on click listener
            closeBtn.setOnClickListener {

                //Find EditText view
                val et = findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText

                //Clear the text from EditText view
                et.setText("")

                //Clear query
                searchView.setQuery("", false)
                searchView.isIconified = true

                tvToolbarTitle.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        searchView.setOnQueryTextListener(this)
        searchView.queryHint = getString(R.string.enter_location)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.searchBar -> {
                searchView.isIconified = false
                tvToolbarTitle.visibility = View.GONE
            }
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            searchPlace(query)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_CHECK)
        }

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            searchPlace(newText)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_CHECK)
        }
        return false
    }

    private fun searchPlace(query: String?) {
        if (query != null && query.isNotEmpty()) {

            filterPlace(query)
            listPlaces.visibility = View.VISIBLE
            val fm = supportFragmentManager
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(mapFragment)
                    .commit()
            btnCurrLocation.visibility = View.GONE
            btnSetLocation.visibility = View.GONE
            marker.visibility = View.GONE
            tvToolbarTitle.visibility = View.GONE
        } else {
            listPlaces.visibility = View.GONE
            val fm = supportFragmentManager
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(mapFragment)
                    .commit()
            btnCurrLocation.visibility = View.VISIBLE
            btnSetLocation.visibility = View.VISIBLE
            marker.visibility = View.VISIBLE
            tvToolbarTitle.visibility = View.VISIBLE
        }
    }

    private fun filterPlace(query: String) {
        val placesList: MutableList<PlaceData> = ArrayList<PlaceData>()
        val token = AutocompleteSessionToken.newInstance()
        val biasLocation: LatLng
        /*var bounds: RectangularBounds? = null
        try {
            biasLocation = LatLng(sourceLatLng.latitude,
                    sourceLatLng.longitude)
            LogHelper.instance.printWarningLog("Bias Location : $biasLocation")
            val latLngBounds = LocationUtil.getLatLngBounds(biasLocation,
                    Constants.PLACE_PICKER_LOCATION_BIAS_RADIUS)
            bounds = RectangularBounds.newInstance(latLngBounds)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }*/
        val request = FindAutocompletePredictionsRequest.builder()
//                .setLocationBias(bounds) //.setLocationRestriction(bounds)
//                .setCountry(Constants.PLACE_PICKER_COUNTRY)
//.setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build()
        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
            for (prediction in response.autocompletePredictions) {
                val placeData = PlaceData()
                val placeId = prediction.placeId
                val fullText = prediction.getFullText(null).toString()
                val primaryText = prediction.getPrimaryText(null).toString()
                val secondaryText = prediction.getSecondaryText(null).toString()
                placeData.setPlaceId(placeId)
                placeData.setPlaceText(fullText)
                placeData.setPrimaryText(primaryText)
                placeData.setSecondaryText(secondaryText)
                LogHelper.instance.printInfoLog("placeId: $placeId")
                LogHelper.instance.printInfoLog("fullText: $fullText")
                LogHelper.instance.printInfoLog("primaryText: $primaryText")
                LogHelper.instance.printInfoLog("secondaryText: $secondaryText")
                placesList.add(placeData)
            }
            val layoutManager = LinearLayoutManager(this@MapActivity,
                LinearLayoutManager.VERTICAL,
                false)
            listPlaces.layoutManager = layoutManager
            adapter = DestinationPlacePickerAdapter(this@MapActivity,
                placesList, true, object : DestinationPlacePickerAdapter.SelectDestination {
                    override fun onSelectDestination(
                        lat: String?,
                        lng: String?,
                        place_name: String?,
                    ) {

                        searchView.setQuery("", false)
                        searchView.isIconified = true
                        tvToolbarTitle.visibility = View.VISIBLE

                        hideKeyboard(this@MapActivity)
                        listPlaces.visibility = View.GONE

                        val fm = supportFragmentManager
                        fm.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .show(mapFragment)
                            .commit()

                        btnSetLocation.visibility = View.VISIBLE
                        btnCurrLocation.visibility = View.VISIBLE
                        marker.visibility = View.VISIBLE
                        tvToolbarTitle.visibility = View.VISIBLE

                        animateCameraToLocationLATLNG(lat!!.toDouble(), lng!!.toDouble())


                    }

                })
            listPlaces.adapter = adapter
            listPlaces.isNestedScrollingEnabled = false
            LogHelper.instance.printInfoLog("Auto complete predictions size: "
                    + placesList.size)
        }.addOnFailureListener { exception: java.lang.Exception? ->
            if (exception is ApiException) {
                LogHelper.instance.printInfoLog("Place not found: " + exception.statusCode)
            }
        }
    }


    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableGetPlaceAddress)
    }

}
