package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class TransactionModel(
    @SerializedName("data")
    val data: Data
) : BaseModel(), Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("balance")
        @Expose
        var balance: String,

        @SerializedName("customer_id")
        @Expose
        var customerId: String,

        @SerializedName("history")
        @Expose
        var history: List<History> = mutableListOf(),

        @SerializedName("credit_count")
        @Expose
        var creditCount: Int,

        @SerializedName("debit_count")
        @Expose
        var debitCount: Int,

        @SerializedName("total_credits")
        @Expose
        var totalCredits: String,

        @SerializedName("total_debits")
        @Expose
        var totalDebits: String,
    ) : Parcelable {

        @Parcelize
        data class History(
            @SerializedName("order_id")
            @Expose
            var orderId: String,

            @SerializedName("amount")
            @Expose
            var amount: String,

            @SerializedName("status")
            @Expose
            var status: String,

            @SerializedName("type")
            @Expose
            var type: String,

            @SerializedName("date")
            @Expose
            var date: String,

            @SerializedName("desc")
            @Expose
            var desc: String,
        ) : Parcelable
    }
}