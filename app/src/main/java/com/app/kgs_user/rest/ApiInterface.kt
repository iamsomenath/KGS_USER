package com.app.kgs_user.rest

import com.app.kgs_user.model.ReviewResponse
import com.app.kgs_user.model.ResetResponse
import com.app.kgs_user.model.UpdateProfileResponse
import com.app.kgs_user.model.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiInterface {

    @get:GET("api/service.php?action=household_essentials")
    val appVersion: Call<ResponseBody>

    @GET("api/service.php?action=kmcharges")
    fun aboutPage(): Observable<ResponseBody>

    @GET("about")
    fun about_page(): Call<ResponseBody>

    @GET("contactus")
    fun contactus(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("kitchen_list")
    fun getKitchen(@Field("lat") lat: String, @Field("lng") lng: String): Observable<KitchenRestResponse>

   /* @FormUrlEncoded
    @POST("todays_menu")
    fun todaysMenu(@Field("user_id") user_id: String): Observable<MenuResponse>*/

    @FormUrlEncoded
    @POST("todays_menu")
    fun todaysMenu(@Field("user_id") user_id: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("wallet_balance")
    fun getWallet(@Field("user_id") user_id: String): Observable<WalletResponse>

    @FormUrlEncoded
    @POST("cut_from_wallet")
    fun cutFomWallet(@Field("user_id") user_id: String, @Field("amount") amount: String): Observable<CutWalletResponse>

    @FormUrlEncoded
    @POST("make_order")
    fun placeOrder(@Field("user_id") user_id: String, @Field("kitchen_id") kitchen_id: String,
                   @Field("menu_id") menu_id: String, @Field("payment_status") payment_status : String,
                   @Field("payment_via") payment_via : String, @Field("extra_note") extra_note: String,
                   @Field("pincode") pincode: String, @Field("landmark") landmark: String,
                   @Field("address") address: String, @Field("quantity") quantity: String,
                   @Field("price") price: String): Observable<OrderResponse>

    @FormUrlEncoded
    @POST("todays_order")
    fun todaysOrder(@Field("user_id") user_id: String): Observable<PostOrderResponse>

    @FormUrlEncoded
    @POST("update_user_profile")
    fun update_user_profile(
        @Field("user_id") user_id: String, @Field("name") name: String,
        @Field("email") email: String, @Field("contact") contact: String
    ): Observable<UpdateProfileResponse>

    @FormUrlEncoded
    @POST("change_password")
    fun change_password(@Field("user_id") user_id: String, @Field("password") password: String,
                        @Field("oldpassword") oldpassword: String): Observable<ResetResponse>

    @FormUrlEncoded
    @POST("submit_review")
    fun submit_review(@Field("user_id") user_id: String, @Field("kitchen_id") kitchen_id: String,
                      @Field("order_id") order_id: String, @Field("rating") rating: String,
                      @Field("comment") comment: String): Observable<ReviewResponse>

}