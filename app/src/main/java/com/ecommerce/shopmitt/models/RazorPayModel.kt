package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RazorPayModel(
    val id: String,
    val entity: String,
    val amount: String
) : BaseModel(), Parcelable