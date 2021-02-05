package com.ecommerce.shopmitt.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.BuildConfig
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.CountMenuActivity
import com.ecommerce.shopmitt.databinding.ActivityDetailsBinding
import com.ecommerce.shopmitt.db.AppDatabase
import com.ecommerce.shopmitt.db.dao.CartDao
import com.ecommerce.shopmitt.db.entities.CartData
import com.ecommerce.shopmitt.models.AddCart
import com.ecommerce.shopmitt.models.DetailsModel
import com.ecommerce.shopmitt.models.ProductModelWishlist
import com.ecommerce.shopmitt.models.WishListResponse
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants
import com.ecommerce.shopmitt.views.adapters.ColorOptionsAdapter
import com.ecommerce.shopmitt.views.adapters.ProductImagesAdapter
import com.ecommerce.shopmitt.views.adapters.RelatedProductsAdapter
import com.ecommerce.shopmitt.views.fragments.QuantityDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class DetailsActivity : CountMenuActivity(), ColorOptionsAdapter.ColorSelected,
    QuantityDialogFragment.OptionBridge {

    private var currOptionPosForSize: Int = 0
    private var currOptionValPosForSize: Int = 0
    private var currOptionPosForColor: Int = 0
    private var currOptionValPosForColor: Int = 0

    private lateinit var item: DetailsModel.Data

    private var colorsAdapter: ColorOptionsAdapter? = null

    private var imageAdapter: ProductImagesAdapter? = null

    private var disposableProductDetails: Disposable? = null
    private var disposableAddToCart: Disposable? = null
    private var disposableUpdateCart: Disposable? = null
    private var disposableRemoveCart: Disposable? = null
    private var disposableIsWishListed: Disposable? = null
    private var disposableAddWishList: Disposable? = null
    private var disposableRemoveWishList: Disposable? = null

    private lateinit var productId: String

    private lateinit var binding: ActivityDetailsBinding

    private var currentOption: DetailsModel.Data.Option? = null
    private var optionsData: List<DetailsModel.Data.Option> = mutableListOf()

    private var hasOption: Boolean = false
    private var currOptionPos: Int = 0

    var displayMetrics: DisplayMetrics? = null

    private lateinit var cartDao: CartDao

    private var isApisCalled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartDao = AppDatabase.getDatabase(this).cartDao()


        displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics!!.widthPixels
        binding.rlImages.layoutParams.height = width
        binding.rlImages.requestLayout()

        getExtrasData()

        binding.btnAdd.setOnClickListener {


            binding.llAdd.visibility = View.VISIBLE
            binding.btnAdd.visibility = View.GONE
            binding.edtQty.setText("0")
            onClickAdd()
        }

        binding.tvQuantity.setOnClickListener {
            if (hasOption)
                QuantityDialogFragment.newInstance(item)
                    .show(supportFragmentManager, "quantity_dialog")
        }

        binding.imgAdd.setOnClickListener {
            onClickAdd()
        }

        binding.imgRemove.setOnClickListener {
            val current_qty = binding.edtQty.text.toString().toInt()
            if (current_qty > 1)
                updateCartQuantity(current_qty)
            else if (current_qty == 1) {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        removeItem()
                    }
                }
            }
        }

        binding.favIconFill.setOnClickListener {
            removeWishList()
        }

        binding.favIconEmpty.setOnClickListener {
            if (AppDataManager.instance.isLoggedIn) {
                addToWishList()
            } else {
                getToast().show("Login to continue")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()

        if (isApisCalled) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    callDBCount()
                }
            }
        }
    }

    private fun addToWishList() {
        showLoadingDialog()
        disposableAddWishList = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as WishListResponse
                isWishlisted(true)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).addWishlist(item.productId)
    }

    private fun removeWishList() {
        showLoadingDialog()
        disposableRemoveWishList = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as WishListResponse
                isWishlisted(false)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).removeWishlist(item.productId)
    }

    private suspend fun removeItem() {
        showLoadingDialog()
        disposableRemoveCart = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as AddCart
                removeProductFromLocalDB(1)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).deleteCartDB(deleteProduct())
    }

    private suspend fun getKeyOfDeleted(): String = withContext(Dispatchers.IO) {

        val key: String

        if (hasOption) {
            key = cartDao.getProductKeyByOption(
                item.productId,
                item.options[0].optionValue[currOptionPos].name,
            )

        } else {

            key = cartDao.getProductKeyById(
                item.productId,
            )
        }

        return@withContext key
    }


    private suspend fun deleteProduct(): JsonObject {
        val obj = JsonObject()
        val key = getKeyOfDeleted()

        obj.addProperty("key", key)

        return obj
    }


    private fun updateCartQuantity(currentQty: Int) {
        showLoadingDialog()
        disposableUpdateCart = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as AddCart
                removeProductFromLocalDB(currentQty)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).addToCartDB(updateProductDetails())
    }

    private fun removeProductFromLocalDB(currentQty: Int) {

        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                withContext(Dispatchers.IO) {
                    if (currentQty > 1) {
                        if (hasOption) {
                            cartDao.updateProductByOption(
                                item.productId,
                                item.options[0].optionValue[currOptionPos].name,
                                binding.edtQty.text.toString()
                            )
                        } else {

                            cartDao.updateProductById(
                                item.productId,
                                binding.edtQty.text.toString()
                            )
                        }
                    } else {
                        if (hasOption) {
                            cartDao.deleteByOption(
                                item.productId,
                                item.options[0].optionValue[currOptionPos].name,
                            )
                        } else {

                            cartDao.deleteById(item.productId)
                        }
                    }
                }

                if (currentQty > 1) {
                    AppDataManager.instance.decrementCartCount()
                    val count = currentQty - 1
                    binding.edtQty.setText(count.toString())
                } else {
                    AppDataManager.instance.clearCartCount()
                    binding.llAdd.visibility = View.GONE
                    binding.btnAdd.visibility = View.VISIBLE
                }

                invalidateOptionsMenu()
            }
        }
    }

    private fun updateProductDetails(): JsonObject {

        if (item.options.isEmpty()) {
            val obj = JsonObject()
            obj.addProperty("product_id", item.productId)
            obj.addProperty("quantity", "-1")
            return obj
        } else {

            if (item.options[0].optionValue.isEmpty()) {
                val obj = JsonObject()
                obj.addProperty("product_id", item.productId)
                obj.addProperty("quantity", "-1")
                return obj
            } else {
                val op_id: String = item.options[0].productOptionId
                val p_op_id: String =
                    item.options[0].optionValue[currOptionPos].productOptionValueId
                val obj = JsonObject()
                val option = JsonObject()

                option.addProperty(op_id, p_op_id)
                obj.add("option", option)
                obj.addProperty("product_id", item.productId)
                obj.addProperty("quantity", "-1")
                return obj
            }
        }
    }

    private fun getExtrasData() {
        productId = intent.getStringExtra("product_id")!!

        callProductDetails()
        if (AppDataManager.instance.isLoggedIn) {
            callIsItemFavourite(productId)
        }
    }

    private fun callIsItemFavourite(productId: String) {
        showLoadingDialog()
        disposableIsWishListed = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ProductModelWishlist
                if (model.data.isNotEmpty()) {
                    checkIfWishlisted(model.data, productId)
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).viewWishlist()
    }

    private fun checkIfWishlisted(wishlist: List<ProductModelWishlist.Data>, product_id: String) {
        for (i in wishlist.indices) {
            if (wishlist[i].productId.equals(product_id)) {
                isWishlisted(true)
            }
        }
    }

    private fun isWishlisted(bool: Boolean) {
        if (bool) {
            binding.favIconFill.visibility = View.VISIBLE
            binding.favIconEmpty.visibility = View.GONE
        } else {
            binding.favIconFill.visibility = View.GONE
            binding.favIconEmpty.visibility = View.VISIBLE
        }
    }

    private fun onClickAdd() {
        var stock: Int

        if (item.options.isEmpty() || item.options[0].optionValue.isEmpty()) {
            stock = item.quantity.toInt()
        } else {
            if (item.options[0].optionValue.isEmpty()) {
                stock = item.quantity.toInt()
            } else {
                stock =
                    item.options[currOptionPosForSize].optionValue[currOptionValPosForSize].quantity.toInt()
            }
        }

        if (binding.edtQty.text.toString().toInt() < stock && binding.edtQty.text.toString()
                .toInt() < item.quantity.toInt()
        ) {
            if (binding.edtQty.text.toString().equals("", ignoreCase = true))
                binding.edtQty.setText("0")
            binding.edtQty.setText((binding.edtQty.text.toString().toInt() + 1).toString() + "")
            addToCartDb()
        } else {
            if (stock > item.quantity.toInt())
                stock = item.quantity.toInt()
            binding.edtQty.setText(stock.toString())
            Toast.makeText(
                this@DetailsActivity,
                getString(R.string.we_dont_have) + item.name + getString(R.string.as_requested),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addToCartDb() {
        showLoadingDialog()
        disposableAddToCart = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as AddCart
                AppDataManager.instance.incrementCartCount()
                addProductToLocalDB(model.data?.product?.key)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).addToCartDB(getSelectedProductDetails())
    }

    private fun addProductToLocalDB(key: String?) {

        lateinit var product: CartData

        if (!hasOption) {
            product = CartData(
                item.productId,
                binding.edtQty.text.toString(),
                item.model,
                false,
                key!!,
                item.quantity
            )
        } else {

            product = CartData(
                item.productId,
                binding.edtQty.text.toString(),
                item.options[0].optionValue[currOptionPos].name,
                false,
                key!!,
                item.quantity
            )

        }

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                var count: String?
                withContext(Dispatchers.IO) {

                    if (!hasOption) {
                        count = cartDao.getProductCountById(item.productId)
                    } else {

                        count = cartDao.getProductCountByOption(
                            item.productId,
                            item.options[0].optionValue[currOptionPos].name
                        )

                    }
                }

                if (count != null && count != "0") {
                    withContext(Dispatchers.IO) {
                        if (hasOption) {

                           Log.i("Curr",binding.edtQty.text.toString())
                            cartDao.updateProductByOption(
                                item.productId,
                                item.options[0].optionValue[currOptionPos].name,
                                binding.edtQty.text.toString()
                            )
                        } else {

                            cartDao.updateProductById(
                                item.productId,
                                binding.edtQty.text.toString()
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.IO) {
                        cartDao.addProductToCart(product)
                    }
                }

            }

            invalidateOptionsMenu()
        }

    }

    private fun getSelectedProductDetails(): JsonObject {

        if (item.options.isEmpty()) {
            val obj = JsonObject()
            obj.addProperty("product_id", item.productId)
            obj.addProperty("quantity", "1")
            return obj
        } else {
            if (item.options[0].optionValue.isEmpty()) {
                val obj = JsonObject()
                obj.addProperty("product_id", item.productId)
                obj.addProperty("quantity", "1")
                return obj
            } else {
                val op_id: String = item.options[0].productOptionId
                val p_op_id: String =
                    item.options[0].optionValue[currOptionPos].productOptionValueId
                val obj = JsonObject()
                val option = JsonObject()

                option.addProperty(op_id, p_op_id)
                obj.add("option", option)
                obj.addProperty("product_id", item.productId)
                obj.addProperty("quantity", "1")
                return obj
            }
        }
    }

    private fun callProductDetails() {

        showLoadingDialog()
        disposableProductDetails = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as DetailsModel
                if (model.success == 1) {
                    handleProductDetails(model)
                } else {
                    getToast().show("Product not found.")
                    finish()
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show("Product not found.")
                finish()
            }

        }, this).getProductDetails(productId)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun handleProductDetails(model: DetailsModel) {
        isApisCalled = true

        item = model.data!!
        setToolbar(item.name.replace("&amp;", "&"))

        optionsData = item.options

        binding.tvProductName.text = item.name.replace("&amp;", "&")

        binding.tvQuantity.text = getQuantityName(item, currOptionPos)

        if (item.description != "") {
            val description: String = item.description
            val encodedHtml = Base64.encodeToString(description.toByteArray(), Base64.NO_PADDING)
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.loadData(encodedHtml, "text/html", "base64")
        }

        if (item.related.isNotEmpty())
            setRelatedProdAdapter(item.related)

        if (item.reward != null) {

           binding.llRewards.visibility = View.VISIBLE
            binding.txtReward.text =
                getString(R.string.earn_upto) + " " + item.reward + " " + getString(R.string.reward_for_this_product)
        }


        //// IMAGES ADAPTER

        if (item.originalImages.isNotEmpty()) {
            imageAdapter = ProductImagesAdapter(this@DetailsActivity, item.originalImages)
            vp_product_images.adapter = imageAdapter

        } else {
            if (item.original_image != null) {
                val list = mutableListOf<String>()
                list.add(0, item.original_image)
                val og_list = mutableListOf<String>()
                og_list.add(0, item.original_image)
                imageAdapter = ProductImagesAdapter(this@DetailsActivity, og_list)
                vp_product_images.adapter = imageAdapter
            }
        }

        // PRICE SETTINGS

        handleOptions(0, 0)

        // OPTIONS HANDLING


        if (optionsData.isNotEmpty()) {

            if (optionsData[0].optionValue.isEmpty()) {
                hasOption = false
            } else {
                hasOption = true
                binding.tvQuantity.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.arrow_down_black,
                    0
                )
            }

        } else {
            hasOption = false
        }





        binding.imgMoveToLeft.setOnClickListener {
            binding.vpProductImages.currentItem = getViewPagerPos(true)
        }

        binding.imgMoveToRight.setOnClickListener {
            binding.vpProductImages.currentItem = getViewPagerPos(false)
        }

        binding.imgShare.setOnClickListener {
            shareProduct()
        }


        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                callDBCount()
            }
        }

    }

    private fun getQuantityName(item: DetailsModel.Data, position: Int): String? {
        var qty: String? = null
        if (item.options.isNotEmpty()) {
            if (item.options[0].optionValue.isNotEmpty()) {
                qty = item.options[0].optionValue[position].name
            } else {
                qty = item.weight + " " + item.weightClass
            }
        } else {
            qty = item.weight + " " + item.weightClass
        }

        return qty
    }

    private fun createColorOptions(option: DetailsModel.Data.Option, optionPos: Int) {
        val colors: ArrayList<DetailsModel.Data.Option.OptionValue> = ArrayList()
        currentOption = option

        if (currentOption?.optionValue?.isNotEmpty()!!) {
            for (i in currentOption?.optionValue?.indices!!) {
                colors.add(currentOption?.optionValue!![i])
            }
        }

        binding.llColorOption.visibility = View.VISIBLE

        colorsAdapter = ColorOptionsAdapter(this, colors, optionPos)

        binding.rvColors.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvColors.adapter = colorsAdapter
        binding.rvColors.isNestedScrollingEnabled = false


    }

    private suspend fun callDBCount() {

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                var count: String?
                withContext(Dispatchers.IO) {
                    if (hasOption) {
                        count = cartDao.getProductCountByOption(
                            item.productId,
                            item.options[0].optionValue[currOptionPos].name,
                        )
                    } else {

                        count = cartDao.getProductCountById(item.productId)
                    }
                }

                if (count != null && count != "0") {
                    binding.llAdd.visibility = View.VISIBLE
                    binding.btnAdd.visibility = View.GONE
                    binding.edtQty.setText(count.toString())
                    Log.i("TT", "FDF" + count.toString())
                } else {
                    Log.i("DSS", "SG")
                    binding.btnAdd.visibility = View.VISIBLE
                    binding.llAdd.visibility = View.GONE
                }
            }
        }
    }


    private fun setRelatedProdAdapter(related: List<DetailsModel.Data.Related>) {

        binding.llRelatedProducts.visibility = View.VISIBLE

        val adapter = RelatedProductsAdapter(this, related as ArrayList<DetailsModel.Data.Related>)

        binding.rvRelated.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        binding.rvRelated.adapter = adapter
        binding.rvRelated.isNestedScrollingEnabled = false

    }


    override suspend fun getCartItemCount(): Int {

        return cartDao.getTotalProductCount()
    }


    private fun shareProduct() {

        // Get access to the URI for the bitmap
        val bmpUri = getLocalBitmapUri(imageAdapter?.getCurrentImage()!!)

        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            val text = """Checkout ${binding.tvProductName.text.toString()} in Alis app; 
            for knowing more, visit https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"""

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, text)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.type = "image/*"
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } else {
            // ...sharing failed, handle error
        }

    }

    private fun getViewPagerPos(isPrev: Boolean): Int {

        if (isPrev) {

            val pos = binding.vpProductImages.currentItem
            if (pos == 0)
                return pos
            else
                return pos - 1
        } else {

            val pos = binding.vpProductImages.currentItem
            if (pos == binding.vpProductImages.adapter?.count)
                return pos
            else
                return pos + 1
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    private fun getLocalBitmapUri(imageView: ImageView): Uri? {
        // Extract Bitmap from ImageView drawable
        val drawable = imageView.drawable
        var bmp: Bitmap? = null
        bmp = if (drawable is BitmapDrawable) {
            (imageView.drawable as BitmapDrawable).bitmap
        } else {
            return null
        }
        // Store image to default external storage directory
        var bmpUri: Uri? = null
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    applicationContext,
                    BuildConfig.APPLICATION_ID.toString() + ".provider",
                    file
                )
            } else {
                Uri.fromFile(file)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onColorSelected(optionPos: Int, optionValPos: Int) {
        currOptionPosForColor = optionPos
        currOptionValPosForColor = optionValPos


        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                callDBCount()
            }
        }
    }

    private fun handleOptions(optionPos: Int, optionValPos: Int) {

        val total_int: Int
        val mrp_int: Int
        if (item.options.isNotEmpty() && item.options[0].optionValue.isNotEmpty()) {

            val optionDetails: DetailsModel.Data.Option.OptionValue =
                item.options[optionPos].optionValue[optionValPos]


            //////////// option price logic
            val base_price: Double
            var base_special = 0.0
            var option_base = 0.0
            var option_special = 0.0
            var has_base_special = false
            var total: String? = null
            var mrp: String? = null

            // Product Base Original Price
            base_price = item.price.toDouble()

            if (item.special != null && !item.special
                    .equals("false")
            ) {
                has_base_special = true
                base_special = item.special!!.toDouble()
            }
            binding.tvOriginalPrice.paintFlags =
                binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val option_base_val: String = optionDetails.price
            val option_special_val: String? = optionDetails.specialPrice
            if (option_special_val != null && option_special_val != "false") {
                binding.tvOriginalPrice.visibility = View.VISIBLE
                mrp = (base_price + option_base_val!!.toDouble()).toString()
                option_special = option_special_val.toDouble()
                total = if (!has_base_special) {
                    (base_price + option_special).toString()
                } else {
                    (base_special + option_special).toString()
                }
                mrp_int = mrp.toDouble().toInt()
                total_int = total.toDouble().toInt()
                binding.tvOriginalPrice.setText(Constants.CURRENCY + mrp_int)
                binding.tvProductPrice.setText(Constants.CURRENCY + total_int)
                if (mrp == total) {
                    binding.tvOriginalPrice.visibility = View.GONE
                }
            } else {
//                    binding.tvOriginalPrice.setVisibility(GONE);
                if (option_base_val != null && option_base_val != "false") {
                    option_base = option_base_val.toDouble()
                    mrp = (base_price + option_base_val.toDouble()).toString()
                    mrp_int = mrp.toDouble().toInt()
                    if (!has_base_special) {
                        total = (base_price + option_base).toString()
                        binding.tvOriginalPrice.visibility = View.GONE
                    } else {
                        total = (base_special + option_base).toString()
                        binding.tvOriginalPrice.visibility = View.VISIBLE
                        binding.tvOriginalPrice.setText(Constants.CURRENCY + mrp_int)
                    }
                    total_int = total.toDouble().toInt()
                    binding.tvProductPrice.setText(Constants.CURRENCY + total_int)
                } else {
                    total_int = base_price.toInt()
                    binding.tvProductPrice.setText(Constants.CURRENCY + total_int)
                }
            }

            /////////////////////////
        } else {
            val base_price: String
            val base_special: String
            Log.v("Special:", "Special " + item.special)
            if (item.special != null && !item.special
                    .equals("false")
            ) {
                base_special = item.special!!
                base_price = java.lang.String.valueOf(item.price)
                binding.tvOriginalPrice.paintFlags =
                    binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvOriginalPrice.visibility = View.VISIBLE
                mrp_int = base_price.toDouble().toInt()
                total_int = base_special.toDouble().toInt()
                binding.tvOriginalPrice.setText(Constants.CURRENCY + mrp_int)
                binding.tvProductPrice.setText(Constants.CURRENCY + total_int)
            } else {
                base_price = java.lang.String.valueOf(item.price)
                total_int = base_price.toDouble().toInt()
                binding.tvProductPrice.setText(Constants.CURRENCY + total_int)
                binding.tvOriginalPrice.visibility = View.GONE
            }
        }
    }

    override fun onOptionSelected(pos: Int) {
        currOptionPos = pos

        handleOptions(0, currOptionPos)

        binding.tvQuantity.text = item.options[0].optionValue[currOptionPos].name

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                callDBCount()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableProductDetails)
        disposeApiCall(disposableAddToCart)
        disposeApiCall(disposableUpdateCart)
        disposeApiCall(disposableRemoveCart)
        disposeApiCall(disposableIsWishListed)
        disposeApiCall(disposableAddWishList)
        disposeApiCall(disposableRemoveWishList)
    }

}