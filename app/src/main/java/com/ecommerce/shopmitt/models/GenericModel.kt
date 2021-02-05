package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class GenericModel: BaseModel() {

    @SerializedName("data")
    @Expose
    val data: Data? = null

    @Parcelize
    class Data(
        @SerializedName("message")
        @Expose
        var message: String? = null): Parcelable
}