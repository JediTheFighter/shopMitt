package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class DeliveryPlacesModel: BaseModel() {

    @SerializedName("data")
    @Expose
    val data: List<Data> = mutableListOf()

    @Parcelize
    data class Data(
        @SerializedName("zip_code")
        @Expose
        var zipCode: String,

        @SerializedName("city_name")
        @Expose
        var cityName: String): Parcelable
}