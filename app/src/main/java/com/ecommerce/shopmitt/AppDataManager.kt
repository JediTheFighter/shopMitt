package com.ecommerce.shopmitt

import android.content.Context
import com.ecommerce.shopmitt.utils.AlisApplication
import com.ecommerce.shopmitt.utils.Constants

class AppDataManager private constructor() {

    val isSessionLive: Boolean
        get() {
            val accessToken = PreferencesManager.instance
                    .getString(PreferencesManager.KEY_ACCESS_TOKEN)
            return !accessToken.isNullOrEmpty()
        }

    val isLoggedIn: Boolean
    get() {
        val status = PreferencesManager.instance.getString(PreferencesManager.KEY_LOG_IN)
        return !status.isNullOrEmpty()
    }

    /* Language Helpers */
    fun saveLanguage(language: String?) {
        val preferences = AlisApplication.instance.getSharedPreferences(
            Constants.LANGUAGE_PREFERENCE_NAME,
                        Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.putString(PreferencesManager.KEY_LANGUAGE, language)
        editor?.apply()
    }

    val savedLanguage: String?
        get() {
            val preferences = AlisApplication.instance.getSharedPreferences(
                Constants.LANGUAGE_PREFERENCE_NAME,
                            Context.MODE_PRIVATE)
            return preferences?.getString(PreferencesManager.KEY_LANGUAGE, Constants.NO_LANGUAGE)
        }



    fun setNotificationCount() {
        PreferencesManager.instance.incrementNotificationCount()
    }

    fun enableNotification() {
        PreferencesManager.instance.setBoolean(PreferencesManager.KEY_NOTIFICATION_ENABLED,true)
    }

    fun disableNotification() {
        PreferencesManager.instance.setBoolean(PreferencesManager.KEY_NOTIFICATION_ENABLED,false)
    }

    fun isNotificationEnabled() : Boolean {
        return PreferencesManager.instance.getBoolean(PreferencesManager.KEY_NOTIFICATION_ENABLED)
    }

    val notificationCount: Int
        get() = PreferencesManager.instance.getInt(PreferencesManager.KEY_NOTIFICATION_COUNT)

    fun clearNotificationCount() {
        PreferencesManager.instance.setInt(PreferencesManager.KEY_NOTIFICATION_COUNT, 0)
    }

    val cartCount: Int
        get() = PreferencesManager.instance.getInt(PreferencesManager.KEY_CART_COUNT)

    fun incrementCartCount() {
        PreferencesManager.instance.incrementCartCount()
    }

    fun decrementCartCount() {
        PreferencesManager.instance.decrementCartCount()
    }

    fun clearCartCount() {
        PreferencesManager.instance.clearCartCount()
    }


    /**
     * Get User Session token
     *
     * @return String session token
     */
    /**
     * Save User Session token
     *
     * @param token session token
     */
    var sessionToken: String?
        get() = PreferencesManager.instance
                .getString(PreferencesManager.KEY_ACCESS_TOKEN)
        private set(token) {
            PreferencesManager.instance.setString(PreferencesManager.KEY_ACCESS_TOKEN, token)
        }

    var customerId: String?
    get() = PreferencesManager.instance.getString(PreferencesManager.KEY_CUSTOMER_ID)
    private set(id) {
        PreferencesManager.instance.setString(PreferencesManager.KEY_CUSTOMER_ID,id)
    }

    var userName: String?
    get() = PreferencesManager.instance.getString(PreferencesManager.KEY_USER_NAME)
    private set(name) {
        PreferencesManager.instance.setString(PreferencesManager.KEY_USER_NAME,name)
    }

    /*fun saveLoginData(model: LoginModel, isWithToken: Boolean) {
        val manager = instance
        if (isWithToken) {
            sessionToken = model.data!!.accessToken
            PreferencesManager.instance.setString(PreferencesManager.KEY_ACCESS_TOKEN, sessionToken)
            PreferencesManager.instance.setString(PreferencesManager.KEY_PHONE,model.data?.mobile)
        }
        val gson = Gson()
        val userJson = gson.toJson(model)
        PreferencesManager.instance.setString(PreferencesManager.KEY_USER_MODEL, userJson)
    }*/

    /*val loginData: LoginModel
        get() {
            val gson = Gson()
            val userJson = PreferencesManager.instance.getString(PreferencesManager.KEY_USER_MODEL)
            return gson.fromJson(userJson, LoginModel::class.java)
        }*/

    fun clearLoginData() {
        PreferencesManager.instance.setString(PreferencesManager.KEY_LOG_IN,"")
        PreferencesManager.instance.setString(PreferencesManager.KEY_USER_NAME,"")
        PreferencesManager.instance.setString(PreferencesManager.KEY_CUSTOMER_ID,"")
        PreferencesManager.instance.setString(PreferencesManager.KEY_ACCESS_TOKEN, "")
        //AppDataHolder.getInstance().clearAppDataCache();
    }



    private object HOLDER {
        val INSTANCE = AppDataManager()
    }

    companion object {
        //val instance: com.shopping.alis.utils.LogHelper = HOLDER.INSTANCE
        val instance: AppDataManager by lazy { HOLDER.INSTANCE }
    }

}