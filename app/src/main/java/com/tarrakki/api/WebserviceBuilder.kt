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

    @GET("goals/cal_pmt/")
    fun calculatePMT(@Query("data") data: String): Observable<ApiResponse>

    /**
     * ApiNames to differentiate APIs
     */
    enum class ApiNames {
        onLogin, onSignUp, getGoals, calculatePMT
    }
}