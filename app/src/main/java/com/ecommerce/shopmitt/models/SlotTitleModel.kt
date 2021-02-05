package com.ecommerce.shopmitt.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SlotTitleModel(
    val title: String,
    val date: String
): Parcelable