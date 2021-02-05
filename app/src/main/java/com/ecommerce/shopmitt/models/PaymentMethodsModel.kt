package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class PaymentMethodsModel: BaseModel() {

    @SerializedName("data")
    @Expose
    val data: Data? = null

    @Parcelize
    data class Data(
        @SerializedName("payment_methods")
        @Expose
        var paymentMethods: List<PaymentMethod>? = null,

        @SerializedName("code")
        @Expose
        var code: String? = null,

        @SerializedName("comment")
        @Expose
        var comment: String? = null,

        @SerializedName("agree")
        @Expose
        var agree: String? = null): Parcelable {

        @Parcelize
        data class PaymentMethod(
            @SerializedName("code")
            @Expose
            var code: String? = null,

            @SerializedName("title")
            @Expose
            var title: String? = null,

            @SerializedName("terms")
            @Expose
            var terms: String? = null,

            @SerializedName("sort_order")
            @Expose
            var sortOrder: String? = null,

            @SerializedName("image")
            @Expose
            var image: String? = null
        ): Parcelable
    }
}