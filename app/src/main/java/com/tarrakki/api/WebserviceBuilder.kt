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
    fun getFundDetails(@Path("id") id: String, @Query("data") data: String): Observable<ApiResponse>

    @GET("cart/")
    fun getCartItem(): Observable<ApiResponse>

    @DELETE("cart/add_to_cart/{id}/")
    fun deleteCartItem(@Path("id") id: String): Observable<ApiResponse>

    @FormUrlEncoded
    @PUT("cart/add_to_cart/{id}/")
    fun updateCartItem(@Path("id") id: String,
                       @Field("data") data: String
    ): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/forgot-password/")
    fun forgotPassword(@Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("cart/add_to_cart/")
    fun addtocart(@Field("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @PUT("tarrakki/zyaada/add_to_cart_zyaada/{tarrakkiZyaadaId}/")
    fun addToCartTarrakkiZyaada(@Path("tarrakkiZyaadaId") id: String, @Field("data") data: String): Observable<ApiResponse>

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
    fun completeRegistration(@Part("data") data: RequestBody,
                             @Part file: MultipartBody.Part,
                             @Part dobCertificate: MultipartBody.Part? = null): Observable<ApiResponse>

    @Multipart
    @POST("ekyc/remaining-fields/{userId}/")
    fun saveRemainingData(
            @Path("userId") userId: String?,
            @Part("data") data: RequestBody,
            @Part file: MultipartBody.Part? = null,
            @Part dobCertificate: MultipartBody.Part? = null): Observable<ApiResponse>

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

    @GET("portfolio/{userId}/")
    fun getUserPortfolio(@Path("userId") userId: String?): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("transactions/redeem/")
    fun redeemPortfolio(@Field("data") data: String): Observable<ApiResponse>

    @POST("transactions/stop/{transactionId}")
    fun stopPortfolio(@Path("transactionId") transactionId: Int): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("transactions/delete-transaction/{userId}")
    fun deleteUnpaidTransactions(@Path("userId") userId: String?, @Field("data") data: String): Observable<ApiResponse>

    @GET("profile/")
    fun getUserProfile(): Observable<ApiResponse>

    @FormUrlEncoded
    @PUT("profile/{user_id}")
    fun updateProfile(@Path("user_id") userId: String?,
                      @Field("data") data: String): Observable<ApiResponse>

    @Multipart
    @PUT("profile/{user_id}")
    fun updateProfile(@Path("user_id") userId: String?,
                      @Part image: MultipartBody.Part?): Observable<ApiResponse>

    @GET("users/social_auth/")
    fun socialLogin(@Query("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("users/social_auth/")
    fun socialSignUp(@Field("data") data: String): Observable<ApiResponse>

    @POST("users/logout/{user_id}")
    fun logout(@Path("user_id") userId: String?): Observable<ApiResponse>

    @GET("blogs/")
    fun getBlogs(@Query("data") data: String): Observable<ApiResponse>

    @GET("banks/get-default-bank/{user_id}")
    fun getDefaultBank(@Path("user_id") userId: String?): Observable<ApiResponse>

    @GET("tarrakki/zyaada/return-funds/")
    fun getTarrakkiZyaada(@Query("data") data: String): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("tarrakki/zyaada/apply-debit-card/{user_id}")
    fun applyForDebitCart(@Path("user_id") userId: String?, @Field("data") data: String): Observable<ApiResponse>

    @GET("tarrakki/zyaada/get-scheme-details/{user_id}")
    fun getSchemeDetails(@Path("user_id") userId: String?, @Query("data") data: String): Observable<ApiResponse>

    @GET("tarrakki/zyaada/get-folio-list/{user_id}")
    fun getFolioList(@Path("user_id") userId: String?): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("tarrakki/zyaada/redemption/{user_id}")
    fun instaRedeem(@Path("user_id") userId: String?, @Field("data") data: String): Observable<ApiResponse>

    @GET("configurations/get-version-details/android/")
    fun checkAppUpdate(): Observable<ApiResponse>

    @GET("configurations/get-maintenance-details/")
    fun getMaintenanceDetails(): Observable<ApiResponse>

    @GET("transactions/my-sip/{userId}/")
    fun getMySip(@Path("userId") userId: String?, @Query("data") data: String): Observable<ApiResponse>

    @Multipart
    @PUT("banks/user-banks/update/{bank_id}/")
    fun updateUserBankDetails(@Part("account_number") account_number: RequestBody?,
                              @Part("ifsc_code") ifsc_code: RequestBody?,
                              @Part("account_type") account_type: RequestBody?,
                              @Part("user_id") user_id: RequestBody?,
                              @Part("bank_id") bank_id: RequestBody?,
                              @Path("bank_id") bank_user_id: String?,
                              @Part verification_document: MultipartBody.Part): Observable<ApiResponse>

    @FormUrlEncoded
    @PUT("banks/user-banks/update/{bank_id}/")
    fun updateUserBank(@Field("account_number") account_number: String?,
                       @Field("ifsc_code") ifsc_code: String?,
                       @Field("account_type") account_type: String?,
                       @Field("user_id") user_id: String?,
                       @Field("bank_id") bank_id: String?,
                       @Path("bank_id") user_bank_id: String?): Observable<ApiResponse>


    @GET("banks/user-mandate/details/{mandateId}/")
    fun getISIPMandateData(@Path("mandateId") mandateId: String?): Observable<ApiResponse>

    @GET("ekyc/create-investor/{userId}/")
    fun apiApplyNewKYC(@Path("userId") userId: String?): Observable<ApiResponse>

    @GET("ekyc/fetch-kyc-flags/{userId}/")
    fun getKYCStatus(@Path("userId") userId: String?): Observable<ApiResponse>

    @GET("risk-assessment/get-questionaire/{userId}/")
    fun getRiskAssessmentQuestions(@Path("userId") userId: String?): Observable<ApiResponse>

    /**
     * ApiNames to differentiate APIs
     */
    enum class ApiNames {
        onLogin, onSignUp, getGoals, calculatePMT, getHomeData, getGoalById, getOTP, verifyOTP, addGoal, getFunds,
        getFundDetails, addGoalToCart, getCartItem, deleteCartItem, updateCartItem, forgotPassword,
        addtocart, forgotPasswordVerifyOTP, resetPassword, investmentRecommendation, getAllBanks, addBankDetails,
        deleteSavedGoals, getEKYCPage, complateRegistration, uploadNACHMandate, KYCData, transactions,
        mandateConfirmOrder, ConfirmOrderResponse, PaymentResponse, UserPortfolio, logout, updateUserBankDetails
    }
}