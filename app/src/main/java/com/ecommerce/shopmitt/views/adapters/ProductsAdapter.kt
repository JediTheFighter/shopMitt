package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.db.AppDatabase
import com.ecommerce.shopmitt.db.dao.CartDao
import com.ecommerce.shopmitt.db.entities.CartData
import com.ecommerce.shopmitt.models.AddCart
import com.ecommerce.shopmitt.models.ProductModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.CURRENCY
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.activities.DetailsActivity
import com.ecommerce.shopmitt.views.activities.ProductListActivity
import com.ecommerce.shopmitt.views.activities.SearchActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.*
import kotlin.properties.Delegates

class ProductsAdapter(
    val onItemClicked: (ProductModel.Data) -> Unit,
    val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cartDao: CartDao = AppDatabase.getDatabase(context).cartDao()
    private val itemUpdate: ItemUpdate = context as ItemUpdate


    val VIEW_TYPE_Q = 1
    val VIEW_TYPE_V = 3

    inner class Q_ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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
        val ivSale: ImageView = view.findViewById(R.id.iv_sale)

        fun bind(product: ProductModel.Data, position: Int) {

            if (product.options.isEmpty()) {
                if (product.weight == "0") {
                    tvWeight.visibility = View.INVISIBLE
                } else {
                    tvWeight.text =
                        java.lang.String.format("%s %s", product.weight, product.weightClass)
                }
            } else {
                if (product.options.get(0).optionValue.isEmpty())
                    if (product.weight == "0") {
                        tvWeight.visibility = View.INVISIBLE
                    } else {
                        tvWeight.text =
                            java.lang.String.format("%s %s", product.weight, product.weightClass)
                    }
            }


            tvProductName.text = product.name.replace("&amp;", "&")


            if (product.quantity.toInt() <= 0) {
                tvOffer.visibility = View.GONE
                llAdd.setVisibility(View.GONE)
                tvOutStock.setVisibility(View.VISIBLE)
                tvOutStock.setText(R.string.message_out_of_stock)
                if (product.stockStatus != null && !product.stockStatus
                        .equals("") && !product.stockStatus.equals("In Stock")
                ) tvOutStock.text = product.stockStatus
                tvOutStock.setTextColor(context?.resources!!.getColor(R.color.quantum_googred))
            } else {

                val count = getProductCount(product.productId, product.model)

                Log.i("SGAG", count + "fs")

                if (count == null || count == "0") {
                    tvOffer.visibility = View.VISIBLE
                    llAdd.visibility = View.GONE
                    btnAdd.visibility = View.VISIBLE
                } else {
                    tvOffer.visibility = View.GONE
                    llAdd.visibility = View.VISIBLE
                    edtQty.setText(count.toString())
                    btnAdd.visibility = View.GONE
                }


                tvOutStock.setVisibility(View.GONE)
                tvOutStock.setText(R.string.message_in_stock)
                tvOutStock.setTextColor(context?.getResources().getColor(R.color.colorAccent))
            }


            //  PRICE CHANGE
            val base_price: String
            val base_special: String?
            val total_int: Int
            val mrp_int: Int
            Log.v("Special:", "Special " + product.special)

            if (product.special != null && !product.special
                    .equals("false") && !product.special.equals("0.00")
                && !product.special.equals("0")
            ) {
                base_special = product.special
                base_price = product.price
                tvOriginalPrice.paintFlags =
                    tvOriginalPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
                tvOriginalPrice.visibility = View.VISIBLE

                if (product.getProductOffer() != 0) {
                    tvOffer.setText(product.getProductOffer().toString() + "% OFF")
                    tvOffer.setVisibility(View.VISIBLE)
                    ivSale.setVisibility(View.VISIBLE)
                } else {
                    tvOffer.setVisibility(View.GONE)
                    ivSale.setVisibility(View.GONE)
                }
                mrp_int = base_price.toDouble().toInt()
                total_int = base_special!!.toDouble().toInt()
                tvOriginalPrice.setText(CURRENCY + mrp_int)
                tvProductPrice.setText(CURRENCY + total_int)
                tvSavings.setVisibility(View.VISIBLE)
                val df = DecimalFormat("#.##")
                var sav: Double = product.price.toDouble() - product.special!!.toDouble()
                sav = java.lang.Double.valueOf(df.format(sav))
                tvSavings.setText(context?.getString(R.string.you_save) + sav.toInt())
            } else {
                base_price = product.price
                total_int = base_price.toDouble().toInt()
                tvProductPrice.setText(CURRENCY + total_int)
                tvOriginalPrice.setVisibility(View.GONE)
                tvOffer.setVisibility(View.GONE)
                ivSale.setVisibility(View.GONE)
                tvSavings.setVisibility(View.GONE)
            }

            Glide.with(context)
                .load(product.originalImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.app_logo)
                .into(imgProduct)


        }

        fun onClickAddButton(item: ProductModel.Data) {
            llAdd.visibility = View.VISIBLE
            btnAdd.visibility = View.GONE
            edtQty.setText("0")
            imgAddClick(item)
        }

        fun imgAddClick(product: ProductModel.Data) {

            val stock: Int = product.quantity.toInt()
            val current_qty: Int = edtQty.getText().toString().toInt()
            if (current_qty < stock) {
                imgAdd.clearColorFilter()
                addToCartDb(current_qty)
            } else {
                imgAdd.setColorFilter(ContextCompat.getColor(context, R.color.colorDisabled))
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.we_dont_have) + product.name +
                            context.resources.getString(R.string.as_requested),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun imgRemoveClick(item: ProductModel.Data) {
            val current_qty: Int = edtQty.text.toString().toInt()
            if (current_qty > 1)
                updateCartQuantity(current_qty)
            else if (current_qty == 1) {
                removeItem()
            }
        }

        private fun removeItem() {
            val product = items[adapterPosition]
            showLoadingDialog()
            RestHelper(object : RestResponseHandler {
                override fun onSuccess(`object`: Any?) {
                    hideLoadingDialog()
                    val model = `object` as AddCart
                    btnAdd.visibility = View.VISIBLE
                    llAdd.visibility = View.GONE

                    var trimmed: String = product.name
                    trimmed = trimmed.replace("&amp;".toRegex(), "&")
                    Toast.makeText(
                        context,
                        trimmed + " " + context.resources.getString(R.string.remove_cart),
                        Toast.LENGTH_SHORT
                    ).show()
                    removeItemFromDb(product)
                    itemUpdate.onUpdate()
                }

                override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                    hideLoadingDialog()
                    ToastHelper.instance.show(statusMessage)
                }

            }, context).deleteCartDB(deleteProduct())
        }

        private fun deleteProduct(): JsonObject {
            val product = items[adapterPosition]
            val obj = JsonObject()
            obj.addProperty("key", getItemKeyOfProduct(product.productId))
            return obj
        }

        private fun updateCartQuantity(currentQty: Int) {
            val product = items[adapterPosition]
            showLoadingDialog()
            RestHelper(object : RestResponseHandler {
                override fun onSuccess(`object`: Any?) {
                    hideLoadingDialog()
                    val model = `object` as AddCart
                    val count = currentQty - 1
                    edtQty.setText(count.toString())
                    addProductToLocalDB(model.data?.product?.key, product)
                    itemUpdate.onUpdate()
                }

                override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                    hideLoadingDialog()
                    ToastHelper.instance.show(statusMessage)
                }

            }, context).addToCartDB(updateProductDetails())
        }

        private fun updateProductDetails(): JsonObject {
            val product = items[adapterPosition]
            val obj = JsonObject()
            obj.addProperty("product_id", product.productId)
            obj.addProperty("quantity", "-1")
            return obj
        }

        private fun addToCartDb(currentQty: Int) {
            val product = items[adapterPosition]
            showLoadingDialog()
            RestHelper(object : RestResponseHandler {
                override fun onSuccess(`object`: Any?) {
                    hideLoadingDialog()
                    val model = `object` as AddCart
                    val count = currentQty + 1
                    edtQty.setText(count.toString())
                    addProductToLocalDB(model.data?.product?.key, product)
                    itemUpdate.onUpdate()
                }

                override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                    hideLoadingDialog()
                    ToastHelper.instance.show(statusMessage)
                }

            }, context).addToCartDB(getSelectedProductDetails())
        }

        private fun getSelectedProductDetails(): JsonObject {
            val product = items[adapterPosition]
            val obj = JsonObject()
            obj.addProperty("product_id", product.productId)
            obj.addProperty("quantity", "1")
            return obj
        }

        private fun addProductToLocalDB(key: String?, item: ProductModel.Data) {

            var product = CartData(
                item.productId!!,
                edtQty.text.toString(),
                item.model,
                false,
                key!!,
                item.quantity!!
            )

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    var count: String?
                    withContext(Dispatchers.IO) {
                        count = cartDao.getProductCountById(item.productId!!)
                    }

                    if (count != null && count != "0") {
                        cartDao.updateProductById(
                            item.productId!!,
                            edtQty.text.toString()
                        )

                    } else {
                        withContext(Dispatchers.IO) {
                            cartDao.addProductToCart(product)
                        }
                    }

                }

            }
        }

    }

    private fun showLoadingDialog() {
        if (context.javaClass == ProductListActivity::class.java)
            (context as ProductListActivity).showLoadingDialog()
       else if (context.javaClass == SearchActivity::class.java)
            (context as SearchActivity).showLoadingDialog()
    }

    private fun hideLoadingDialog() {
        if (context.javaClass == ProductListActivity::class.java)
            (context as ProductListActivity).hideLoadingDialog()
        else if (context.javaClass == SearchActivity::class.java)
            (context as SearchActivity).hideLoadingDialog()
    }


    inner class V_ViewHolder(view: View) : RecyclerView.ViewHolder(view), OnItemSelectedListener {

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
        val rlRoot: RelativeLayout = view.findViewById(R.id.rl_root)
        val layoutControls: RelativeLayout = view.findViewById(R.id.layout_controls)
        val btnAdd: RelativeLayout = view.findViewById(R.id.btn_add)
        val tvSavings: TextView = view.findViewById(R.id.tv_savings)
        val tvOffer: TextView = view.findViewById(R.id.tv_offer)
        val tvBrandName: TextView = view.findViewById(R.id.tv_brand_name)
        val ivSale: ImageView = view.findViewById(R.id.iv_sale)
        val spnVariant: Spinner = view.findViewById(R.id.spn_variant)

        fun bind(product: ProductModel.Data, position: Int) {

            spnVariant.onItemSelectedListener = this

            val varients: ArrayList<String> = ArrayList<String>()
            val prices: ArrayList<String> = ArrayList<String>()
            val mrp_list: ArrayList<String> = ArrayList<String>()


            if (product.options[0].optionValue.isNotEmpty()) {
                if (product.quantity.toInt() > 0) {
                    tvOutStock.setVisibility(View.GONE)
                    tvOutStock.setText(R.string.message_in_stock)
                    tvOutStock.setTextColor(context.resources.getColor(R.color.colorAccent))
                    llAdd.setVisibility(View.VISIBLE)
                } else {
                    llAdd.setVisibility(View.GONE)
                    tvOutStock.setVisibility(View.VISIBLE)
                    tvOutStock.setText(R.string.message_out_of_stock)
                    if (product.stockStatus != null && !product.stockStatus
                            .equals("") && !product.stockStatus.equals("In Stock")
                    ) tvOutStock.setText(product.stockStatus)
                    tvOutStock.setTextColor(context.resources.getColor(R.color.quantum_googred))
                    tvProductPrice.setText(
                        Html.fromHtml(
                            "<b>" + product.priceFormatted.toString() + "</b>"
                        )
                    )
                }
                for (i in 0 until product.options.get(0).optionValue.size) {
                    if (product.options.get(0).optionValue.get(i).quantity
                            .toInt() != 0
                    ) {

//                            double base_price = 0;
                        val weight: String =
                            product.options.get(0).optionValue.get(i).name
                        var price: String? =
                            product.options.get(0).optionValue.get(i).specialPrice
                        if (price == null || price == "") {
                            price = product.options.get(0).optionValue.get(i).price
                        }


                        ////////////  spinner logic
                        var base_price: Double
                        var base_special = 0.0
                        var option_base = 0.0
                        var option_special = 0.0
                        var total: String? = null
                        var mrp: String? = null
                        var has_base_special = false
                        // Product Base Original Price
                        base_price = product.price.toDouble()
                        if (product.special != null && !product.special.equals("false")
                            && !product.special.equals("0.00")
                            && !product.special.equals("0")
                        ) {
                            has_base_special = true
                            base_special = product.special!!.toDouble()
                        }
                        val option_base_val: String =
                            product.options.get(0).optionValue.get(i).price
                        val option_special_val: String? =
                            product.options.get(0).optionValue.get(i).specialPrice
                        if (option_special_val != null && option_special_val != "false") {
                            mrp = (base_price + option_base_val!!.toDouble()).toString()
                            option_special = option_special_val.toDouble()
                            total = if (!has_base_special) {
                                (base_price + option_special).toString()
                            } else {
                                (base_special + option_special).toString()
                            }
                        } else {
                            if (option_base_val != null && option_base_val != "false") {
                                option_base = option_base_val.toDouble()
                                mrp = (base_price + option_base_val.toDouble()).toString()
                                total = if (!has_base_special) {
                                    (base_price + option_base).toString()
                                } else {
                                    (base_special + option_base).toString()
                                }
                            }
                        }
                        var total_int: Int
                        var mrp_int: Int
                        if (weight != null) {
                            varients!!.add(weight)
                        }
                        if (total != null) {
                            total_int = total.toDouble().toInt()
                            prices.add(total_int.toString() + "")
                        }
                        if (mrp != null) {
                            mrp_int = mrp.toDouble().toInt()
                            mrp_list.add(mrp_int.toString() + "")
                        }


                        ////////////////////////////////////////////////////////////////////////////////
                    }
                }
            } else {
                llAdd.setVisibility(View.GONE)
                tvOutStock.setVisibility(View.VISIBLE)
                tvOutStock.setText(R.string.message_out_of_stock)
                tvOutStock.setTextColor(context.resources.getColor(R.color.quantum_googred))
                tvProductPrice.setText(
                    Html.fromHtml(
                        "<b>" + product.priceFormatted.toString() + "</b>"
                    )
                )
            }

            val count = getProductCountWithOption(product.productId, product.options[0].name)
            if (count == null || count == "0") {
                btnAdd.visibility = View.VISIBLE
                llAdd.visibility = View.GONE
            } else {
                btnAdd.visibility = View.GONE
                llAdd.visibility = View.VISIBLE
                edtQty.setText(count.toString())
            }
            if (varients != null) {
                /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_dropdown_item, varients);*/
                val spinnerAdapter = CustomSpinnerAdapter(
                    context, R.layout.row_spinner_basic, varients,
                    R.layout.row_option, prices, mrp_list
                )
                spnVariant.setAdapter(spinnerAdapter)
                spnVariant.setDropDownVerticalOffset(0)
            }

            tvProductName.setText(product.name.replace("&amp;", "&"))

            if (product.manufacturer != null) {
                tvBrandName.setVisibility(View.VISIBLE)
                tvBrandName.setText(product.manufacturer)
            } else {
                tvBrandName.setVisibility(View.GONE)
            }


            Glide.with(context)
                .load(product.originalImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.app_logo)
                .into(imgProduct)

            if (product.quantity.toInt() <= 0) {
                tvOutStock.setVisibility(View.VISIBLE)
                tvOutStock.setText("Out of Stock")
                if (product.stockStatus != null && !product.stockStatus
                        .equals("") && !product.stockStatus.equals("In Stock")
                ) tvOutStock.setText(product.stockStatus)
                tvOutStock.setTextColor(context.resources.getColor(R.color.quantum_googred))
                btnAdd.setVisibility(View.GONE)
                llAdd.setVisibility(View.GONE)
            } else {
                tvOutStock.setVisibility(View.GONE)
                llAdd.setVisibility(View.VISIBLE)
            }


        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, i: Int, p3: Long) {
            val df = DecimalFormat("#.##")
            val product: ProductModel.Data = items[adapterPosition]
            val varientQty: String? = getProductCountWithOption(
                product.productId,
                product.options[0].optionValue[i].name
            )

            Log.i("TEgs",product.productId+" is id"+product.options[0].optionValue[i].name+" is name")

            Log.i("TEST",varientQty+ " is varient")

            if (varientQty != null && varientQty != "0") {
                btnAdd.setVisibility(View.GONE)
                llAdd.setVisibility(View.VISIBLE)
                edtQty.setText(varientQty)
            } else {
                btnAdd.setVisibility(View.VISIBLE)
                llAdd.setVisibility(View.GONE)
                edtQty.setText(varientQty)
            }
            tvProductName.setText(product.name.replace("&amp;", "&"))


            if (product.manufacturer != null) {
                tvBrandName.setVisibility(View.VISIBLE)
                tvBrandName.setText(product.manufacturer)
            } else {
                tvBrandName.setVisibility(View.GONE)
            }


            //////////// option price logic
            val base_price: Double
            var base_special = 0.0
            var option_base = 0.0
            var option_special = 0.0
            val total_int: Int
            val mrp_int: Int
            var has_base_special = false
            var total: String? = null
            var mrp: String? = null

            // Product Base Original Price
            base_price = product.price.toDouble()


            if (product.special != null && !product.special.equals("false")
                && !product.special.equals("0.00")
                && !product.special.equals("0")
            ) {
                has_base_special = true
                base_special = product.special!!.toDouble()
            }
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            val option_base_val: String =
                product.options.get(0).optionValue.get(i).price
            val option_special_val: String? =
                product.options.get(0).optionValue.get(i).specialPrice

            if (option_special_val != null && option_special_val != "false") {
                tvOriginalPrice.setVisibility(View.VISIBLE)
                mrp = (base_price + option_base_val!!.toDouble()).toString()
                option_special = option_special_val.toDouble()
                total = if (!has_base_special) {
                    (base_price + option_special).toString()
                } else {
                    (base_special + option_special).toString()
                }
                tvSavings.setVisibility(View.VISIBLE)
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
                    ivSale.setVisibility(View.VISIBLE)
                } else {
                    tvOffer.setVisibility(View.GONE)
                    ivSale.setVisibility(View.GONE)
                }
                mrp_int = mrp.toDouble().toInt()
                total_int = total.toDouble().toInt()
                tvOriginalPrice.setText(CURRENCY + mrp_int)
                tvProductPrice.setText(CURRENCY + total_int)
                if (mrp == total) {
                    tvOffer.setVisibility(View.GONE)
                    tvSavings.setVisibility(View.GONE)
                    tvOriginalPrice.setVisibility(View.GONE)
                    ivSale.setVisibility(View.GONE)
                }
            } else {
                tvOriginalPrice.setVisibility(View.GONE)
                if (option_base_val != null && option_base_val != "false") {
                    option_base = option_base_val.toDouble()
                    mrp = (base_price + option_base_val.toDouble()).toString()
                    mrp_int = mrp.toDouble().toInt()
                    if (!has_base_special) {
                        total = (base_price + option_base).toString()
                    } else {
                        total = (base_special + option_base).toString()
                        tvOriginalPrice.setVisibility(View.VISIBLE)
                        tvOriginalPrice.setText(CURRENCY + mrp_int)
                        var sav = mrp.toDouble() - total.toDouble()
                        sav = java.lang.Double.valueOf(df.format(Math.abs(sav)))
                        tvSavings.setText(context.getString(R.string.you_save) + df.format(sav))
                        if (getOfferPercent(mrp.toDouble(), total.toDouble()) != 0) {
                            tvOffer.setVisibility(View.VISIBLE)
                            ivSale.setVisibility(View.VISIBLE)
                            tvOffer.setText(
                                getOfferPercent(
                                    mrp.toDouble(),
                                    total.toDouble()
                                ).toString() + "% OFF"
                            )
                        } else {
                            tvOffer.setVisibility(View.GONE)
                            ivSale.setVisibility(View.GONE)
                        }
                    }
                    total_int = total.toDouble().toInt()
                    tvProductPrice.setText(CURRENCY + total_int)
                } else {
                    tvSavings.setVisibility(View.GONE)
                    total_int = base_price.toInt()
                    tvProductPrice.setText(CURRENCY + total_int)
                }
            }




            if (product.options.get(0).optionValue.get(i).quantity.toInt() > 0
                && product.quantity.toInt() > 0
            ) {
                llAdd.setVisibility(View.VISIBLE)
                tvOutStock.setVisibility(View.GONE)
                tvOutStock.setText(R.string.message_in_stock)
                tvOutStock.setTextColor(context.resources.getColor(R.color.colorAccent))
            } else {
                btnAdd.setVisibility(View.GONE)
                llAdd.setVisibility(View.GONE)
                tvOutStock.setVisibility(View.VISIBLE)
                tvOutStock.setText(R.string.message_out_of_stock)
                if (product.stockStatus != null && !product.stockStatus
                        .equals("") && !product.stockStatus.equals("In Stock")
                ) tvOutStock.setText(product.stockStatus)
                tvOutStock.setTextColor(context.resources.getColor(R.color.quantum_googred))
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

        fun onClickAddButton(item: ProductModel.Data) {
            llAdd.visibility = View.VISIBLE
            btnAdd.visibility = View.GONE
            edtQty.setText("0")
            imgAddClick(item)
        }

        fun imgAddClick(product: ProductModel.Data) {

            var stock: Int = product.options.get(0).optionValue
                .get(spnVariant.getSelectedItemPosition()).quantity.toInt()
            var quantiti = 0
            Log.v("responce ", "stock " + stock + " quanty " + edtQty.getText().toString())
            if (edtQty.getText().toString() != "") quantiti =
                edtQty.getText().toString().toInt() else edtQty.setText(quantiti.toString() + "")
            if (quantiti < stock && quantiti < product.quantity.toInt()) {
                if (edtQty.getText().toString().equals("", ignoreCase = true)) edtQty.setText("0")
                edtQty.setText((edtQty.getText().toString().toInt() + 1).toString() + "")
                addToCartDb(quantiti)
            } else {
                if (stock > product.quantity.toInt()) stock = product.quantity.toInt()
                edtQty.setText(stock.toString())
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.we_dont_have) + product.name +
                            context.resources.getString(R.string.as_requested),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun imgRemoveClick(item: ProductModel.Data) {

            if (edtQty.getText().toString().equals("", ignoreCase = true)) edtQty.setText("0")
            if (edtQty.getText().toString().toFloat() > 1) {
                edtQty.setText((edtQty.getText().toString().toInt() - 1).toString() + "")
                val stock: Int = item.options.get(0).optionValue
                    .get(spnVariant.getSelectedItemPosition()).quantity.toInt()
                if (edtQty.getText().toString().toInt() > stock) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.product_out_of_stock),
                        Toast.LENGTH_SHORT
                    ).show()
                } else
                    updateCartQuantity()
            } else if (edtQty.getText().toString().toFloat() == 1f) {
                removeItem()
            }
        }

        private fun removeItem() {
            val product = items[adapterPosition]
            showLoadingDialog()
            RestHelper(object : RestResponseHandler {
                override fun onSuccess(`object`: Any?) {
                    hideLoadingDialog()
                    val model = `object` as AddCart
                    btnAdd.visibility = View.VISIBLE
                    llAdd.visibility = View.GONE

                    var trimmed: String = product.name
                    trimmed = trimmed.replace("&amp;".toRegex(), "&")
                    Toast.makeText(
                        context,
                        trimmed + " " + context.resources.getString(R.string.remove_cart),
                        Toast.LENGTH_SHORT
                    ).show()
                    removeItemFromDb(product)
                    itemUpdate.onUpdate()
                }

                override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                    hideLoadingDialog()
                    ToastHelper.instance.show(statusMessage)
                }

            }, context).deleteCartDB(deleteProduct())
        }

        private fun deleteProduct(): JsonObject {
            val product = items[adapterPosition]
            val obj = JsonObject()
            var del_id = "0"
            del_id = getItemKeyOfProduct(product.productId)!!
            Log.v("respop", "key $del_id")
            obj.addProperty("key", del_id)
            return obj
        }

        private fun updateCartQuantity() {
            val product = items[adapterPosition]
            showLoadingDialog()
            RestHelper(object : RestResponseHandler {
                override fun onSuccess(`object`: Any?) {
                    hideLoadingDialog()
                    val model = `object` as AddCart
                    addProductToLocalDB(model.data?.product?.key, product)
                    itemUpdate.onUpdate()
                }

                override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                    hideLoadingDialog()
                    ToastHelper.instance.show(statusMessage)
                }

            }, context).addToCartDB(updateProductDetails())
        }

        private fun updateProductDetails(): JsonObject {
            val product = items[adapterPosition]
            val op_id: String = product.options.get(0).productOptionID
            val p_op_id: String = product.options.get(0).optionValue
                .get(spnVariant.getSelectedItemPosition()).productOptionValueID
            val obj = JsonObject()
            val option = JsonObject()
            option.addProperty(op_id, p_op_id)
            obj.add("option", option)
            obj.addProperty("product_id", product.productId)
            obj.addProperty("quantity", "-1")
            return obj
        }

        private fun addToCartDb(currentQty: Int) {
            val product = items[adapterPosition]
            showLoadingDialog()
            RestHelper(object : RestResponseHandler {
                override fun onSuccess(`object`: Any?) {
                    hideLoadingDialog()
                    val model = `object` as AddCart
                    val count = currentQty + 1
                    edtQty.setText(count.toString())
                    addProductToLocalDB(model.data?.product?.key, product)
                    itemUpdate.onUpdate()
                }

                override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                    hideLoadingDialog()
                    ToastHelper.instance.show(statusMessage)
                }

            }, context).addToCartDB(getSelectedProductDetails())
        }

        private fun getSelectedProductDetails(): JsonObject {
            val product = items[adapterPosition]
            val op_id: String = product.options[0].productOptionID
            val p_op_id: String = product.options[0].optionValue[spnVariant.selectedItemPosition].productOptionValueID
            val obj = JsonObject()
            val option = JsonObject()
            option.addProperty(op_id, p_op_id)
            obj.add("option", option)
            obj.addProperty("product_id", product.productId)
            obj.addProperty("quantity", "1")
            return obj
        }

        private fun addProductToLocalDB(key: String?, item: ProductModel.Data) {

            var product = CartData(
                item.productId!!,
                edtQty.text.toString(),
                item.options[0].optionValue[spnVariant.selectedItemPosition].name,
                false,
                key!!,
                item.quantity!!
            )

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    var count: String?
                    withContext(Dispatchers.IO) {
                        count = cartDao.getProductCountByOption(
                            item.productId!!,
                            item.options[0].optionValue[spnVariant.selectedItemPosition].name
                        )
                    }

                    if (count != null && count != "0") {
                        cartDao.updateProductByOption(
                            item.productId!!,
                            item.options[0].optionValue[spnVariant.selectedItemPosition].name,
                            edtQty.text.toString()
                        )

                    } else {
                        withContext(Dispatchers.IO) {
                            cartDao.addProductToCart(product)
                        }
                    }

                }

            }
        }
    }

    fun getProductCount(productId: String, model: String): String? = runBlocking {
        cartDao.getProductQuantityById(productId, model)
    }

    fun getProductCountWithOption(productId: String, unitType: String): String? = runBlocking {
        cartDao.getProductQuantityByOption(productId, unitType)
    }

    private fun getItemKeyOfProduct(productId: String): String? = runBlocking {
        cartDao.getProductKeyById(productId)
    }

    private fun removeItemFromDb(product: ProductModel.Data) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                withContext(Dispatchers.IO) {
                    cartDao.deleteById(product.productId!!)
                }
            }

        }
    }

    private fun getOfferPercent(product_price: Double, product_special_price: Double): Int {
        return if (product_special_price != 0.0 && product_price != 0.0) {
            var off = 0.0
            off = (product_price - product_special_price) / product_price * 100
            Math.round(Math.abs(off)).toInt()
        } else 0
    }


    var items: MutableList<ProductModel.Data> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder

        viewHolder = if (viewType == VIEW_TYPE_Q) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_q, parent, false)
            Q_ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_v, parent, false)
            V_ViewHolder(view)
        }

        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {

        val products: ProductModel.Data = items[position]
        return when {
            products.options.isEmpty() -> VIEW_TYPE_Q
            products.options[0].optionValue.isEmpty() -> VIEW_TYPE_Q
            products.options.isNotEmpty() -> VIEW_TYPE_V
            else -> super.getItemViewType(position)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = items[position]

        if (holder is Q_ViewHolder) {
            (holder).bind(item, position)
            holder.rlRoot.setOnClickListener {
                moveToDetails(item)
            }

            holder.btnAdd.setOnClickListener {
                holder.onClickAddButton(item)
            }

            holder.imgAdd.setOnClickListener {
                holder.imgAddClick(item)
            }

            holder.imgRemove.setOnClickListener {
                holder.imgRemoveClick(item)
            }
        } else if (holder is V_ViewHolder) {
            holder.bind(item, position)
            holder.rlRoot.setOnClickListener {
                moveToDetails(item)
            }

            holder.btnAdd.setOnClickListener {
                holder.onClickAddButton(item)
            }

            holder.imgAdd.setOnClickListener {
                holder.imgAddClick(item)
            }

            holder.imgRemove.setOnClickListener {
                holder.imgRemoveClick(item)
            }
        }

    }

    private fun moveToDetails(item: ProductModel.Data) {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra("product_id", item.productId)
        context.startActivity(intent)
    }

    override fun getItemCount() = items.size

    interface ItemUpdate {
        fun onUpdate()
    }
}