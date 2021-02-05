package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class DetailsModel : BaseModel() {

    @SerializedName("data")
    var data: Data? = null

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

        @SerializedName("model")
        var model: String,

        @SerializedName("original_image")
        var original_image: String,

        @SerializedName("images")
        var images: List<String> = mutableListOf(),

        @SerializedName("original_images")
        var originalImages: List<String> = mutableListOf(),

        @SerializedName("description")
        var description: String,

        @SerializedName("size_chart")
        var sizeChart: String,

        @SerializedName("original_size_chart_image")
        var sizeChartImage: String,

        @SerializedName("price")
        var price: String,

        @SerializedName("price_formated")
        var priceFormatted: String,

        @SerializedName("special")
        var special: String? = null,

        @SerializedName("special_formated")
        var specialFormatted: String,

        @SerializedName("minimum")
        var minimum: String,

        @SerializedName("weight")
        var weight: String? = null,

        @SerializedName("weight_class")
        var weightClass: String? = null ,

        @SerializedName("stock_status_id")
        var stockStatusId: String,

        @SerializedName("quantity")
        var quantity: String,

        @SerializedName("reward")
        var reward: String? = null,

        @SerializedName("options")
         var options: List<Option> = mutableListOf(),

        @SerializedName("related_products")
         var related: List<Related> = mutableListOf()

    ) : Parcelable {

        @Parcelize
        data class Related(
            @SerializedName("product_id")
             var productId: String,

            @SerializedName("name")
             val name: String,

            @SerializedName("manufacturer")
             val manufacturer: String? = null,

            @SerializedName("sku")
             val sku: String,

            @SerializedName("model")
             val model: String,

            @SerializedName("image")
             val image: String,

            @SerializedName("quantity")
             val quantity: Int,

            @SerializedName("price")
             val price: String,

            @SerializedName("price_formated")
             val priceFormated: String,

            @SerializedName("special")
             val special: String? = null,

            @SerializedName("special_formated")
             val specialFormated: String,

            @SerializedName("options")
             val options: List<RelatedOption> = mutableListOf(),
        ) : Parcelable {

            @Parcelize
            data class RelatedOption(
                @SerializedName("product_option_id")
                val productOptionId: String,

                @SerializedName("option_value")
                val optionValue: List<OptionValue> = mutableListOf(),

                @SerializedName("option_id")
                val optionId: String,

                @SerializedName("name")
                val name: String,

                @SerializedName("type")
                val type: String,

                @SerializedName("value")
                val value: String,

                @SerializedName("required")
                val required: String,
            ) : Parcelable {

                @Parcelize
                data class OptionValue(
                    @SerializedName("image")
                    var image: String,

                    @SerializedName("price")
                    val price: String,

                    @SerializedName("price_excluding_tax")
                    val priceExcludingTax: String,

                    @SerializedName("price_formated")
                    val priceFormated: String,

                    @SerializedName("price_prefix")
                    val pricePrefix: String,

                    @SerializedName("price_special")
                    val priceSpecial: String,

                    @SerializedName("price_special_formated")
                    val priceSpecialFormated: String,

                    @SerializedName("product_option_value_id")
                    val productOptionValueId: String,

                    @SerializedName("option_value_id")
                    val optionValueId: String,

                    @SerializedName("name")
                    val name: String,

                    @SerializedName("quantity")
                    val quantity: String,

                    @SerializedName("special_price")
                    val specialPrice: String? = null,
                ) : Parcelable
            }

        }

        @Parcelize
        data class Option(
            @SerializedName("product_option_id")
            val productOptionId: String,

            @SerializedName("option_value")
            val optionValue: List<OptionValue> = mutableListOf(),

            @SerializedName("option_id")
             val optionId: String,

            @SerializedName("name")
             val name: String,

            @SerializedName("type")
             val type: String,

            @SerializedName("value")
             val value: String,

            @SerializedName("required")
             val required: String,
        ) : Parcelable {

            @Parcelize
            data class OptionValue(
                @SerializedName("image")
                 var image: String,

                @SerializedName("price")
                 val price: String,

                @SerializedName("price_excluding_tax")
                 val priceExcludingTax: String,

                @SerializedName("price_formated")
                 val priceFormated: String,

                @SerializedName("price_prefix")
                 val pricePrefix: String,

                @SerializedName("price_special")
                 val priceSpecial: String,

                @SerializedName("price_special_formated")
                 val priceSpecialFormated: String,

                @SerializedName("product_option_value_id")
                 val productOptionValueId: String,

                @SerializedName("option_value_id")
                 val optionValueId: String,

                @SerializedName("name")
                 val name: String,

                @SerializedName("quantity")
                 val quantity: String,

                @SerializedName("special_price")
                 val specialPrice: String? = null,
            ) : Parcelable
        }
    }
}