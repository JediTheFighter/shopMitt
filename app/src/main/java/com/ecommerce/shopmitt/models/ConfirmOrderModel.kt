package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class ConfirmOrderModel: BaseModel() {

    @SerializedName("data")
    @Expose
    val data: Data? = null

    @Parcelize
    data class Data(
        @SerializedName("order_id")
        @Expose
        val orderId: String? = null,

        @SerializedName("bank_refno")
        @Expose
        val bank_refno: String? = null,

        @SerializedName("amount")
        @Expose
        val amount: String? = null,

        var totals: List<Totals> = mutableListOf()
    ): Parcelable {

        @Parcelize
        data class Totals(
            @SerializedName("total_amount")
            @Expose
            var total_amount: Double = 0.0
        ): Parcelable
    }
}