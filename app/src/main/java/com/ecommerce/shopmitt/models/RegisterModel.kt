package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class RegisterModel: BaseModel() {

    @SerializedName("data")
    var data: Data? = null

    @Parcelize
    data class Data(
        @SerializedName("firstname")
        var firstName: String = "",

        @SerializedName("lastname")
        var lastName: String = "",

        @SerializedName("email")
        var email: String = "",

        @SerializedName("address_1")
        var address_one: String = "",

        @SerializedName("address_2")
        var address_two: String = "",

        @SerializedName("company")
        var company: String = "",

        @SerializedName("telephone")
        var telephone: String = "",

        @SerializedName("city")
        var city: String = "",

        @SerializedName("postcode")
        var postcode: String = "",

        @SerializedName("country_id")
        var countryId: String = "",

        @SerializedName("zone_id")
        var zoneId: String = "",

        @SerializedName("customer_id")
        var customerId: String = "",

        @SerializedName("address_id")
        var addressId: String = "",

    ) : Parcelable
}