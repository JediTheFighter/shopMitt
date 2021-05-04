package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class ViewCart : BaseModel() {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @Parcelize
    class Data(
        @SerializedName("weight")
        @Expose
        var weight: String? = null,

        @SerializedName("weight_limit")
        @Expose
        var weightLimit: String? = null,

        @SerializedName("category_id")
        @Expose
        val category_id: String? = null,

        @SerializedName("products")
        @Expose
        var products: List<Product>? = null,

        @SerializedName("vouchers")
        @Expose
        var vouchers: List<String>? = null,

        @SerializedName("coupon_status")
        @Expose
        var couponStatus: String? = null,

        @SerializedName("coupon")
        @Expose
        var coupon: String? = null,

        @SerializedName("voucher_status")
        @Expose
        var voucherStatus: String? = null,

        @SerializedName("voucher")
        @Expose
        var voucher: String? = null,

        @SerializedName("message")
        @Expose
        var message: String? = null,

        @SerializedName("reward_status")
        @Expose
        var rewardStatus: Boolean? = null,

        @SerializedName("reward")
        @Expose
        var reward: String? = null,

        @SerializedName("totals")
        @Expose
        var totals: List<Total> = mutableListOf(),

        @SerializedName("total")
        @Expose
        var total: String? = null,

        @SerializedName("total_raw")
        @Expose
        var totalRaw: Double = 0.0,

        @SerializedName("total_product_count")
        @Expose
        var totalProductCount: Int? = null,

        @SerializedName("reward_total_points")
        @Expose
        var rewardTotalPoints: Int? = null,

        @SerializedName("total_savings")
        @Expose
        var totalSavings: String? = null,

        @SerializedName("has_shipping")
        @Expose
        var hasShipping: String? = null,

        @SerializedName("you_save")
        @Expose
        var youSave: String? = null,

        @SerializedName("products_count")
        @Expose
        var productsCount: String? = null,
    ) : Parcelable {

        @Parcelize
        data class Product(

            @SerializedName("key")
            @Expose
            var key: String? = null,

            @SerializedName("thumb")
            @Expose
            var thumb: String? = null,

            @SerializedName("name")
            @Expose
            var name: String? = null,

            @SerializedName("product_id")
            @Expose
            var productId: String? = null,

            @SerializedName("model")
            @Expose
            var model: String? = null,

            @SerializedName("option")
            @Expose
            var options: List<Option> = mutableListOf(),

            @SerializedName("quantity")
            @Expose
            var quantity: String? = null,

            @SerializedName("stock")
            @Expose
            var stock: Boolean? = null,

            @SerializedName("stock_quantity")
            @Expose
            var stockQuantity: String? = null,

            @SerializedName("reward")
            @Expose
            var reward: String? = null,

            @SerializedName("price_raw")
            @Expose
            var price: String? = null,

            @SerializedName("total_raw")
            @Expose
            var total: String? = null,

            @SerializedName("actual_price")
            @Expose
            var actualPrice: String? = null,

            @SerializedName("special")
            @Expose
            val special: String? = null,

            @SerializedName("weight")
            @Expose
            val weight: String? = null,

            @SerializedName("weight_class")
            @Expose
            val weight_class: String? = null,

            @SerializedName("manufacturer")
            @Expose
            val manufacturer: String? = null,
        ) : Parcelable {

            fun getProductOffer(): Int {
                return if (price != "false" && special != null && special != "false") {
                    var off = 0.0
                    off = 100 - java.lang.Double.valueOf(special) * 100 / java.lang.Double.valueOf(
                        price!!
                    )
                    Math.round(off).toInt()
                } else 0
            }

            @Parcelize
            data class Option(
                @SerializedName("name")
                @Expose
                var name: String? = null,

                @SerializedName("value")
                @Expose
                var value: String? = null,

                @SerializedName("special_price")
                @Expose
                val specialPrice: String? = null,

                @SerializedName("option_price")
                @Expose
                val optionPrice: String? = null,
            ): Parcelable
        }

        @Parcelize
        data class Total(
            @SerializedName("title")
            @Expose
            var title: String? = null,

            @SerializedName("text")
            @Expose
            var text: String? = null,

            @SerializedName("value")
            @Expose
            var value: Float = 0f
        ) : Parcelable

    }
}