package com.ecommerce.shopmitt.network

import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.models.*
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*


interface RestApiClient {

    @POST("index.php?route=feed/rest_api/ForceUpdate")
    fun checkForceUpdate(@Query("version") version: Int, @Query("platform") platform: String): Observable<GenericModel>

    @GET("index.php?route=feed/rest_api/categories&top=1")
    fun getCategories(): Observable<CategoryModel>

    @POST("index.php?route=feed/rest_api/gettoken&grant_type=client_credentials")
    fun getToken(): Observable<TokenModel>

    @GET("index.php?route=feed/rest_api/mobileHomeTitleBanners")
    fun getHomeTitles(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/mobileBannersPositionOne")
    fun getBannerPosOne(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/mobileBannersPositionTwo")
    fun getBannerPosTwo(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/mobileBannersPositionThree")
    fun getBannerPosThree(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/mobileBannersPositionFour")
    fun getBannerPosFour(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/mobileBannersPositionFive")
    fun getBannerPosFive(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/FeaturedSectionBanners")
    fun getFeaturedBanners(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/featuredSection")
    fun getFeaturedProducts(@Query("id") id: String): Observable<ProductModel>

    @GET("index.php?route=feed/rest_api/HomeTopSection")
    fun getTopSection(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/HomeMiddleSection")
    fun getMiddleSection(): Observable<BannerModel>

    @GET("index.php?route=feed/rest_api/HomeBottomSection")
    fun getBottomSection(): Observable<BannerModel>

    @POST("index.php?route=rest/register/register")
    fun register(@Body params: JsonObject): Observable<RegisterModel>

    @POST("index.php?route=rest/register/generateOtp")
    fun generateOTP(@Body params: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/register/resendOtp")
    fun resendOTP(@Body params: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/register/validateOtp")
    fun validateOTP(@Body params: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/login/customerValidateOtp")
    fun validateLoginOTP(@Body params: JsonObject): Observable<RegisterModel>

    @POST("index.php?route=rest/login/customerLoginOtp")
    fun loginOTPGen(@Body params: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/logout/logout")
    fun logout(@Body params: JsonObject): Observable<BaseModel>

    @GET("index.php?route=feed/rest_api/categories&level=3")
    fun getCategoryById(@Query("id") id: String): Observable<SubCategoryModel>

    @GET("index.php?route=feed/rest_api/products")
    fun getProductsByCategory(@Query("category") category: String): Observable<ProductModel>

    @GET("index.php?route=feed/rest_api/products")
    fun getProductsBySearch(@Query("search") keyword: String): Observable<ProductModel>

    @GET("index.php?route=feed/rest_api/products")
    fun getProductDetails(@Query("id") product_id: String): Observable<DetailsModel>

    @POST("index.php?route=rest/cart/cart")
    fun addToCartDB(@Body json: JsonObject): Observable<AddCart>

    @PUT("index.php?route=rest/cart/cart")
    fun updateCartDB(@Body json: JsonObject): Observable<AddCart>

    @HTTP(method = "DELETE", path = "index.php?route=rest/cart/cart", hasBody = true)
    fun deleteCartDB(@Body json: JsonObject): Observable<AddCart>

    @DELETE("index.php?route=rest/cart/emptycart")
    fun dropCart(): Observable<BaseModel>

    @GET("index.php?route=rest/cart/cart")
    fun viewCartDB(): Observable<ViewCart>

    @GET("index.php?route=rest/wishlist/wishlist")
    fun viewWishlist(): Observable<ProductModelWishlist>

    @POST("index.php?route=rest/wishlist/wishlist")
    fun addWishlist(@Query("id") product_id: String?): Observable<WishListResponse>

    @DELETE("index.php?route=rest/wishlist/wishlist")
    fun removeWishlist(@Query("id") product_id: String): Observable<WishListResponse>

    @GET("index.php?route=rest/wishlist/wishlist")
    fun getWishListedProducts(): Observable<WishListProductsModel>

    @GET("index.php?route=rest/payment_address/paymentaddress")
    fun getPaymentAddress(): Observable<ShippingAddressModel>

    @GET("index.php?route=rest/shipping_address/shippingaddress")
    fun getShippingAddress(): Observable<ShippingAddressModel>

    @POST("index.php?route=rest/payment_address/paymentaddress&existing=1")
    fun setPaymentAddress(@Body existing: JsonObject?): Observable<BaseModel>

    @POST("index.php?route=rest/shipping_address/shippingaddress&existing=1")
    fun setShippingAddress(@Body existing: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/shipping_address/shippingaddress")
    fun addShippingAddress(@Body existing: JsonObject): Observable<BaseModel>

    @GET("index.php?route=rest/shipping_method/shippingmethods")
    fun getShippingMethods(): Observable<ShippingMethodsModel>

    @POST("index.php?route=rest/shipping_method/shippingmethods")
    fun setShippingMethod(@Body json: JsonObject): Observable<BaseModel>

    @GET("index.php?route=rest/payment_method/payments")
    fun getPaymentMethods(): Observable<PaymentMethodsModel>

    @POST("index.php?route=rest/payment_method/payments")
    fun setPaymentMethod(@Body json: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/confirm/confirm")
    fun confirmOrder(@Body jsonObject: JsonObject): Observable<ConfirmOrderModel>

    @PUT("index.php?route=rest/confirm/confirm")
    fun confirmPUT(): Observable<ConfirmOrderModel>

    @GET("index.php?route=rest/order/orders")
    fun getMyOrders(): Observable<OrdersModel>

    @GET("index.php?route=rest/order/orders")
    fun getOrderDetails(@Query("id") id: String?): Observable<OrderDetailsModel>

    @GET("index.php?route=feed/rest_api/checkpincode")
    fun checkPinCode(@Query("postcode") postcode: String): Observable<GenericModel>

    @DELETE("index.php?route=rest/account/address")
    fun deleteAddress(@Query("id") id: String): Observable<BaseModel>

    @PUT("index.php?route=rest/account/address")
    fun updateAddress(@Query("id") id: String, @Body data: JsonObject): Observable<BaseModel>

    @GET("index.php?route=rest/account/account")
    fun getProfile(): Observable<ProfileModel>

    @PUT("index.php?route=rest/account/account")
    fun updateProfile(@Body json: JsonObject): Observable<BaseModel>

    @PUT("index.php?route=rest/account/password")
    fun changePassword(@Body existing: JsonObject): Observable<GenericModel>

    @GET("index.php?route=feed/rest_api/information&id=1&info=1")
    fun getPrivacyPolicy(): Observable<WebViewModel>

    @GET("index.php?route=feed/rest_api/information&id=2&info=1")
    fun getTerms(): Observable<WebViewModel>

    @GET("index.php?route=feed/rest_api/information&id=4")
    fun getAboutUs(): Observable<WebViewModel>

    @POST("index.php?route=rest/cart/coupon")
    fun applyCoupon(@Body params: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/cart/voucher")
    fun applyVoucher(@Body params: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/cart/reward")
    fun applyReward(@Body params: JsonObject): Observable<BaseModel>

    @GET("index.php?route=rest/order/reorder")
    fun reOrder(@Query("id") id: String): Observable<BaseModel>

    @GET("index.php?route=feed/rest_api/timeslot")
    fun getDeliverySlots(): Observable<DeliverySlotModel>

    @GET("index.php?route=rest/confirm/get_curl_handle")
    fun getRazorPayOrderId(@Query("amount") amount: String, @Query("order_id") orderId: String): Observable<RazorPayModel>

    @GET("index.php?route=rest/wallet/wallet")
    fun getWalletTransactions(@Query("customer_id") customer_id: String): Observable<TransactionModel>

    @POST("index.php?route=rest/wallet/wallet")
    fun addWallet(@Body params: JsonObject): Observable<BaseModel>

    @POST("index.php?route=rest/wallet/success")
    fun confirmWalletPayment(@Body params: JsonObject): Observable<BaseModel>

    @POST("index.php?route=feed/rest_api/validateReferral")
    fun verifyRefCode(@Body params: JsonObject): Observable<BaseModel>

    @GET("index.php?route=feed/rest_api/refferral")
    fun getReferralCode(): Observable<ReferralModel>

    @GET("index.php?route=feed/rest_api/refferral_offers")
    fun getReferralOffers(): Observable<ReferralOffersModel>

    @GET("index.php?route=feed/rest_api/stores&id=0")
    fun getStoreDetails(): Observable<StoreDetailModel>

    @POST("index.php?route=rest/contact/send")
    fun contactUs(@Body params: JsonObject): Observable<BaseModel>

    @GET("index.php?route=rest/order/getShopList")
    fun getStores(): Observable<StoresModel>

    @GET("index.php?route=feed/rest_api/getDeliveryarea")
    fun getDeliveryPlaces(): Observable<DeliveryPlacesModel>

    @POST("index.php?route=feed/rest_api/ordercancel")
    fun cancelOrder(@Body params: JsonObject): Observable<BaseModel>

    @GET("index.php?route=rest/order/changePaymentMethod")
    fun changePaymentMethod(@Query("order_id") order_id: String): Observable<BaseModel>

    @GET("index.php?route=feed/rest_api/editTimeslot")
    fun getEditableTimeSlots(@Query("delivery_date") date: String): Observable<EditableTimeSlotModel>

    @POST("index.php?route=rest/order/changeTimeSlot")
    fun changeOrderTimeSlot(@Body params: JsonObject): Observable<BaseModel>

    @GET("index.php?route=rest/cart/cartTotal")
    fun getCartTotals(): Observable<ViewCart>

    @GET("index.php")
    fun getNotification(@Query("route") route: String, @Query("id") notif_id: String): Observable<NotificationModel>

    @GET
    fun getPlaceAddress(@Url url: String?): Observable<GooglePlaceAddressModel?>?

}