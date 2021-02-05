package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReferralModel(
    @SerializedName("data")
    val data: Data
): BaseModel(), Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("referral_code")
        @Expose
        val referral_code: String,

        @SerializedName("placeholder")
        @Expose
        val placeholder: String? = null,
    ): Parcelable
}