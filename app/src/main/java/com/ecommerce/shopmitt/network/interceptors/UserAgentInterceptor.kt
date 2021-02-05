package com.ecommerce.shopmitt.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val requestWithUserAgent = originRequest.newBuilder()
                .header("User-Agent", System.getProperty("http.agent").toString())
                .header("Authorization", PreferencesManager.instance.getString(PreferencesManager.KEY).toString())
                //.header("RequestedPlatform","Mobile")
                .build()

        Log.i("TOKEN",PreferencesManager.instance.getString(PreferencesManager.KEY).toString())

        return chain.proceed(requestWithUserAgent)
    }
}