package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

class ProfileModel: BaseModel() {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @Parcelize
    data class Data(
        @SerializedName("firstname")
        @Expose
        var firstname: String? = null,

        @SerializedName("email")
        @Expose
        var email: String? = null,

        @SerializedName("telephone")
        @Expose
        var telephone: String? = null,

        @SerializedName("lastname")
        @Expose
        var lastname: String? = null,

        @SerializedName("reward_total")
        @Expose
        var reward_total: String? = null,

        @SerializedName("rewards")
        @Expose
        val rewards: List<Reward> = mutableListOf(),

        @SerializedName("user_balance")
        @Expose
        val userBalance: String? = null,

        @SerializedName("wallet_banner")
        @Expose
        val wallet_banner: String? = null,): Parcelable {

        @Parcelize
        data class Reward(
            @SerializedName("order_id")
            @Expose
            var orderId: String? = null,

            @SerializedName("points")
            @Expose
            var points: String? = null,

            @SerializedName("description")
            @Expose
            var description: String? = null,

            @SerializedName("date_added")
            @Expose
            var dateAdded: String? = null,): Parcelable
    }
}