package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class ProductModelWishlist: BaseModel() {

    @SerializedName("data")
    @Expose
    var data: List<Data> = mutableListOf()


    @Parcelize
    data class Data(
        @SerializedName("product_id")
        @Expose
        var productId: String? = null,

        @SerializedName("thumb")
        @Expose
        var thumb: String? = null,

        @SerializedName("name")
        @Expose
        var name: String? = null,

        @SerializedName("price_excluding_tax")
        @Expose
        var priceExcludingTax: String? = null,

        @SerializedName("price")
        @Expose
        var price: String? = null,

        @SerializedName("price_formated")
        @Expose
        var priceFormated: String? = null,

        @SerializedName("special")
        @Expose
        var special: String? = null,

        @SerializedName("special_excluding_tax")
        @Expose
        var specialExcludingTax: String? = null,

        @SerializedName("special_formated")
        @Expose
        var specialFormated: String? = null,

        @SerializedName("rating")
        @Expose
        var rating: Int? = null,

        @SerializedName("description")
        @Expose
        var description: String? = null,

        @SerializedName("quantity")
        @Expose
        var quantity: Int = 0,

        @SerializedName("option")
        @Expose
        val option: List<Option> = mutableListOf(),

        @SerializedName("model")
        @Expose
        val model: String? = null,

        @SerializedName("manufacturer")
        @Expose
        val manufacturer: String? = null,

        @SerializedName("weight")
        @Expose
        val weight: String? = null,

        @SerializedName("weight_class")
        @Expose
        val weightClass: String? = null,

        @SerializedName("option_price")
        @Expose
        val optionPrice: String? = null,
    ) : Parcelable {


        val productOffer: Int
            get() = if (price != "false" && special != null && special != "false") {
                var off = 0.0
                off = 100 - java.lang.Double.valueOf(special!!) * 100 / java.lang.Double.valueOf(
                    price!!
                )
                Math.round(off).toInt()
            } else 0

        @Parcelize
        data class Option(
            @SerializedName("product_option_value")
            @Expose
            val product_option_value: List<ProductOption> = mutableListOf(),

            @SerializedName("option_id")
            @Expose
            val optionId: String? = null,

            @SerializedName("product_option_id")
            @Expose
            val productOptionId: String? = null) : Parcelable {

            @Parcelize
            data class ProductOption(
                @SerializedName("product_option_value_id")
                @Expose
                val productOptionValueId: String? = null,

                @SerializedName("option_value_id")
                @Expose
                val optionValueId: String? = null,

                @SerializedName("name")
                @Expose
                val name: String? = null,

                @SerializedName("image")
                @Expose
                val image: String? = null,

                @SerializedName("quantity")
                @Expose
                val quantity: String? = null,

                @SerializedName("subtract")
                @Expose
                val subtract: String? = null,

                @SerializedName("price")
                @Expose
                val price: String? = null,

                @SerializedName("price_prefix")
                @Expose
                val pricePrefix: String? = null,

                @SerializedName("special_price")
                @Expose
                val specialPrice: String? = null,

                @SerializedName("special_price_prefix")
                @Expose
                val specialPricePrefix: String? = null,

                @SerializedName("weight")
                @Expose
                val weight: String? = null,

                @SerializedName("weight_prefix")
                @Expose
                val weight_prefix: String? = null,
            ): Parcelable
        }
    }
}