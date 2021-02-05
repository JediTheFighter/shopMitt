package com.ecommerce.shopmitt.network


interface RestResponseHandler {

    fun onSuccess(`object`: Any?)
    fun onError(statusCode: Int, statusMessage: String?, retry: Boolean)
}