package com.ecommerce.shopmitt.models

import java.util.*

class PlaceData {


    private lateinit var placeId: String
    private lateinit var placeText: String
    private lateinit var primaryText: String
    private var latitude = 0.0
    private var longitude = 0.0

    fun getLatitude(): Double {
        return latitude
    }

    fun setLatitude(latitude: Double) {
        this.latitude = latitude
    }

    fun getLongitude(): Double {
        return longitude
    }

    fun setLongitude(longitude: Double) {
        this.longitude = longitude
    }

    fun getPrimaryText(): String? {
        return primaryText
    }

    fun setPrimaryText(primaryText: String) {
        this.primaryText = primaryText
    }

    fun getSecondaryText(): String? {
        return secondaryText
    }

    fun setSecondaryText(secondaryText: String?) {
        this.secondaryText = secondaryText
    }

    private var secondaryText: String? = null

    fun getPlaceId(): String {
        return placeId
    }

    fun setPlaceId(placeId: String) {
        this.placeId = placeId
    }

    fun getPlaceText(): String {
        return placeText
    }

    fun setPlaceText(placeText: String) {
        this.placeText = placeText
    }

    /*public String toString() {
        return placeText;
    }*/

    /*public String toString() {
        return placeText;
    }*/
    fun getData(): MutableList<PlaceData>? {
        return data
    }

    fun setData(data: MutableList<PlaceData>?) {
        this.data = data
    }

    private var data: MutableList<PlaceData>? = null

    fun getData_(): PlaceData? {
        return data_
    }

    fun setData_(data_: PlaceData?) {
        this.data_ = data_
    }

    private var data_: PlaceData? = null


    fun addPlaceToList(primaryText: String, secText: String?,
                       placeId: String, placeText: String,
                       latitude: Double, longitude: Double) {
        if (!isPlaceAlreadyExist(primaryText)) {
            val data = PlaceData()
            data.setPrimaryText(primaryText)
            data.setSecondaryText(secText)
            data.setPlaceId(placeId)
            data.setPlaceText(placeText)
            data.setLatitude(latitude)
            data.setLongitude(longitude)
            if (getData() == null) setData(ArrayList())
            getData()!!.add(data)
        }
    }

    private fun isPlaceAlreadyExist(primaryText: String): Boolean {
        if (getData() == null) return false
        if (getData()!!.size == 0) return false
        for (i in getData()!!.indices) {
            if (getData()!![i].getPrimaryText() == primaryText) {
                return true
            }
        }
        return false
    }
}