package com.ecommerce.shopmitt.models

import com.google.gson.annotations.SerializedName
import com.ecommerce.shopmitt.base.model.BaseModel

class TokenModel : BaseModel() {

    @SerializedName("data")
    val data: Data? = null

    data class Data(
        @SerializedName("access_token")
        val accessToken: String? = null,

        @SerializedName("expires_in")
        val expiresIn: String? = null,

        @SerializedName("token_type")
        val tokenType: String? = null,
    )
}