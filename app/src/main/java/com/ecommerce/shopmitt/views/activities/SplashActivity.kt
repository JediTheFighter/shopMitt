package com.ecommerce.shopmitt.views.activities

import PreferencesManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.BuildConfig
import com.ecommerce.shopmitt.MainActivity
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.dialog.*
import com.ecommerce.shopmitt.models.GenericModel
import com.ecommerce.shopmitt.models.TokenModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.disposables.Disposable


class SplashActivity : BaseActivity() {

    private var disposableSplash: Disposable? = null

    override val fragmentContainer: Int
        get() = 0

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                val fbToken = it.result.toString()
                Log.v("FCM TOKEN",fbToken)
            }
        }

        checkAppVersion()
    }

    private fun checkAppVersion() {
        disposableSplash = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {

                val model = `object` as GenericModel
                if (model.data?.message!!.contains("Updates are available")) {
                    showUpdateDialog()
                } else {
                    proceedFurther()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {

            }

        }, this).checkForceUpdate(BuildConfig.VERSION_CODE, "android")
    }

    private fun showUpdateDialog() {
        val params: DialogParams = Builder()
            .cancelable(false).dgType(DialogType.DG_POS_ONLY)
            .title(getString(R.string.update_available))
            .message(getString(R.string.there_is_newer_version_available))
            .positive(getString(R.string.ok))
            .build()


        DialogHelper(this, params, object : DialogCallback {
            override fun onButtonPositive(dialogId: Int) {
                moveToPlayStore()
            }

            override fun onButtonNegative(dialogId: Int) {

            }
        }).showDialog(true)
    }

    private fun moveToPlayStore() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName&hl=en")
                )
            )
        }
        finish()
    }

    private fun proceedFurther() {
        if (AppDataManager.instance.isLoggedIn) {

            handler.postDelayed(Runnable {
                launchHome()
            }, 1000)

        } else {
            PreferencesManager.instance.setString(
                PreferencesManager.KEY,
                PreferencesManager.KEY_OPENCART_TOKEN
            )
            getToken()
        }
    }

    private fun getToken() {
        disposableSplash = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {


                val model = `object` as TokenModel

                val key: String =
                    model.data?.tokenType.toString() + " " + model.data?.accessToken
                PreferencesManager.instance.setString(PreferencesManager.KEY, key)
                PreferencesManager.instance.setString(PreferencesManager.KEY_ACCESS_TOKEN, key)

                handler.post(
                    Runnable {
                        navigator.navigate(this@SplashActivity, LoginActivity::class.java)
                        finish()
                    },
                )
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {

            }

        }, this).getToken()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableSplash)
    }

    private fun launchHome() {
        if (PreferencesManager.instance.getString(PreferencesManager.KEY_CURR_LOCATION)
                .equals("")
        ) {
            launchPlacePicker()
        } else {

            val `in` = Intent(this, MainActivity::class.java)
            `in`.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(`in`)
            finish()
        }
    }

    private fun launchPlacePicker() {
        val `in` = Intent(this@SplashActivity, PickupLocationActivity::class.java)
        startActivity(`in`)
        finish()
    }
}