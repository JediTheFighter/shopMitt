package com.ecommerce.shopmitt.network

import android.content.Context
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.utils.AlisApplication
import com.ecommerce.shopmitt.utils.Constants
import com.ecommerce.shopmitt.utils.LogHelper
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.LoginActivity
import io.reactivex.exceptions.CompositeException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException


class RestServiceResponse private constructor() {

    fun handleResults(obj: Any, helper: RestResponseHandler?) {
        LogHelper.instance.printInfoLog("handleResults")
        helper?.onSuccess(obj)
    }

    fun handleError(throwable: Throwable?, helper: RestResponseHandler?, context: Context?) {
        LogHelper.instance.printErrorLog("handleError")
        val errorBody: String?
        var statusCode = 0
        var statusMessage: String? = null
        if (!NetworkUtil.instance.hasNetworkConnection()) {
            ToastHelper.instance.show(context?.getString(R.string.no_internet_connection_available))
            helper?.onError(Constants.STATUS_CODE_NO_CONNECTION, context?.getString(R.string.no_internet_connection_available), true)
            return
        }
        if (throwable != null) {
            throwable.printStackTrace()
            errorBody = throwable.message
            LogHelper.instance.printErrorLog("errorBody : $errorBody")

            try {

                val error = throwable as HttpException
                val errorJson = error.response()!!.errorBody()!!.string()
                LogHelper.instance.printInfoLog("errorParsed: $errorJson")
                val obj = JSONObject(errorJson)

                statusMessage = obj.getJSONArray("error")[0].toString()
                LogHelper.instance.printErrorLog("message : $statusMessage")
                statusCode = obj.getInt("_status")
                LogHelper.instance.printErrorLog("status : $statusCode")
                if (statusCode == 403) {
                    AppDataManager.instance.clearLoginData()
                    AlisApplication.instance.navigator.navigate(context, LoginActivity::class.java)
                    return
                }


            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: ClassCastException) {
                e.printStackTrace()
            } catch (e: CompositeException) {
                e.printStackTrace()
            }
        } else {
            LogHelper.instance.printErrorLog("Throwable Null")
        }
        if (ValidationUtils.instance.isEmpty(statusMessage)) {
            statusMessage = context?.getString(R.string.server_error_try_again_later)
        }
        helper?.onError(statusCode, statusMessage, false)
    }


    private object HOLDER {
        val INSTANCE = RestServiceResponse()
    }

    companion object {
        val instance: RestServiceResponse by lazy { HOLDER.INSTANCE }
    }

}