package com.ecommerce.shopmitt.network

import android.content.Context
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class RestHelper(mHelper: RestResponseHandler?, mContext: Context?) {

    private var helper: RestResponseHandler? = null
    private var context: Context? = null

    init {
        this.context = mContext
        this.helper = mHelper
    }


    fun checkForceUpdate(versionCode: Int, platform: String): Disposable? {
        val update = RestServiceGenerator().getService()!!.checkForceUpdate(versionCode,platform)
        return update.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun register(params: JsonObject): Disposable? {
        val register = RestServiceGenerator().getService()!!.register(params)
        return register.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getCategories(): Disposable? {
        val categories = RestServiceGenerator().getService()!!.getCategories()
        return categories.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getToken(): Disposable? {
        val token = RestServiceGenerator().getService()!!.getToken()
        return token.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getHomeTitles(): Disposable? {
        val titles = RestServiceGenerator().getService()!!.getHomeTitles()
        return titles.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getBannerPosOne(): Disposable? {
        val posOne = RestServiceGenerator().getService()!!.getBannerPosOne()
        return posOne.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getBannerPosTwo(): Disposable? {
        val posTwo = RestServiceGenerator().getService()!!.getBannerPosTwo()
        return posTwo.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getBannerPosThree(): Disposable? {
        val posThree = RestServiceGenerator().getService()!!.getBannerPosThree()
        return posThree.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getBannerPosFour(): Disposable? {
        val posFour = RestServiceGenerator().getService()!!.getBannerPosFour()
        return posFour.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getBannerPosFive(): Disposable? {
        val posFive = RestServiceGenerator().getService()!!.getBannerPosFive()
        return posFive.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getFeaturedBanners(): Disposable? {
        val featured = RestServiceGenerator().getService()!!.getFeaturedBanners()
        return featured.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getFeaturedProducts(id: String): Disposable? {
        val featured = RestServiceGenerator().getService()!!.getFeaturedProducts(id)
        return featured.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getTopSection(): Disposable? {
        val topSection = RestServiceGenerator().getService()!!.getTopSection()
        return topSection.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getMiddleSection(): Disposable? {
        val middleSection = RestServiceGenerator().getService()!!.getMiddleSection()
        return middleSection.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getBottomSection(): Disposable? {
        val bottomSection = RestServiceGenerator().getService()!!.getBottomSection()
        return bottomSection.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun generateOTP(params: JsonObject): Disposable? {
        val otp = RestServiceGenerator().getService()!!.generateOTP(params)
        return otp.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun resendOTP(params: JsonObject): Disposable? {
        val otp = RestServiceGenerator().getService()!!.resendOTP(params)
        return otp.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun validateOTP(params: JsonObject): Disposable? {
        val otp = RestServiceGenerator().getService()!!.validateOTP(params)
        return otp.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun validateLoginOTP(params: JsonObject): Disposable? {
        val otp = RestServiceGenerator().getService()!!.validateLoginOTP(params)
        return otp.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun loginOTPGen(params: JsonObject): Disposable? {
        val otp = RestServiceGenerator().getService()!!.loginOTPGen(params)
        return otp.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun logout(params: JsonObject): Disposable? {
        val logout = RestServiceGenerator().getService()!!.logout(params)
        return logout.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getCategoryById(categoryId: String): Disposable? {
        val logout = RestServiceGenerator().getService()!!.getCategoryById(categoryId)
        return logout.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getProductsByCategory(categoryId: String): Disposable? {
        val products = RestServiceGenerator().getService()!!.getProductsByCategory(categoryId)
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getProductsBySearch(keyword: String): Disposable? {
        val products = RestServiceGenerator().getService()!!.getProductsBySearch(keyword)
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getProductDetails(id: String): Disposable? {
        val products = RestServiceGenerator().getService()!!.getProductDetails(id)
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun addToCartDB(body: JsonObject): Disposable? {
        val products = RestServiceGenerator().getService()!!.addToCartDB(body)
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun updateCartDB(body: JsonObject): Disposable? {
        val products = RestServiceGenerator().getService()!!.updateCartDB(body)
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun deleteCartDB(body: JsonObject): Disposable? {
        val products = RestServiceGenerator().getService()!!.deleteCartDB(body)
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun dropCart(): Disposable? {
        val products = RestServiceGenerator().getService()!!.dropCart()
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun viewCart(): Disposable? {
        val products = RestServiceGenerator().getService()!!.viewCartDB()
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun viewWishlist(): Disposable? {
        val products = RestServiceGenerator().getService()!!.viewWishlist()
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun addWishlist(productId: String): Disposable? {
        val products = RestServiceGenerator().getService()!!.addWishlist(productId)
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun removeWishlist(productId: String): Disposable? {
        val products = RestServiceGenerator().getService()!!.removeWishlist(productId)
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getWishListedProducts(): Disposable? {
        val products = RestServiceGenerator().getService()!!.getWishListedProducts()
        return products.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getPaymentAddress(): Disposable? {
        val address = RestServiceGenerator().getService()!!.getPaymentAddress()
        return address.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getShippingAddress(): Disposable? {
        val address = RestServiceGenerator().getService()!!.getShippingAddress()
        return address.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun setShippingAddress(body: JsonObject): Disposable? {
        val address = RestServiceGenerator().getService()!!.setShippingAddress(body)
        return address.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun setPaymentAddress(body: JsonObject): Disposable? {
        val address = RestServiceGenerator().getService()!!.setPaymentAddress(body)
        return address.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun addShippingAddress(body: JsonObject): Disposable? {
        val address = RestServiceGenerator().getService()!!.addShippingAddress(body)
        return address.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }


    fun getShippingMethods(queries: Map<String,String>): Disposable? {
        val shipping = RestServiceGenerator().getService()!!.getShippingMethods(queries)
        return shipping.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun setShippingMethod(body: JsonObject): Disposable? {
        val shipping = RestServiceGenerator().getService()!!.setShippingMethod(body)
        return shipping.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getPaymentMethods(): Disposable? {
        val payment = RestServiceGenerator().getService()!!.getPaymentMethods()
        return payment.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun setPaymentMethod(body: JsonObject): Disposable? {
        val payment = RestServiceGenerator().getService()!!.setPaymentMethod(body)
        return payment.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun confirmOrder(body: JsonObject): Disposable? {
        val order = RestServiceGenerator().getService()!!.confirmOrder(body)
        return order.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun confirmPUT(): Disposable? {
        val order = RestServiceGenerator().getService()!!.confirmPUT()
        return order.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getMyOrders(): Disposable? {
        val order = RestServiceGenerator().getService()!!.getMyOrders()
        return order.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getOrderDetails(orderId: String): Disposable? {
        val order = RestServiceGenerator().getService()!!.getOrderDetails(orderId)
        return order.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun checkPinCode(pinCode: String): Disposable? {
        val pin = RestServiceGenerator().getService()!!.checkPinCode(pinCode)
        return pin.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun deleteAddress(id: String): Disposable? {
        val address = RestServiceGenerator().getService()!!.deleteAddress(id)
        return address.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun updateAddress(id: String,params: JsonObject): Disposable? {
        val address = RestServiceGenerator().getService()!!.updateAddress(id,params)
        return address.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getProfile(): Disposable? {
        val profile = RestServiceGenerator().getService()!!.getProfile()
        return profile.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun updateProfile(params: JsonObject): Disposable? {
        val profile = RestServiceGenerator().getService()!!.updateProfile(params)
        return profile.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun changePassword(params: JsonObject): Disposable? {
        val password = RestServiceGenerator().getService()!!.changePassword(params)
        return password.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getPrivacyPolicy(): Disposable? {
        val web = RestServiceGenerator().getService()!!.getPrivacyPolicy()
        return web.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getTerms(): Disposable? {
        val web = RestServiceGenerator().getService()!!.getTerms()
        return web.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getAboutUs(): Disposable? {
        val web = RestServiceGenerator().getService()!!.getAboutUs()
        return web.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun applyCoupon(params: JsonObject): Disposable? {
        val apply = RestServiceGenerator().getService()!!.applyCoupon(params)
        return apply.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun applyVoucher(params: JsonObject): Disposable? {
        val apply = RestServiceGenerator().getService()!!.applyVoucher(params)
        return apply.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun applyReward(params: JsonObject): Disposable? {
        val apply = RestServiceGenerator().getService()!!.applyReward(params)
        return apply.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun reOrder(id: String): Disposable? {
        val apply = RestServiceGenerator().getService()!!.reOrder(id)
        return apply.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getDeliverySlots(): Disposable? {
        val slots = RestServiceGenerator().getService()!!.getDeliverySlots()
        return slots.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getRazorPayOrderId(amount: String, orderId: String): Disposable? {
        val pay = RestServiceGenerator().getService()!!.getRazorPayOrderId(amount,orderId)
        return pay.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getWalletTransactions(orderId: String): Disposable? {
        val pay = RestServiceGenerator().getService()!!.getWalletTransactions(orderId)
        return pay.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun addWallet(params: JsonObject): Disposable? {
        val pay = RestServiceGenerator().getService()!!.addWallet(params)
        return pay.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun confirmWalletPayment(params: JsonObject): Disposable? {
        val pay = RestServiceGenerator().getService()!!.confirmWalletPayment(params)
        return pay.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun verifyRefCode(params: JsonObject): Disposable? {
        val ref = RestServiceGenerator().getService()!!.verifyRefCode(params)
        return ref.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getReferralCode(): Disposable? {
        val ref = RestServiceGenerator().getService()!!.getReferralCode()
        return ref.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getReferralOffers(): Disposable? {
        val ref = RestServiceGenerator().getService()!!.getReferralOffers()
        return ref.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getStoreDetails(): Disposable? {
        val store = RestServiceGenerator().getService()!!.getStoreDetails()
        return store.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun contactUs(params: JsonObject): Disposable? {
        val contact = RestServiceGenerator().getService()!!.contactUs(params)
        return contact.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getStores(): Disposable? {
        val stores = RestServiceGenerator().getService()!!.getStores()
        return stores.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getDeliveryPlaces(): Disposable? {
        val places = RestServiceGenerator().getService()!!.getDeliveryPlaces()
        return places.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun cancelOrder(params: JsonObject): Disposable? {
        val order = RestServiceGenerator().getService()!!.cancelOrder(params)
        return order.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun changePaymentMethod(orderId: String): Disposable? {
        val order = RestServiceGenerator().getService()!!.changePaymentMethod(orderId)
        return order.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getEditableTimeSlots(date: String): Disposable? {
        val slots = RestServiceGenerator().getService()!!.getEditableTimeSlots(date)
        return slots.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun changeOrderTimeSlot(params: JsonObject): Disposable? {
        val slots = RestServiceGenerator().getService()!!.changeOrderTimeSlot(params)
        return slots.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getCartTotals(): Disposable? {
        val totals = RestServiceGenerator().getService()!!.getCartTotals()
        return totals.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getNotification(query: String, id: String): Disposable? {
        val notif = RestServiceGenerator().getService()!!.getNotification(query,id)
        return notif.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
                { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }

    fun getPlaceAddress(url: String?): Disposable? {
        val getTrips = RestServiceGenerator().getService()!!.getPlaceAddress(url)
        return getTrips!!.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ obj -> obj?.let { RestServiceResponse.instance.handleResults(it, helper) } })
            { throwable: Throwable? -> RestServiceResponse.instance.handleError(throwable, helper, context) }
    }
}