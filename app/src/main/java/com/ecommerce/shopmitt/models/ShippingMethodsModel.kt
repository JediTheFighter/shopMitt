package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class ShippingMethodsModel: BaseModel() {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @Parcelize
    data class Data(
        @SerializedName("shipping_methods")
        @Expose
        var shippingMethods: List<ShippingMethod> = mutableListOf(),

        @SerializedName("code")
        @Expose
        var code: String? = null,

        @SerializedName("comment")
        @Expose
        var comment: String? = null): Parcelable {

        @Parcelize
        data class ShippingMethod(
            @SerializedName("code")
            @Expose
            var code: String? = null,

            @SerializedName("title")
            @Expose
            var title: String? = null,

            @SerializedName("cost")
            @Expose
            var cost: Double? = null,

            @SerializedName("tax_class_id")
            @Expose
            var taxClassId: Int? = null,

            @SerializedName("text")
            @Expose
            var text: String? = null,

            @SerializedName("image")
            @Expose
            var image: String? = null
        ): Parcelable
    }
}