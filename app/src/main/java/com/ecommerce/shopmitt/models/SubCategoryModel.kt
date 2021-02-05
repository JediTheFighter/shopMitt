package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class SubCategoryModel : BaseModel() {

    @SerializedName("data")
    var data: Data? = null

    @Parcelize
    data class Data(
        @SerializedName("id")
        var categoryId: String? = null,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("original_image")
        var originalImage: String? = null,

        @SerializedName("sub_categories")
        var subcategories: List<SubCategory>,
    ) : Parcelable

    @Parcelize
    data class SubCategory(
        @SerializedName("category_id")
        var subCategoryId: String? = null,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("original_image")
        var originalImage: String? = null,

        @SerializedName("product_type")
        var productType: String? = null,
    ): Parcelable
}