package com.ecommerce.shopmitt.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser


class Navigator {

    companion object {

        @Volatile
        private var INSTANCE: Navigator? = null

        fun getNavigator(context: Context): Navigator {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Navigator()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    fun navigateToForceUpdate(@Suppress("UNUSED_PARAMETER") activity: Activity?,
                              @Suppress("UNUSED_PARAMETER") isSkip: Boolean?) {


    }

    fun navigate(context: Context?, ActivityToOpen: Class<out Activity?>?) {
        val `in` = Intent(context, ActivityToOpen)
        context?.startActivity(`in`)
    }

    fun navigateByClear(context: Context?, ActivityToOpen: Class<out Activity?>?) {
        val `in` = Intent(context, ActivityToOpen)
        `in`.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(`in`)
    }


    fun openBrowser(context: Context?, url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.putExtra(Browser.EXTRA_APPLICATION_ID,
                context?.packageName)
        context?.startActivity(intent)
    }



    fun navigate(context: Context, intent: Intent?) {
        context.startActivity(intent)
    }



}