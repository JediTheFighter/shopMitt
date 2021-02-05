

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.ecommerce.shopmitt.utils.AlisApplication
import com.ecommerce.shopmitt.utils.Constants
import java.util.*



@SuppressLint("CommitPrefEdits")
class PreferencesManager private constructor() {

    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    init {
        preferences = AlisApplication.instance.getSharedPreferences(
            Constants.APP_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        editor = preferences?.edit()
    }


    fun getString(key: String): String? {
        return try {
            preferences!!.getString(key, getDefaultStringValue(key))
        } catch (e: ClassCastException) {
            e.printStackTrace()
            ""
        }
    }

    fun setString(key: String?, value: String?) {
        editor!!.putString(key, value)
        editor!!.apply()
    }

    private fun getDefaultStringValue(key: String): String? {
        return  "" // add default values here
    }


    fun setInt(key: String?, value: Int) {
        editor!!.putInt(key, value)
        editor!!.apply()
    }

    fun getInt(key: String): Int {
        return preferences!!.getInt(key, getDefaultIntValue(key))
    }

    private fun getDefaultIntValue(key: String): Int {
        return 0
    }


    fun setBoolean(key: String?, value: Boolean) {
        editor!!.putBoolean(key, value)
        editor!!.apply()
    }

    fun getBoolean(key: String): Boolean {
        return preferences!!.getBoolean(key, getDefaultBooleanValue(key))
    }

    private fun getDefaultBooleanValue(key: String): Boolean {
        return key == KEY_NOTIFICATION_ENABLED || key == KEY_LOCATION_ENABLED
    }


    fun incrementNotificationCount() {
        setInt(KEY_NOTIFICATION_COUNT, getInt(KEY_NOTIFICATION_COUNT) + 1)
    }

    fun incrementCartCount() {
        setInt(KEY_CART_COUNT, getInt(KEY_CART_COUNT) + 1)
    }

    fun decrementCartCount() {
        setInt(KEY_CART_COUNT, getInt(KEY_CART_COUNT) - 1)
    }

    fun clearCartCount() {
        setInt(KEY_CART_COUNT, 0)
    }


    fun setList(key: String?, setList: List<String>?) {
        val set: Set<String> = HashSet(setList)
        editor!!.putStringSet(key, set)
        editor!!.apply()
    }

    fun getList(key: String?): Set<String?>? {
        return preferences!!.getStringSet(key, null)
    }


    /**
     * Clears the given key value from preference.
     */
    fun remove(key: String?) {
        editor!!.remove(key)
        editor!!.apply()
    }


    /**
     * Clear all preference.
     */
    fun clear() {
        editor!!.clear()
        editor!!.apply()
    }


    private object HOLDER {
        val INSTANCE = PreferencesManager()
    }

    companion object {

        const val KEY_CURR_LOCATION = "current_location"
        const val KEY_CUSTOMER_ID = "customer_id"
        const val KEY_USER_NAME = "customer_id"
        const val KEY_LOG_IN = "log_in"
        const val KEY = "current_key"
        const val KEY_OPENCART_TOKEN = "Basic YWxpczphbGlzMTIz" //beta grocery
        const val KEY_CART_COUNT = "cart_count"
        const val KEY_PERM_STATUS = "perm_status"
        const val KEY_FAV_STATUS = "fav_status"
        const val KEY_SEL_TIME = "time"
        const val KEY_SEL_DATE = "date"
        const val KEY_DOC_ID = "doctor_id"
        const val KEY_PHONE = "phone"
        const val KEY_LANGUAGE = "language"

        const val KEY_NOTIFICATION_COUNT = "notification_count "
        const val KEY_NOTIFICATION_ENABLED = "notification_enabled "
        const val KEY_LOCATION_ENABLED = "location_enabled"

        const val KEY_ACCESS_TOKEN = "access_token_"
        const val KEY_USER_MODEL = "user_model"
        const val KEY_USER_ID = "user_id"
        const val KEY_MENU_LIST = "menu_list"

        //val instance: com.shopping.alis.utils.LogHelper = HOLDER.INSTANCE
        val instance: PreferencesManager by lazy { HOLDER.INSTANCE }
    }

}