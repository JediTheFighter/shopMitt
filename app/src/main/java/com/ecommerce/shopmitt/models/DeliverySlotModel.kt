package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class DeliverySlotModel: BaseModel() {

    @SerializedName("data")
    @Expose
    val data: Data? = null

    @Parcelize
    data class Data(

        @SerializedName("delivery_date")
        @Expose
        val dates: List<Date> = mutableListOf()

    ): Parcelable {

        @Parcelize
        data class Date(
            @SerializedName("title")
            @Expose
            var title: String,

            @SerializedName("date")
            @Expose
            var date: String,

            @SerializedName("time_slot")
            @Expose
            val timeSlots: List<TimeSlot> = mutableListOf()
        ): Parcelable {

            @Parcelize
            data class TimeSlot(
                @SerializedName("time_slot")
                @Expose
                val timeSlot: String
            ): Parcelable
        }
    }
}