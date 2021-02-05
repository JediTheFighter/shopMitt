package com.ecommerce.shopmitt.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartData(

    @ColumnInfo(name = "id")
    var productId: String = "",

    @ColumnInfo(name = "qty")
    var quantity: String = "",

    @ColumnInfo(name = "unit_type")
    var unitType: String? = null,

    @ColumnInfo(name = "wishlist_status")
    var wishListStatus: Boolean = false,

    @ColumnInfo(name = "cart_key")
    var cartKey: String = "",

    @ColumnInfo(name = "stock")
    var stock: String = "",

    @PrimaryKey(autoGenerate = true)
    val table_id: Long? = null,

)