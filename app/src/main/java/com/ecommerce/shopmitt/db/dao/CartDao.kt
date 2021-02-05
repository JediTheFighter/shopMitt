package com.ecommerce.shopmitt.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ecommerce.shopmitt.db.entities.CartData
import com.ecommerce.shopmitt.models.OrderDetailsModel
import com.ecommerce.shopmitt.models.ViewCart

@Dao
interface CartDao {

    @Query("SELECT COUNT(*) FROM cart WHERE id=:productId")
    suspend fun getProducts(productId: String): Int

    @Query("SELECT COUNT(*) FROM cart")
    suspend fun getTotalProductCount(): Int

    @Insert
    suspend fun addProductToCart(product: CartData)

    /// DELETE

    @Query("DELETE FROM cart WHERE id = :productId")
    suspend fun deleteById(productId: String): Int

    @Query("DELETE FROM cart WHERE id = :productId AND unit_type = :unitType")
    suspend fun deleteByOption(productId: String, unitType: String): Int

    @Query("DELETE FROM cart")
    suspend fun clearTable()

    /// UPDATE

    @Query("UPDATE cart SET qty = :itemQty WHERE id = :productId")
    suspend fun updateProductById(productId: String, itemQty: String)


    @Query("UPDATE cart SET qty = :itemQty  WHERE id = :productId AND unit_type =:unitType")
    suspend fun updateProductByOption(productId: String, unitType: String, itemQty: String)


    /// KEY - CART

    @Query("SELECT cart_key FROM cart WHERE id=:productId")
    suspend fun getProductKeyById(productId: String): String

    @Query("SELECT cart_key FROM cart WHERE id=:productId AND unit_type = :unitType")
    suspend fun getProductKeyByOption(productId: String, unitType: String): String


    /// PRODUCT COUNT

    @Query("SELECT qty FROM cart WHERE id=:productId")
    suspend fun getProductCountById(productId: String): String?

    @Query("SELECT COUNT(*) FROM cart WHERE cart_key=:key")
    suspend fun getProductCountByKey(key: String): Int

    @Query("SELECT qty FROM cart WHERE id=:productId AND unit_type=:unitType")
    suspend fun getProductCountByOption(productId: String, unitType: String): String?

    /// QUANTITY COUNT

    @Query("SELECT qty FROM cart WHERE id=:productId AND unit_type=:model")
    suspend fun getProductQuantityById(productId: String, model: String): String?

    @Query("SELECT qty FROM cart WHERE cart_key=:key")
    suspend fun getProductQuantityByKey(key: String): String?

    @Query("SELECT qty FROM cart WHERE id=:productId AND unit_type=:unitType")
    suspend fun getProductQuantityByOption(productId: String, unitType: String): String?



    suspend fun addBulkProducts(data: ViewCart.Data) {

        for (i in 0 until data.products?.size!!) {

            val item = data.products!![i]

            val product : CartData

            if (item.options.isNotEmpty()) {
                product = CartData(item.productId!!,item.quantity!!,item.options[0].name,false,item.key!!,item.stockQuantity!!)
            } else {
                product = CartData(item.productId!!,item.quantity!!,item.model,false,item.key!!,item.stockQuantity!!)
            }

            addProductToCart(product)
        }
    }



}