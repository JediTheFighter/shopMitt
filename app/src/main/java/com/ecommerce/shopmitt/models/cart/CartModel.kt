package com.ecommerce.shopmitt.models.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartModel(val productName: String,
                     val price: String,
                     val mrp: String,
                     val sizes: List<String>,
                     val qty: List<String>,
                     val image: Int) : Parcelable