package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class BannerModel : BaseModel() {

    @SerializedName("data")
    var data: List<Data> = mutableListOf()

    @Parcelize
    data class Data(
        @SerializedName("title")
        var title: String = "",

        @SerializedName("type")
        var type: String = "",

        @SerializedName("product_type")
        var productType: Boolean = false,

        @SerializedName("link")
        var link: String = "",

        @SerializedName("baseurl")
        var baseUrl: String = "",

        @SerializedName("category_id")
        var categoryId: String = "",

        @SerializedName("image_full_path")
        var image: String = "",

        @SerializedName("image")
        var imageOnly: String = "",
    ) : Parcelable
}