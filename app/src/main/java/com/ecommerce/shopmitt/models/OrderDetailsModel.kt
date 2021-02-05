package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class OrderDetailsModel : BaseModel() {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @Parcelize
    class Data(
        @SerializedName("order_id")
        @Expose
        var orderId: String? = null,

        @SerializedName("delivery_date")
        @Expose
        val delivery_date: String? = null,

        @SerializedName("delivery_date_only")
        @Expose
        val delivery_date_only: String? = null,

        @SerializedName("shipping_address")
        @Expose
        val delivery_address: String? = null,

        @SerializedName("time_slot")
        @Expose
        val time_slot: String? = null,

        @SerializedName("bank_refno")
        @Expose
        val bank_refno: String? = null,

        @SerializedName("invoice_no")
        @Expose
        var invoiceNo: String? = null,

        @SerializedName("invoice_prefix")
        @Expose
        var invoicePrefix: String? = null,

        @SerializedName("store_id")
        @Expose
        var storeId: String? = null,

        @SerializedName("store_name")
        @Expose
        var storeName: String? = null,

        @SerializedName("store_url")
        @Expose
        var storeUrl: String? = null,

        @SerializedName("customer_id")
        @Expose
        var customerId: String? = null,

        @SerializedName("firstname")
        @Expose
        var firstname: String? = null,

        @SerializedName("lastname")
        @Expose
        var lastname: String? = null,

        @SerializedName("telephone")
        @Expose
        var telephone: String? = null,

        @SerializedName("fax")
        @Expose
        var fax: String? = null,

        @SerializedName("email")
        @Expose
        var email: String? = null,

        @SerializedName("payment_firstname")
        @Expose
        var paymentFirstname: String? = null,

        @SerializedName("payment_lastname")
        @Expose
        var paymentLastname: String? = null,

        @SerializedName("payment_company")
        @Expose
        var paymentCompany: String? = null,

        @SerializedName("payment_address_1")
        @Expose
        var paymentAddress1: String? = null,

        @SerializedName("payment_address_2")
        @Expose
        var paymentAddress2: String? = null,

        @SerializedName("payment_postcode")
        @Expose
        var paymentPostcode: String? = null,

        @SerializedName("payment_city")
        @Expose
        var paymentCity: String? = null,

        @SerializedName("payment_zone_id")
        @Expose
        var paymentZoneId: String? = null,

        @SerializedName("payment_zone")
        @Expose
        var paymentZone: String? = null,

        @SerializedName("payment_zone_code")
        @Expose
        var paymentZoneCode: String? = null,

        @SerializedName("payment_country_id")
        @Expose
        var paymentCountryId: String? = null,

        @SerializedName("payment_country")
        @Expose
        var paymentCountry: String? = null,

        @SerializedName("payment_iso_code_2")
        @Expose
        var paymentIsoCode2: String? = null,

        @SerializedName("payment_iso_code_3")
        @Expose
        var paymentIsoCode3: String? = null,

        @SerializedName("payment_address_format")
        @Expose
        var paymentAddressFormat: String? = null,

        @SerializedName("payment_method")
        @Expose
        var paymentMethod: String? = null,

        @SerializedName("shipping_firstname")
        @Expose
        var shippingFirstname: String? = null,

        @SerializedName("shipping_lastname")
        @Expose
        var shippingLastname: String? = null,

        @SerializedName("shipping_company")
        @Expose
        var shippingCompany: String? = null,

        @SerializedName("shipping_address_1")
        @Expose
        var shippingAddress1: String? = null,

        @SerializedName("shipping_address_2")
        @Expose
        var shippingAddress2: String? = null,

        @SerializedName("shipping_postcode")
        @Expose
        var shippingPostcode: String? = null,

        @SerializedName("shipping_city")
        @Expose
        var shippingCity: String? = null,

        @SerializedName("shipping_zone_id")
        @Expose
        var shippingZoneId: String? = null,

        @SerializedName("shipping_zone")
        @Expose
        var shippingZone: String? = null,

        @SerializedName("shipping_zone_code")
        @Expose
        var shippingZoneCode: String? = null,

        @SerializedName("shipping_country_id")
        @Expose
        var shippingCountryId: String? = null,

        @SerializedName("shipping_country")
        @Expose
        var shippingCountry: String? = null,

        @SerializedName("shipping_iso_code_2")
        @Expose
        var shippingIsoCode2: String? = null,

        @SerializedName("shipping_iso_code_3")
        @Expose
        var shippingIsoCode3: String? = null,

        @SerializedName("shipping_address_format")
        @Expose
        var shippingAddressFormat: String? = null,

        @SerializedName("shipping_method")
        @Expose
        var shippingMethod: String? = null,

        @SerializedName("comment")
        @Expose
        var comment: String? = null,

        @SerializedName("total")
        @Expose
        var total: String? = null,

        @SerializedName("order_status_id")
        @Expose
        var orderStatusId: String? = null,

        @SerializedName("language_id")
        @Expose
        var languageId: String? = null,

        @SerializedName("currency_id")
        @Expose
        var currencyId: String? = null,

        @SerializedName("currency_code")
        @Expose
        var currencyCode: String? = null,

        @SerializedName("currency_value")
        @Expose
        var currencyValue: String? = null,

        @SerializedName("date_modified")
        @Expose
        var dateModified: String? = null,

        @SerializedName("date_added")
        @Expose
        var dateAdded: String? = null,


        @SerializedName("ip")
        @Expose
        var ip: String? = null,

        @SerializedName("products")
        @Expose
        var products: List<Product>? = null,

        @SerializedName("totals")
        @Expose
        var totals: List<Total>? = null,

        @SerializedName("histories")
        @Expose
        var histories: List<History>? = null,

        @SerializedName("order_status_final")
        @Expose
        var order_status_final: String? = null,
    ) : Parcelable {

        @Parcelize
        data class History(
            @SerializedName("date_added")
            @Expose
            var dateAdded: String? = null,

            @SerializedName("status")
            @Expose
            var status: String? = null,

            @SerializedName("comment")
            @Expose
            var comment: String? = null,
        ) : Parcelable

        @Parcelize
        data class Product(
            @SerializedName("product_id")
            @Expose
            var productId: String? = null,

            @SerializedName("order_product_id")
            @Expose
            var orderProductId: String? = null,

            @SerializedName("name")
            @Expose
            var name: String? = null,

            @SerializedName("original_image")
            @Expose
            var originalImage: String? = null,

            @SerializedName("model")
            @Expose
            var model: String? = null,

            @SerializedName("option")
            @Expose
            var option: List<Option> = mutableListOf(),

            @SerializedName("quantity")
            @Expose
            var quantity: String? = null,

            @SerializedName("price")
            @Expose
            var price: String? = null,

            @SerializedName("total")
            @Expose
            var total: String? = null,

            @SerializedName("return")
            @Expose
            var `return`: String? = null,
        ) : Parcelable {

            @Parcelize
            data class Option(
                @SerializedName("name")
                @Expose
                var name: String? = null,

                @SerializedName("value")
                @Expose
                var value: String? = null,
            ) : Parcelable
        }

        @Parcelize
        data class Total(
            @SerializedName("order_total_id")
            @Expose
            var orderTotalId: String? = null,

            @SerializedName("order_id")
            @Expose
            var orderId: String? = null,

            @SerializedName("code")
            @Expose
            var code: String? = null,

            @SerializedName("title")
            @Expose
            var title: String? = null,

            @SerializedName("text")
            @Expose
            var text: String? = null,

            @SerializedName("value")
            @Expose
            var value: String? = null,

            @SerializedName("sort_order")
            @Expose
            var sortOrder: String? = null,
        ) : Parcelable
    }
}

