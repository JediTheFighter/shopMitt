package com.ecommerce.shopmitt.utils

import android.text.TextUtils
import android.util.Log


class LogHelper private constructor() {

    /**
     * The tag to identify the log is from which module.
     */
// private static String TAG = ApplicationContext.TAG;
    private val TAG = "GD"

    /**
     * The tag to identify the rest client debug logs
     */
    private val DEBUG_TAG = "RC-DEBUG LOG : "

    /**
     * Prints the error log message in log cat.
     *
     * @param logMessage the error log message to be displayed.
     */
    fun printErrorLog(logMessage: String) {
        if (!TextUtils.isEmpty(logMessage)) Log.e(TAG, logMessage)
    }

    /**
     * Prints the ic_warning log message in log cat.
     *
     * @param logMessage the waring log message to be displayed.
     */
    fun printWarningLog(logMessage: String) {
        if (!TextUtils.isEmpty(logMessage)) Log.w(TAG, logMessage)
    }

    /////////////

    /////////////
    /**
     * Prints the debug log message in log cat.
     *
     * @param logMessage the debug log message to be displayed.
     */
    fun printDebugLog(logMessage: String) {
        if (!TextUtils.isEmpty(logMessage)) Log.d(TAG, logMessage)
    }

    /**
     * Prints the verbose log message in log cat.
     *
     * @param logMessage the debug log message to be displayed.
     */
    fun printVerboseLog(logMessage: String) {
        if (!TextUtils.isEmpty(logMessage)) Log.v(TAG, logMessage)
    }


    ////////////

    ////////////
    /**
     * Prints the information log message in log cat.
     *
     * @param logMessage the information log message to be displayed.
     */
    fun printInfoLog(logMessage: String) {
        if (!TextUtils.isEmpty(logMessage)) Log.i(TAG, logMessage)
    }

    /**
     * Prints the debug log if rest client debug option is enabled.
     *
     * @param <T>
     * @param debugLog the object used to create log
    </T> */
    fun <T> printDebugLog(debugLog: T?, hasDebug: Boolean) {
        if (hasDebug) {
            var log = "response object is NULL"
            if (debugLog != null) {
                log = debugLog.toString()
            }
            println(DEBUG_TAG + log)
            Log.e(TAG, DEBUG_TAG + log)
        }
    }


    private object HOLDER {
        val INSTANCE = LogHelper()
    }

    companion object {
        //val instance: com.shopping.alis.utils.LogHelper = HOLDER.INSTANCE
        val instance: LogHelper by lazy { HOLDER.INSTANCE }
    }

}