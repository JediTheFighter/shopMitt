package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreDetailModel(
    @SerializedName("data")
    @Expose
    val data: Data
): BaseModel(), Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("store_id")
        @Expose
        val storeId: String,

        @SerializedName("store_fax")
        @Expose
        val storeFax: String,

        @SerializedName("store_image")
        @Expose
        val storeImage: String,

        @SerializedName("thumb")
        @Expose
        val thumb: String,

        @SerializedName("store_open")
        @Expose
        val storeOpen: String,

        @SerializedName("store_comment")
        @Expose
        val storeComment: String,

        @SerializedName("store_language")
        @Expose
        val storeLanguage: String,

        @SerializedName("config_tax")
        @Expose
        val configTax: String,

        @SerializedName("config_customer_online")
        @Expose
        val configCustomerOnline: String,

        @SerializedName("config_checkout_guest")
        @Expose
        val configCheckoutGuest: String,

        @SerializedName("store_telephone")
        @Expose
        val storeTelephone: String,

        @SerializedName("store_geocode")
        @Expose
        val storeGeocode: String,

        @SerializedName("store_email")
        @Expose
        val storeEmail: String,

        @SerializedName("store_address")
        @Expose
        val storeAddress: String,

        @SerializedName("store_owner")
        @Expose
        val storeOwner: String,

        @SerializedName("store_name")
        @Expose
        val storeName: String,
    ): Parcelable
}