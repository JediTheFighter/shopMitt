package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class ProductModel : BaseModel() {

    @SerializedName("data")
    var data: List<Data> = mutableListOf()

    @Parcelize
    data class Data(
        @SerializedName("id")
        var id: String,

        @SerializedName("product_id")
        var productId: String,

        @SerializedName("name")
        var name: String,

        @SerializedName("manufacturer")
        var manufacturer: String? = null,

        @SerializedName("sku")
        var sku: String,

        @SerializedName("model")
        var model: String,

        @SerializedName("original_image")
        var originalImage: String,

        @SerializedName("image")
        var image: String,

        @SerializedName("description")
        var description: String,

        @SerializedName("weight")
        var weight: String,

        @SerializedName("weight_class")
        var weightClass: String,

        @SerializedName("price")
        var price: String,

        @SerializedName("price_formated")
        var priceFormatted: String,

        @SerializedName("quantity")
        var quantity: String,

        @SerializedName("stock_status")
        var stockStatus: String? = null,

        @SerializedName("special")
        var special: String? = null,

        @SerializedName("special_formated")
        var specialFormatted: String? = null,

        @SerializedName("options")
        var options: List<Options> = mutableListOf(),
    ) : Parcelable {


        fun getProductOffer(): Int {
            return if (price != "false" && special != null && special != "false") {
                var off = 0.0
                off =
                    100 - java.lang.Double.valueOf(special!!) * 100 / java.lang.Double.valueOf(price)
                Math.round(off).toInt()
            } else 0
        }

        @Parcelize
        data class Options(
            @SerializedName("product_option_id")
            var productOptionID: String,

            @SerializedName("option_id")
            var optionID: String,

            @SerializedName("name")
            var name: String,

            @SerializedName("type")
            var type: String,

            @SerializedName("option_value")
            var optionValue: List<OptionValue> = mutableListOf(),
        ) : Parcelable {

            @Parcelize
            data class OptionValue(

                @SerializedName("price")
                var price: String,

                @SerializedName("price_formated")
                var priceFormatted: String,

                @SerializedName("product_option_value_id")
                var productOptionValueID: String,

                @SerializedName("option_value_id")
                var optionValueID: String,

                @SerializedName("name")
                var name: String,

                @SerializedName("quantity")
                var quantity: String,

                @SerializedName("price_special")
                var priceSpecial: String? = null,

                @SerializedName("price_special_formated")
                var priceSpecialFormatted: String? = null,

                @SerializedName("special_price")
                var specialPrice: String? = null,

                ) : Parcelable
        }
    }

}