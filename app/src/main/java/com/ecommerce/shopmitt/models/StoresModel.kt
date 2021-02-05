package com.ecommerce.shopmitt.models

import android.os.Parcelable
import com.ecommerce.shopmitt.base.model.BaseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class StoresModel(
    @SerializedName("data")
    @Expose
    val data: List<String> = mutableListOf()
): BaseModel(), Parcelable {

}