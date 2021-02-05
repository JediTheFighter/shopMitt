package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize


class ShippingAddressModel : BaseModel() {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @Parcelize
    class Data(
        @SerializedName("address_id")
        @Expose
        var addressId: String? = null,

        @SerializedName("addresses")
        @Expose
        var addresses: List<Address> = mutableListOf(),

        @SerializedName("postcode")
        @Expose
        var postcode: String? = null,

        @SerializedName("country_id")
        @Expose
        var countryId: String? = null,

        @SerializedName("zone_id")
        @Expose
        var zoneId: String? = null,

        ) : Parcelable {

        @Parcelize
        data class Address(
            @SerializedName("address_id")
            @Expose
            var addressId: String? = null,

            @SerializedName("firstname")
            @Expose
            var firstname: String? = null,

            @SerializedName("lastname")
            @Expose
            var lastname: String? = null,

            @SerializedName("company")
            @Expose
            var company: String? = null,

            @SerializedName("telephone")
            @Expose
            var telephone: String? = null,

            @SerializedName("address_1")
            @Expose
            var address1: String? = null,

            @SerializedName("address_2")
            @Expose
            var address2: String? = null,

            @SerializedName("postcode")
            @Expose
            var postcode: String? = null,

            @SerializedName("city")
            @Expose
            var city: String? = null,

            @SerializedName("zone_id")
            @Expose
            var zoneId: String? = null,

            @SerializedName("zone")
            @Expose
            var zone: String? = null,

            @SerializedName("zone_code")
            @Expose
            var zoneCode: String? = null,

            @SerializedName("country_id")
            @Expose
            var countryId: String? = null,

            @SerializedName("country")
            @Expose
            var country: String? = null,

            @SerializedName("iso_code_2")
            @Expose
            var isoCode2: String? = null,

            @SerializedName("iso_code_3")
            @Expose
            var isoCode3: String? = null,

            @SerializedName("address_format")
            @Expose
            var addressFormat: String? = null,

            @SerializedName("latitude")
            @Expose
            val latitude: String? = null,

            @SerializedName("longitude")
            @Expose
            val longitude: String? = null,

            @SerializedName("prev_addr")
            @Expose
            val prevAddress: String? = null,
        ) : Parcelable
    }
}