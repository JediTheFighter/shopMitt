package com.ecommerce.shopmitt.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GooglePlaceAddressModel {

    @SerializedName("plus_code")
    @Expose
    private var plusCode: PlusCode? = null
    @SerializedName("results")
    @Expose
    private var results: List<Result?>? = null
    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getPlusCode(): PlusCode? {
        return plusCode
    }

    fun setPlusCode(plusCode: PlusCode?) {
        this.plusCode = plusCode
    }

    fun getResults(): List<Result?>? {
        return results
    }

    fun setResults(results: List<Result?>?) {
        this.results = results
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }


    inner class Result {
        @SerializedName("formatted_address")
        @Expose
        var formattedAddress: String? = null
        @SerializedName("place_id")
        @Expose
        var placeId: String? = null

    }

    inner class PlusCode {
        @SerializedName("compound_code")
        @Expose
        var compoundCode: String? = null
        @SerializedName("global_code")
        @Expose
        var globalCode: String? = null

    }
}