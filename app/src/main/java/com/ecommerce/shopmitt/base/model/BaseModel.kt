package com.ecommerce.shopmitt.base.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class BaseModel {
    @Expose
    @SerializedName("success")
    var success: Int = 0
    @Expose
    @SerializedName("error")
    var error: List<String> = mutableListOf()

}