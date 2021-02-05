package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReferralOffersModel(
    @SerializedName("data")
    val data: List<RewardOffers> = mutableListOf()
): BaseModel(), Parcelable {

    @Parcelize
    data class RewardOffers(

        @SerializedName("statement")
        var statement: String,

        @SerializedName("code")
        var code: String? = null,
    ): Parcelable
}