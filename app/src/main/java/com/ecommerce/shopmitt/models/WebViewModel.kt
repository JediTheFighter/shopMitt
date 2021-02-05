package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class WebViewModel: BaseModel() {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @Parcelize
    data class Data(
        @SerializedName("title")
        @Expose
        var title: String? = null,

        @SerializedName("description")
        @Expose
        var description: String? = null,
    ): Parcelable
}