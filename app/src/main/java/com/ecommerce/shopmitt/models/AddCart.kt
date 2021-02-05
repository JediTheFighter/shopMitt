package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class AddCart : BaseModel() {

    @SerializedName("data")
    var data: Data? = null

    @Parcelize
    data class Data(
        @SerializedName("product_id")
        var productId: String,
        @SerializedName("quantity")
        var quantity: String,
        @SerializedName("option")
        var option: String,
        @SerializedName("product")
        var product: Product,
        @SerializedName("total")
        var total: String,
        @SerializedName("total_product_count")
        var totalProductCount: Int,
        @SerializedName("total_price")
        var totalPrice: String,
    ) : Parcelable {

        @Parcelize
        data class Product(
            @SerializedName("product_id")
            var productId: String,
            @SerializedName("name")
            var name: String,
            @SerializedName("quantity")
            var quantity: String,
            @SerializedName("key")
            var key: String
        ) : Parcelable
    }


}