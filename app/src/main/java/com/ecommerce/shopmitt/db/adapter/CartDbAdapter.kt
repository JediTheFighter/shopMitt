package com.ecommerce.shopmitt.db.adapter

import android.content.Context
import android.database.Cursor
import com.ecommerce.shopmitt.db.schema.CartDB

class CartDbAdapter(mContext: Context) {

    var cartDb: CartDB? = null
    private var context: Context? = null

    init {
        this.context = mContext
        this.cartDb = CartDB(context!!)
    }

    fun getQtyOfProduct(productId: String): String? {
        var cursor: Cursor? = null
        return try {
            cursor = cartDb!!.readableDatabase.rawQuery(
                "select *  from " + CartDB.TABLE_CART.toString() + " where " + CartDB.FIELD_PRODUCT_ID.toString() + "='" + productId + "'",
                null
            )
            if (cursor == null || cursor.count == 0) return "0"
            cursor.moveToFirst()
            val s = cursor.getString(cursor.getColumnIndex(CartDB.FIELD_QUANTITY))
            cursor.close()
            s
        } catch (e: Exception) {
            "0"
        }
    }
}