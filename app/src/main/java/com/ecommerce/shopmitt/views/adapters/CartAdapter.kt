package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.db.AppDatabase
import com.ecommerce.shopmitt.db.dao.CartDao
import com.ecommerce.shopmitt.db.entities.CartData
import com.ecommerce.shopmitt.models.AddCart
import com.ecommerce.shopmitt.models.ViewCart
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.CURRENCY
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.CartActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class CartAdapter(val onItemClicked: (ViewCart.Data.Product) -> Unit, val context: Context) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var currOptionPos: Int = 0
    var hasOption: Boolean = false

    var cartDao: CartDao = AppDatabase.getDatabase(context).cartDao()
    val cartBridge: CartBridge = context as CartBridge

    var items: MutableList<ViewCart.Data.Product> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_cart, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    private fun showLoadingDialog() {
        (context as CartActivity).showLoadingDialog()
    }

    private fun hideLoadingDialog() {
        (context as CartActivity).hideLoadingDialog()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]

        holder.bind(item, position)

        holder.imgRemove.setOnClickListener {
            holder.handleCartRemove(item)
        }

        holder.imgAdd.setOnClickListener {
            holder.handleCartAdd(item)
        }

        holder.tvRemove.setOnClickListener {
            holder.removeProduct(item, position)
        }

    }


    override fun getItemCount() = items.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgAdd: ImageView = view.findViewById(R.id.img_add)
        val imgRemove: ImageView = view.findViewById(R.id.img_remove)
        val imgProduct: ImageView = view.findViewById(R.id.img_product)
        val edtQty: EditText = view.findViewById(R.id.edt_qty)
        val tvProductName: TextView = view.findViewById(R.id.tv_product_name)
        val tvDefaultUnit: TextView = view.findViewById(R.id.tv_default_unit)
        val tvOriginalPrice: TextView = view.findViewById(R.id.tv_original_price)
        val tvProductPrice: TextView = view.findViewById(R.id.tv_product_price)
        val tvOutStock: TextView = view.findViewById(R.id.txt_out_stock)
        val llAdd: RelativeLayout = view.findViewById(R.id.ll_add)
        val layoutControls: RelativeLayout = view.findViewById(R.id.layout_controls)
        val btnAdd: RelativeLayout = view.findViewById(R.id.btn_add)
        val rlRoot: RelativeLayout = view.findViewById(R.id.rl_root)
        val tvSavings: TextView = view.findViewById(R.id.tv_savings)
        val tvOffer: TextView = view.findViewById(R.id.tv_offer)
        val tvWeight: TextView = view.findViewById(R.id.tv_weight)
        val tvBrandName: TextView = view.findViewById(R.id.tv_brand_name)
        val tvRemove: TextView = view.findViewById(R.id.tv_remove)
        val tvProductTotal: TextView = view.findViewById(R.id.tv_amnt)
        val ivSale: ImageView = view.findViewById(R.id.iv_sale)


        fun bind(item: ViewCart.Data.Product, position: Int) {

            btnAdd.setVisibility(View.GONE)
            llAdd.setVisibility(View.VISIBLE)

            val product: ViewCart.Data.Product = items.get(adapterPosition)
            edtQty.setText(product.quantity)
            if (edtQty.getText().toString() == "1")
                imgRemove.setColorFilter(ContextCompat.getColor(context, R.color.colorDisabled))
            if (product.options.size != 0)
                tvProductName.setText(
                    product.name?.replace("&amp;", "&").toString() + " " + product.options.get(
                        0
                    ).value
                )
            else
                tvProductName.setText(product.name?.replace("&amp;", "&"))
            if (product.stock == false) {
                tvProductName.setTextColor(ContextCompat.getColor(context, R.color.quantum_grey))
                imgAdd.setEnabled(false)
                tvOutStock.setVisibility(View.GONE)
                tvOutStock.setText(R.string.message_out_of_stock)
                tvOutStock.setTextColor(context.resources.getColor(R.color.quantum_googred))
//                llAdd.setVisibility(View.GONE);
//                btn_order.setEnabled(false);
            } else {
                tvOutStock.setVisibility(View.GONE)
                tvOutStock.setText(R.string.message_in_stock)
                tvProductName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorTextPrimary
                    )
                )
                tvOutStock.setTextColor(context.resources.getColor(R.color.colorAccent))
            }

            /*String[] arrOfStr = product.getPrice().split("₹", 2);
            String price_new = arrOfStr[1];
            int price = (int) Double.parseDouble(price_new.trim());*/


            /*String[] arrOfStr = product.getPrice().split("₹", 2);
            String price_new = arrOfStr[1];
            int price = (int) Double.parseDouble(price_new.trim());*/

            if (product.manufacturer != null) {
                tvBrandName.setVisibility(View.VISIBLE)
                tvBrandName.setText(product.manufacturer)
            } else {
                tvBrandName.setVisibility(View.GONE)
            }


            if (product.options.isEmpty()) {
                if (product.weight != null && product.weight.trim().equals("0")) {
                    tvWeight.setVisibility(View.INVISIBLE)
                } else {
                    if (product.options.size > 0 && product.options.get(0).value!!.contains("+"))
                            tvWeight.setText(product.options.get(0).value)
                    else tvWeight.setText(java.lang.String.format("%s %s", product.weight, product.weight_class))
                }
            } else {
                if (product.weight != null && product.weight.trim().equals("0")) {
                    tvWeight.setVisibility(View.INVISIBLE)
                } else {
                    if (product.options.size > 0 &&
                        product.options.get(0).value!!.contains("+"))
                            tvWeight.setText(product.options.get(0).value)
                    else
                        tvWeight.setText(product.options.get(0).value)
                    //                        tvWeight.setText(String.format("%s %s", product.getWeight(), product.getWeight_class()));
                }
            }


            val mrp_int: Int
            val total_int: Int
            val total_sum: Int
            if (product.options.isNotEmpty()) {
                val optionDetails: ViewCart.Data.Product.Option = product.options[0]


                //////////// option price logic
                val base_price: Double
                var base_special = 0.0
                var option_base = 0.0
                var option_special = 0.0
                var has_base_special = false
                var total: String? = null
                var mrp: String? = null

                // Product Base Original Price
                base_price = product.price!!.toDouble()
                if (product.special != null && !product.special
                        .equals("false") && !product.special.equals("0.00")
                    && !product.special.equals("0")
                ) {
                    has_base_special = true
                    base_special = product.special.toDouble()
                }
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                val option_base_val: String = optionDetails.optionPrice!!
                val option_special_val: String? = optionDetails.specialPrice
                if (option_special_val != null && option_special_val != "0.0000" && option_special_val != "false") {
                    tvOriginalPrice.setVisibility(View.VISIBLE)
                    mrp = (base_price + option_base_val!!.toDouble()).toString()
                    option_special = option_special_val.toDouble()
                    total = if (!has_base_special) {
                        (base_price + option_special).toString()
                    } else {
                        (base_special + option_special).toString()
                    }
                    tvSavings.setVisibility(View.GONE)
                    //                    tvSavings.setVisibility(View.VISIBLE);
                    val df = DecimalFormat("#.##")
                    var sav = mrp.toDouble() - total.toDouble()
                    sav = java.lang.Double.valueOf(df.format(Math.abs(sav)))
                    tvSavings.setText(context.getString(R.string.you_save) + df.format(sav))
                    if (getOfferPercent(mrp.toDouble(), total.toDouble()) != 0) {
                        tvOffer.setVisibility(View.VISIBLE)
                        tvOffer.setText(
                            getOfferPercent(
                                mrp.toDouble(),
                                total.toDouble()
                            ).toString() + "% OFF"
                        )
                    } else tvOffer.setVisibility(View.GONE)
                    mrp_int = mrp.toDouble().toInt()
                    total_int = total.toDouble().toInt()
                    total_sum = total_int * product.quantity!!.toInt()
                    tvOriginalPrice.setText(CURRENCY + mrp_int)
                    tvProductPrice.setText(CURRENCY + total_int)
                    tvProductTotal.setText(CURRENCY + total_sum)
                    if (tvProductPrice.getText().toString()
                            .replace(" ", "") == tvOriginalPrice.getText().toString()
                            .replace(" ", "")
                    ) {
                        tvOriginalPrice.setVisibility(View.GONE)
                        tvOffer.setVisibility(View.GONE)
                    }
                    if (mrp == total) {
                        tvOffer.setVisibility(View.GONE)
                        tvSavings.setVisibility(View.GONE)
                        tvOriginalPrice.setVisibility(View.GONE)
                    }
                } else {
                    tvOriginalPrice.setVisibility(View.GONE)
                    if ( option_base_val != "0.0000" && option_base_val != "false") {
                        tvSavings.setVisibility(View.GONE)
                        option_base = option_base_val.toDouble()
                        mrp = (base_price + option_base_val.toDouble()).toString()
                        mrp_int = mrp.toDouble().toInt()
                        if (!has_base_special) {
                            val price = base_price + option_base
                            total = price.toString()
                        } else {
                            total = (base_special + option_base).toString()
                            tvOriginalPrice.setVisibility(View.VISIBLE)
                            tvOriginalPrice.setText(CURRENCY + mrp_int)
                            val df = DecimalFormat("#.##")
                            var sav = mrp.toDouble() - total.toDouble()
                            sav = java.lang.Double.valueOf(df.format(Math.abs(sav)))
                            tvSavings.setText(context.getString(R.string.you_save) + df.format(sav))
                            if (getOfferPercent(mrp.toDouble(), total.toDouble()) != 0) {
                                tvOffer.setVisibility(View.VISIBLE)
                                tvOffer.setText(
                                    getOfferPercent(
                                        mrp.toDouble(),
                                        total.toDouble()
                                    ).toString() + "% OFF"
                                )
                            } else tvOffer.setVisibility(View.GONE)
                        }
                        total_int = total.toDouble().toInt()
                        total_sum = total_int * product.quantity!!.toInt()
                        tvProductPrice.setText(CURRENCY + total_int)
                        tvProductTotal.setText(CURRENCY + total_sum)
                    } else {
                        tvSavings.setVisibility(View.GONE)
                        total_int = base_price.toInt()
                        total_sum = total_int * product.quantity!!.toInt()
                        tvProductPrice.setText(CURRENCY + total_int)
                        tvProductTotal.setText(CURRENCY + total_sum)
                    }
                }

                /////////////////////////
            } else {
                val base_price: String
                val base_special: String?
                Log.v("Special:", "Special " + product.special)
                if (product.special != null && !product.special
                        .equals("false") && !product.special.equals("0.00")
                    && !product.special.equals("0")
                ) {
                    base_special = product.special
                    base_price = product.price!!
                    tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    tvOriginalPrice.setVisibility(View.VISIBLE)
                    if (product.getProductOffer() != 0) {
                        tvOffer.setText(product.getProductOffer().toString() + "% OFF")
                        tvOffer.setVisibility(View.VISIBLE)
                    } else {
                        tvOffer.setVisibility(View.GONE)
                    }
                    mrp_int = base_price.toDouble().toInt()
                    total_int = base_special.toDouble().toInt()
                    total_sum = total_int * product.quantity!!.toInt()
                    tvOriginalPrice.setText(CURRENCY + mrp_int)
                    tvProductPrice.setText(CURRENCY + total_int)
                    tvProductTotal.setText(CURRENCY + total_sum)
                    tvSavings.setVisibility(View.VISIBLE)
                    val df = DecimalFormat("#.##")
                    var sav: Double =
                        product.price!!.toDouble() - product.special.toDouble()
                    sav = java.lang.Double.valueOf(df.format(sav))
                    tvSavings.setText(context.getString(R.string.you_save) + sav)
                    tvSavings.setVisibility(View.GONE)
                } else {
                    base_price = product.price!!
                    total_int = base_price.toDouble().toInt()
                    total_sum = total_int * product.quantity!!.toInt()
                    tvProductPrice.setText(CURRENCY + total_int)
                    tvProductTotal.setText(CURRENCY + total_sum)
                    tvOriginalPrice.setVisibility(View.GONE)
                    tvOffer.setVisibility(View.GONE)
                    tvSavings.setVisibility(View.GONE)
                }
            }



            Glide.with(context)
                .load(product.thumb)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.app_logo)
                .into(imgProduct)


        }

        private fun setSizeOptions(option: ViewCart.Data.Product.Option, sizeText: TextView) {
            sizeText.text = "Size : " + option.value
        }

        private fun setColorOptions(option: ViewCart.Data.Product.Option, colorText: TextView) {
            colorText.text = "Color : " + option.value
        }

        private fun setOptionText(option: ViewCart.Data.Product.Option, sizeText: TextView) {
            sizeText.text = "Qty : " + option.value
        }

        private fun setQtyText(qty: String, sizeText: TextView) {
            sizeText.text = "" + qty
        }

        private fun updateCartQuantity(isAdded: Boolean, item: ViewCart.Data.Product) {
            showLoadingDialog()
            RestHelper(object : RestResponseHandler {
                override fun onSuccess(`object`: Any?) {
                    hideLoadingDialog()
                    val model = `object` as AddCart
                    cartBridge.updateItemCount(edtQty.text.toString().toInt(), itemCount, isAdded)
                    cartBridge.refreshCart()
                    addProductToLocalDB(item.key, item)
                }

                override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                    hideLoadingDialog()
                    ToastHelper.instance.show(statusMessage)
                }

            }, context).updateCartDB(updateProductDetails(isAdded, item))
        }

        private fun updateProductDetails(
            isAdded: Boolean,
            item: ViewCart.Data.Product
        ): JsonObject {
            val obj = JsonObject()
            obj.addProperty("key", item.key)
            obj.addProperty("quantity", edtQty.text.toString())
            if (isAdded) {
                obj.addProperty("is_delete ", "0")
            } else {
                obj.addProperty("is_delete ", "1")
            }
            return obj
        }

        fun handleCartAdd(item: ViewCart.Data.Product) {

            val stock = item.stockQuantity?.toInt()!!
            if (edtQty.text.toString().toInt() < stock) {
                //            qty.set(getAdapterPosition(), Float.parseFloat(edtQty.getText().toString()) + 1);
                if (edtQty.text.toString().equals("", ignoreCase = true))
                    edtQty.setText("0")
                if (edtQty.text.toString().toInt() > 1)
                    imgRemove.clearColorFilter()
                edtQty.setText((edtQty.text.toString().toInt() + 1).toString() + "")
                updateCartQuantity(true, item)
            } else {
                imgAdd.setColorFilter(ContextCompat.getColor(context, R.color.colorDisabled))
                Toast.makeText(
                    context,
                    context.getString(R.string.we_dont_have)
                        .toString() + " " + item.name + " " + context.getString(R.string.as_requested),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun handleCartRemove(item: ViewCart.Data.Product) {
            if (edtQty.text.toString().toFloat() <= 1) {
                imgRemove.setColorFilter(ContextCompat.getColor(context, R.color.colorDisabled))
                return
            }
            showLoadingDialog()
            edtQty.setText((edtQty.text.toString().toInt() - 1).toString() + "")
            updateCartQuantity(false, item)
        }

        private fun addProductToLocalDB(key: String?, item: ViewCart.Data.Product) {

            lateinit var product: CartData

            if (item.options.isEmpty()) {
                product = CartData(
                    item.productId!!,
                    edtQty.text.toString(),
                    item.model,
                    false,
                    key!!,
                    item.quantity!!
                )
            } else {

                product = CartData(
                    item.productId!!,
                    edtQty.text.toString(),
                    item.options[currOptionPos].name,
                    false,
                    key!!,
                    item.quantity!!,
                )


            }

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    var count: String?
                    withContext(Dispatchers.IO) {

                        if (item.options.isEmpty()) {
                            count = cartDao.getProductCountById(item.productId!!)
                        } else {
                            count = cartDao.getProductCountByOption(
                                item.productId!!,
                                item.options[currOptionPos].name!!
                            )
                        }
                    }

                    if (count != null && count != "0") {

                        if (hasOption) {
                            cartDao.updateProductByOption(
                                item.productId!!,
                                item.options[currOptionPos].name!!,
                                edtQty.text.toString()
                            )
                        } else {
                            cartDao.updateProductById(
                                item.productId!!,
                                edtQty.text.toString()
                            )
                        }

                    } else {
                        withContext(Dispatchers.IO) {
                            cartDao.addProductToCart(product)
                        }
                    }

                }

            }

        }

        fun removeProduct(item: ViewCart.Data.Product, position: Int) {
            showLoadingDialog()
            RestHelper(object : RestResponseHandler {
                override fun onSuccess(`object`: Any?) {
                    hideLoadingDialog()
                    val model = `object` as AddCart
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)

                    if (itemCount == 0) {
                        cartBridge.onEmptyProducts()
                    } else {
                        cartBridge.updateItemCount(edtQty.text.toString().toInt(), itemCount, false)
                        cartBridge.refreshCart()
                        deleteProductFromLocal(item)
                    }
                }

                override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                    hideLoadingDialog()
                    ToastHelper.instance.show(statusMessage)
                }

            }, context).deleteCartDB(deleteProduct(item))
        }

        private fun deleteProductFromLocal(item: ViewCart.Data.Product) {

            GlobalScope.launch {
                withContext(Dispatchers.Main) {

                    withContext(Dispatchers.IO) {

                        if (hasOption) {
                            cartDao.deleteByOption(
                                item.productId!!,
                                item.options[currOptionPos].name!!
                            )
                        } else {
                            cartDao.deleteById(item.productId!!)
                        }

                    }
                }
            }

        }

        private fun deleteProduct(item: ViewCart.Data.Product): JsonObject {
            val obj = JsonObject()
            obj.addProperty("key", item.key)
            return obj
        }
    }

    private fun getOfferPercent(product_price: Double, product_special_price: Double): Int {
        return if (product_special_price != 0.0 && product_price != 0.0) {
            var off = 0.0
            off = (product_price - product_special_price) / product_price * 100
            abs(off).roundToInt().toInt()
        } else 0
    }


    interface CartBridge {
        fun updateItemCount(itemCount: Int, productCount: Int, isAdded: Boolean)
        fun onEmptyProducts()
        fun refreshCart()
    }

}