package com.tarrakki.api

import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.soapmodel.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


/**
 * Declare all the APIs in this class with specific interface
 * e.g. Profile for Login/Register Apis
 */
interface WebserviceBuilder {

    @FormUrlEncoded
    @POST("api-token-auth/")
    fun onLogin(@Field("auth_data") authData: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/signup/")
    fun onSignUp(@Field("data") data: String): Observable<ApiResponse>

    @GET("goals/")
    fun getGoals(): Observable<ApiResponse>

    @GET("users/verify/email/{id}/")
    fun isEmailVerified(@Path("id") userId: String?): Observable<ApiResponse>

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
                       @Field("lumpsum_amount") lumpsum_amount: String,
                       @Field("day") day: String?,
                       @Field("sip_amount") sip_amount: String
    ): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/forgot-password/")
    fun forgotPassword(@Field("email") fund_id_id: String,
                       @Field("type") lumpsum_amount: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("cart/add_to_cart/")
    fun addtocart(@Field("data") data: String): Observable<ApiResponse>

    /*@Field("fund_id") fundId: Int,
    @Field("sip_amount") sipAmount: String?,
    @Field("lumpsum_amount") lumpsumAmount: String?
    */
    @FormUrlEncoded
    @POST("users/get_otp/")
    fun verifyForgotOTP(@Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/forgot-password/confirm/")
    fun resetPassword(@Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/change-password/")
    fun changePassword(@Field("data") data: String): Observable<ApiResponse>

    @GET("banks/")
    fun getAllBanks(): Observable<ApiResponse>

    @GET("banks/user-banks/{userId}")
    fun getUserBanks(@Path("userId") userId: String?): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("banks/user-banks/")
    fun addBankDetails(@Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("category/recommendations/")
    fun investmentStrageyRecommeded(@Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @PUT("banks/set-default-account/{userId}")
    fun setDefault(@Path("userId") userId: String?, @Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("PLKYC/Home/home/")
    fun eKYC(@Field("url") url: String,
             @Field("session_id") sessionId: String,
             @Field("ekyctype") eKYCType: String,
             @Field("plkyc_type") plkycType: String,
             @Field("kyc_data") KYCdata: String): Observable<String>


    @GET("goals/saved_goals/{id}")
    fun getSavedGoals(@Path("id") id: String?): Observable<ApiResponse>

    @DELETE("goals/saved_goals/{id}")
    fun deleteSavedGoals(@Path("id") id: Int?): Observable<ApiResponse>

    @GET("banks/user-mandate/{userId}")
    fun getUserMandateBanks(@Path("userId") userId: String?): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("banks/user-mandate-add/")
    fun addMandateBank(@Field("data") data: String): Observable<ApiResponse>

    @GET("banks/user-mandate-download/{mandate_id}/")
    fun downloadMandateForm(@Path("mandate_id") bank_mandate_id: String?): Observable<ApiResponse>

    @Multipart
    @PUT("banks/user-mandate-download/{mandate_id}/")
    fun uploadNachMandateForm(@Path("mandate_id") bank_mandate_id: Int?,
                              @Part image: MultipartBody.Part): Observable<ApiResponse>

    @Multipart
    @POST("profile/add/")
    fun completeRegistration(@Part("data") data: RequestBody, @Part file: MultipartBody.Part): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("banks/set-kyc-details/{userId}")
    fun saveKYCdata(@Path("userId") userId: String?, @Field("data") data: String): Observable<ApiResponse>

    @GET("banks/get-kyc-details/{userId}")
    fun gatKYCdata(@Path("userId") userId: String?): Observable<ApiResponse>


    /**CAMPS SOAP apis**/

    @Headers(value = ["Content-Type: application/soap+xml; charset=utf-8"])
    @POST("services_kycenquiry.asmx")
    fun requestPassword(@Body body: com.tarrakki.api.soapmodel.RequestBody): Observable<ResponseBody>

    @Headers(value = ["Content-Type: application/soap+xml; charset=utf-8"])
    @POST("services_kycenquiry.asmx")
    fun getPANeKYCStates(@Body body: VerifyPANDetails): Observable<ResponseKYCStates>

    @Headers(value = ["Content-Type: application/soap+xml; charset=utf-8"])
    @POST("services_kycenquiry.asmx")
    fun getEKYCData(@Body body: RequestEnvelopeDownloadPANDetailsEKYC): Observable<ResponseKYCData>

    /**End CAMPS SOAP apis**/


    @GET("cart/confirm/order/{userId}")
    fun getConfirmOrder(@Path("userId") userId: String?): Observable<ApiResponse>

    @FormUrlEncoded
    @PUT("cart/add_to_cart/{id}/")
    fun updateFirstSIPFlag(@Path("id") id: String, @Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @PUT("cart/confirm/order/{orderId}")
    fun mandateIdConfirmOrder(@Path("orderId") orderId: Int?,
                              @Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("cart/confirm/order/")
    fun checkoutOrder(@Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("cart/checkout/")
    fun paymentOrder(@Field("data") data: String): Observable<ApiResponse>

    @GET("transactions/success/")
    fun transactionStatus(@Query("data") data: String): Observable<ApiResponse>

    @GET("transactions/{userId}/")
    fun getTransactions(@Path("userId") userId: String?, @Query("data") data: String): Observable<ApiResponse>

    @GET("transactions/portfolio/{userId}")
    fun getUserPortfolio(@Path("userId") userId: String?): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("transactions/redeem/")
    fun redeemPortfolio(@Field("data") data: String): Observable<ApiResponse>

    @POST("transactions/stop/{transactionId}")
    fun stopPortfolio(@Path("transactionId") transactionId: Int): Observable<ApiResponse>

    /**
     * ApiNames to differentiate APIs
     */
    enum class ApiNames {
        onLogin, onSignUp, getGoals, calculatePMT, getHomeData, getGoalById, getOTP, verifyOTP, addGoal, getFunds,
        getFundDetails, addGoalToCart, getCartItem, deleteCartItem, updateCartItem, forgotPassword,
        addtocart, forgotPasswordVerifyOTP, resetPassword, investmentRecommendation, getAllBanks, addBankDetails,
        deleteSavedGoals, getEKYCPage, complateRegistration, uploadNACHMandate, KYCData, transactions,
        mandateConfirmOrder, ConfirmOrderResponse, PaymentResponse,UserPortfolio
    }
}