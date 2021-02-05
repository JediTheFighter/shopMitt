package com.ecommerce.shopmitt.db.schema

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.ecommerce.shopmitt.db.BaseDB

class CartDB(mContext: Context) : BaseDB(mContext) {

    companion object {
        const val FIELD_PRODUCT_INFO = "product_info"
        const val FIELD_QUANTITY = "qty"
        const val FIELD_UNIT_TYPE = "unit_type"
        const val FIELD_STOCK = "stock"
        const val FIELD_PRODUCT_ID = "product_id"
        const val FIELD_WISHLIST = "wishlist"
        const val FIELD_CART_KEY = "cart_key"
        const val TABLE_CART = "cart"
    }

    var context: Context? = null
    private var database: SQLiteDatabase? = null

    init {
        this.context = mContext
        this.database = writableDatabase
        this.createTableIfNotExists()
    }

    private fun createTableIfNotExists() {
        database!!.execSQL(
            "create table if not exists " + TABLE_CART
                    + " (" + FIELD_ID + " integer primary key autoincrement,"
                    + FIELD_PRODUCT_INFO + " text,"
                    + FIELD_QUANTITY + " text,"
                    + FIELD_PRODUCT_ID + " text,"
                    + FIELD_UNIT_TYPE + " text,"
                    + FIELD_WISHLIST + " text,"
                    + FIELD_CART_KEY + " text,"
                    + FIELD_STOCK + " text);"
        )
//        database.close();
    }

    fun dropTable() {
        database!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CART)
        database!!.close()
    }

    fun removeProductFromCart(productId: String) {
        database!!.delete(
            TABLE_CART,
            FIELD_PRODUCT_ID + "=?",
            arrayOf(productId)
        )
        database!!.close()
    }

    fun removeProductFromCart(productId: String, unit: String) {
        database!!.delete(
            TABLE_CART,
            FIELD_PRODUCT_ID + "=? AND " + FIELD_UNIT_TYPE + " = ? ",
            arrayOf(productId, unit)
        )
        database!!.close()
    }

    fun removeCartItem(cartId: String) {
        database!!.delete(TABLE_CART, "$FIELD_ID=?", arrayOf(cartId))
        database!!.close()
    }

    fun getItemIdOfProduct(productId: String): String? {
        val cursor = readableDatabase.query(
            TABLE_CART,
            null,
            FIELD_PRODUCT_ID + "=" + productId,
            null,
            null,
            null,
            null
        )
        //        Cursor cursor = getReadableDatabase().rawQuery("select *  from " + CartDB.TABLE_CART + " where " + CartDB.FIELD_PRODUCT_ID + "='" + productId + "'", null);
        if (cursor.count == 0) {
            Log.v("responce", "count zero")
            return "0"
        }
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(FIELD_CART_KEY))
        }
        val value =
            cursor.getString(cursor.getColumnIndex(FIELD_CART_KEY))
        cursor.close()
        return value
    }

    /*fun addBulk(product: ViewCart.Data) {
        for (i in 0 until product.getProducts().size()) {
            val values = ContentValues()
            var unit = ""
            values.put(
                FIELD_PRODUCT_INFO,
                Gson().toJson(product.getProducts().get(i))
            )
            values.put(
                FIELD_QUANTITY,
                product.getProducts().get(i).getQuantity()
            )
            values.put(
                FIELD_PRODUCT_ID,
                product.getProducts().get(i).getProductId()
            )
            values.put(
                FIELD_CART_KEY,
                product.getProducts().get(i).getKey()
            )
            if (product.getProducts().get(i).getOption().size() > 0) unit =
                product.getProducts().get(i).getOption().get(0).getValue()
            values.put(FIELD_UNIT_TYPE, unit)
            val cursor = readableDatabase.rawQuery(
                "select * from " + TABLE_CART + " where " + FIELD_PRODUCT_ID + " ='" + product.getProducts()
                    .get(i)
                    .getProductId() + "' AND " + FIELD_UNIT_TYPE + "='" + unit + "'",
                null
            )
            if (cursor.count != 0) {
                cursor.moveToFirst()
                database!!.update(
                    TABLE_CART, values,
                    "$FIELD_ID=?", arrayOf(cursor.getString(cursor.getColumnIndex(FIELD_ID)))
                )
            } else {
                database!!.insert(TABLE_CART, null, values)
            }
            //            cursor.close();
        }
        database!!.close()
    }*/
}