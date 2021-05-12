package com.ecommerce.shopmitt.utils

import com.ecommerce.shopmitt.BuildConfig

object Constants {

    ///
    const val FROM_SHIPPING = 566
    const val FROM_CHANGE_ADDRESS = 565
    const val FROM_PICKUP = 564
    const val FROM_DASH = 563

    const val GET_LOCATION = 234
    ///

    const val WHATSAPP_NO = "91 8409940569"
    const val PLACE_PICKER_COUNTRY = "KW"
    const val CURRENCY = "₹"
    const val IS_NOT_RELATIVE = "1"
    const val IS_RELATIVE = "2"
    const val PLACE_PICKER_LOCATION_BIAS_RADIUS: Int = 50 * 1000
    const val APP_PREFERENCE_NAME = "alis_preference"
    const val LANGUAGE_PREFERENCE_NAME = "alis_language_pref"
    const val APP_FOLDER = "Alis"
    const val FOLDER_PROFILE = "/Profile/"
    const val FOLDER_DOWNLOADS = "Downloads"
    const val CAMERA_REQUEST = 201
    const val GALLERY_REQUEST = 202
    const val EDIT_TIMESLOTS = 351
    const val DEVICE_TYPE_ANDROID = 1
    const val NO_LANGUAGE = ""

    const val MIME_TYPE_PDF = "application/pdf"
    const val MIME_TYPE_IMG = "application/image"
    const val LOCATION_SETTINGS_TYPE_GPS = 1001
    const val LOCATION_SETTINGS_TYPE_PERMISSION = 1002
    const val REQUEST_CHECK_LOCATION_SETTINGS = 1003
    const val REQUEST_CHECK_UPDATE = 1004
    const val STATUS_CODE_NO_CONNECTION = 5001
    const val OTP_LENGTH = 4

    const val BROADCAST_ACTION_CALL_REQUEST = "action_call_request_afia"

    const val REQUEST_CODE_ADD_PATIENT = 101
    const val REQUEST_CODE_EDIT_APPOINTMENT = 102
    const val REQUEST_CODE_CHOOSE_INSURANCE = 122
    const val PAYMENT_METHOD_CARD = "1"
    const val PAYMENT_METHOD_CASH = "2"
    const val PAYMENT_METHOD_INSURANCE = "2"
    const val FIRST_VISIT = "1"
    const val NOT_FIRST_VISIT = "0"
    const val SORT_TYPE_RELEVANCE = 212
    const val SORT_TYPE_DISTANCE = 213
    const val SORT_TYPE_WAIT_TIME = 214

    // App urls
    const val PRIVACY_POLICY = BuildConfig.BASE_URL + "index.php?route=feed/rest_api/information&id=1&info=1"
    const val TERMS_AND_CONDITIONS = BuildConfig.BASE_URL + "index.php?route=feed/rest_api/information&id=2&info=1"
    const val ABOUT_US = BuildConfig.BASE_URL + "index.php?route=feed/rest_api/information&id=4"


    // App url type
    const val PAGE_PRIVACY = 755
    const val PAGE_TERMS = 756
    const val PAGE_ABOUT_US = 757


}