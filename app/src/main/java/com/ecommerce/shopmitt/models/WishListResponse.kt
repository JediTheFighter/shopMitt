package com.ecommerce.shopmitt.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel

class WishListResponse: BaseModel() {

    @SerializedName("data")
    @Expose
    private var data: List<Any?>? = null

}