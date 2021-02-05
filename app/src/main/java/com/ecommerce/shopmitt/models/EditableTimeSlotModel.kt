package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditableTimeSlotModel(
    @SerializedName("data")
    @Expose
    val data: Data
): BaseModel(), Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("time_slot")
        @Expose
        val timeSlots: List<TimeSlot> = mutableListOf(),
    ): Parcelable {

        @Parcelize
        data class TimeSlot(
            @SerializedName("time_slot")
            @Expose
            val time_slot: String
        ): Parcelable
    }
}