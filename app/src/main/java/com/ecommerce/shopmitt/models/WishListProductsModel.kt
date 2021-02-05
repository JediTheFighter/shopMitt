package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class WishListProductsModel: BaseModel() {

    @SerializedName("data")
    @Expose
    var data: List<Data> = mutableListOf()

    @Parcelize
    data class Data(
        @SerializedName("product_id")
        @Expose
         var productID: String,

        @SerializedName("thumb")
        @Expose
         var thumb: String,

        @SerializedName("name")
        @Expose
         var name: String,

        @SerializedName("model")
        @Expose
         var model: String,

        @SerializedName("stock")
        @Expose
         var stock: String,

        @SerializedName("price")
        @Expose
         var price: String,

        @SerializedName("special")
        @Expose
         var special: String,

    ): Parcelable
}