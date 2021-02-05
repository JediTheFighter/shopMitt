

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.TypedValue
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.utils.LogHelper
import java.util.*

/**
 * The class DeviceInfoUtils
 */
class DeviceInfoUtils private constructor() {
    /**
     * Get the current Android API level.
     *
     * @return the sdk version
     */
    val sdkVersion: Int
        get() = Build.VERSION.SDK_INT

    /**
     * method returns the app version that user installed
     *
     * @param context- Activity context
     * @return - app version string value
     */
    fun getAppCurrentVersion(context: Context): Int {
        var version = 0
        try {
            val pInfo = context.packageManager
                    .getPackageInfo(context.packageName, 0)
            version = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        LogHelper.instance.printInfoLog("App Version : $version")
        return version
    }

    /**
     * Determine devices screen resolution.
     *
     * @param context the context
     * @return the resolution
     */
    fun getResolution(context: Context): ResolutionInfo {
        val resolutionInfo = ResolutionInfo()
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay
                .getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        resolutionInfo.width = width
        resolutionInfo.height = height
        return resolutionInfo
    }

    /**
     * Determine if the device is a tablet (i.e. it has a large screen).
     *
     * @param context The calling context.
     * @return the boolean
     */
    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * Determine devices screen density.
     *
     * @param context The calling context.
     * @return the density
     */
    fun getDensity(context: Context): String {
        val density = context.resources.displayMetrics.density
        LogHelper.instance.printInfoLog("getDensity : $density")
        if (density >= 4.0) {
            return "xxxhdpi  " + density * 160
        }
        if (density >= 3.0) {
            return "xxhdpi  " + density * 160
        }
        if (density >= 2.0) {
            return "xhdpi  " + density * 160
        }
        if (density >= 1.5) {
            return "hdpi  " + density * 160
        }
        return if (density >= 1.0) {
            "mdpi  " + density * 160
        } else "ldpi  " + density * 160
    }

    val deviceDetails: String
        get() = Build.MANUFACTURER + " " + Build.MODEL + "|" +
                Build.VERSION.RELEASE

    /*
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    fun getUserCountry(context: Context): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var simCountry: String? = null
            simCountry = tm.simCountryIso
            if (!(simCountry == null || simCountry.length != 2)) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US)
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US)
                }
            }
        } catch (e: Exception) {
        }
        return null
    }



    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getActionbarSize(context: Context): Int {
        var actionBarHeight = 150
        val tv = TypedValue()
        if (context.theme.resolveAttribute(R.attr.actionBarSize,
                        tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    context.resources.displayMetrics)
        }
        return actionBarHeight
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    class ResolutionInfo {
        var width = 0
        var height = 0

    }

    private object HOLDER {
        val INSTANCE = DeviceInfoUtils()
    }

    companion object {
        //val instance: com.shopping.alis.utils.LogHelper = HOLDER.INSTANCE
        val instance: DeviceInfoUtils by lazy { HOLDER.INSTANCE }
    }

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    /*public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) AlisApplication.instance.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (ActivityCompat.checkSelfPermission(AlisApplication.instance,
                            Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                //return TODO;
            }
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(AlisApplication.instance.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }*/

}