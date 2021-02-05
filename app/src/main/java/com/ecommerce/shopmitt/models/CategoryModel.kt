package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class CategoryModel : BaseModel() {

    @SerializedName("data")
    var data: List<Data> = mutableListOf()

    @Parcelize
    data class Data(
        @SerializedName("category_id")
        var categoryId: String? = null,

        @SerializedName("parent_id")
        var parentId: String? = null,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("product_type")
        var productType: String? = null,

        @SerializedName("image")
        var image: String? = null,

        @SerializedName("original_image")
        var originalImage: String? = null,
    ) :Parcelable

}