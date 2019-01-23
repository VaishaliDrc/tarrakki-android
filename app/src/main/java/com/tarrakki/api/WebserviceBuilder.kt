package com.tarrakki.api

import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.LoginResponse
import com.tarrakki.api.model.SignUpresponse
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Declare all the APIs in this class with specific interface
 * e.g. Profile for Login/Register Apis
 */
interface WebserviceBuilder {

    @FormUrlEncoded
    @POST("api-token-auth/")
    fun onLogin(@Field("auth_data") authData: String): Observable<LoginResponse>

    @FormUrlEncoded
    @POST("users/signup/")
    fun onSignUp(@Field("data") data: String): Observable<SignUpresponse>

    @GET("goals/")
    fun getGoals(): Observable<ApiResponse>

    @GET("goals/{id}/")
    fun getGoalsById(@Path("id") id: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("goals/cal_pmt/")
    fun addGoal(@Field("data") data: String): Observable<ApiResponse>

    @PUT("goals/add_goal_to_cart/{id}/")
    fun addGoalToCart(@Path("id") id: String): Observable<ApiResponse>

    @GET("goals/cal_pmt/")
    fun calculatePMT(@Query("data") data: String): Observable<ApiResponse>

    @GET("category/homepage/")
    fun getHomeData(): Observable<ApiResponse>

    @GET("users/get_otp/")
    fun getOTP(@Query("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/get_otp/")
    fun verifyOTP(@Field("data") data: String): Observable<ApiResponse>

    @GET("fund-details/get_fund/")
    fun getFunds(@Query("data") data: String): Observable<ApiResponse>

    @GET("fund-details/{id}/")
    fun getFundDetails(@Path("id") id: String): Observable<ApiResponse>

    @GET("cart/")
    fun getCartItem(): Observable<ApiResponse>

    @DELETE("cart/add_to_cart/{id}/")
    fun deleteCartItem(@Path("id") id: String): Observable<ApiResponse>

    @FormUrlEncoded
    @PUT("cart/add_to_cart/{id}/")
    fun updateCartItem(@Path("id") id: String,
                       @Field("fund_id_id") fund_id_id: String,
                       @Field("lumpsum_amount") lumpsum_amount: Double,
            /*@Field("start_date") start_date: String,*/
                       @Field("sip_amount") sip_amount: Double
    ): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/forgot-password/")
    fun forgotPassword(@Field("email") email: String,
                       @Field("type") type: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("cart/add_to_cart/")
    fun addtocart(@Field("fund_id") fundId: Int,
                  @Field("sip_amount") sipAmount: String,
                  @Field("lumpsum_amount") lumpsumAmount: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/get_otp/")
    fun verifyForgotOTP(@Field("otp_id") otp_id: String?,
                        @Field("otp") otp: String?): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/forgot-password/confirm/")
    fun resetPassword(@Field("token") token: String?,
                        @Field("password") password: String?): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/change-password/")
    fun changePassword(@Field("current_password") token: String?,
                      @Field("new_password") newPassword: String?,
                      @Field("confirm_password") confirmPassword: String?): Observable<ApiResponse>

    @GET("banks/")
    fun getAllBanks(): Observable<ApiResponse>

    @GET("banks/user-banks/1")
    fun getUserBanks(): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("banks/user-banks/")
    fun addBankDetails(@Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("category/recommendations/")
    fun investmentStragey(@Field("third_level_category_id") thirdLevelCategoryId: Int,
                          @Field("years") years: String,
                          @Field("lumpsum_amount") lumpsum_amount: Int,
                          @Field("add_to_cart") addToCart: Int,
                          @Field("sip_amount") sip_amount: Int
    ): Observable<ApiResponse>

    /**
     * ApiNames to differentiate APIs
     */
    enum class ApiNames {
        onLogin, onSignUp, getGoals, calculatePMT, getHomeData, getGoalById, getOTP, verifyOTP, addGoal, getFunds,
        getFundDetails, addGoalToCart, getCartItem, deleteCartItem, updateCartItem, forgotPassword,
        addtocart, forgotPasswordVerifyOTP, resetPassword, investmentRecommendation,getAllBanks, addBankDetails
    }
}