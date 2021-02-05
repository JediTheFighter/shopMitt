package com.ecommerce.shopmitt.models

import android.graphics.Color
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class OrdersModel: BaseModel() {

    @SerializedName("data")
    @Expose
    var data: List<Data> = mutableListOf()

    @Parcelize
    class Data(
        @SerializedName("order_id")
        @Expose
        var orderId: String? = null,

        @SerializedName("name")
        @Expose
        var name: String? = null,

        @SerializedName("status")
        @Expose
        var status: String? = null,

        @SerializedName("date_added")
        @Expose
        var dateAdded: String? = null,

        @SerializedName("products")
        @Expose
        var products: Int? = null,

        @SerializedName("total")
        @Expose
        var total: String? = null,

        @SerializedName("currency_code")
        @Expose
        var currencyCode: String? = null,

        @SerializedName("currency_value")
        @Expose
        var currencyValue: String? = null,

        ): Parcelable {

        fun getStatusColor(): Int {
            if (status.equals(
                    "Confirmed",
                    ignoreCase = true
                )
            ) return Color.parseColor("#191919") else if (status.equals(
                    "PROCESSING",
                    ignoreCase = true
                )
            ) return Color.parseColor("#f15b25") else if (status.equals(
                    "PROCESSED",
                    ignoreCase = true
                )
            ) return Color.parseColor("#fbbc05") else if (status.equals(
                    "SHIPPED",
                    ignoreCase = true
                )
            ) return Color.parseColor("#2874F0") else if (status.equals(
                    "COMPLETE",
                    ignoreCase = true
                )
            ) return Color.parseColor("#4CAF50")
            return Color.parseColor("#191919")
        }
        }
}