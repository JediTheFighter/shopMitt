package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationModel(

    @SerializedName("data")
    var data: List<Data> = mutableListOf()
): Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("notification_id")
        var notificationId: String,

        @SerializedName("title")
        var title: String,

        @SerializedName("type")
        var type: String,

        @SerializedName("id")
        var id: String,

        @SerializedName("content")
        var content: String,

        @SerializedName("bannerImage")
        var bannerImage: String,

        @SerializedName("subTitle")
        var subTitle: String,
    ): Parcelable
}