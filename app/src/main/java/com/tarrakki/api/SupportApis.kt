package com.tarrakki.api

import com.tarrakki.api.model.ApiResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface SupportApis {

    @GET("support/get-query-list/")
    fun getQueryList(): Observable<ApiResponse>

    /***
     * two params query_id & sub_query_id as encrypted data
     * */
    @GET("support/get-question-list/")
    fun getQuestionList(@Query("data") data: String): Observable<ApiResponse>

    /***
     * two params query_id & sub_query_id as encrypted data
     * */
    @GET("support/raise-ticket/")
    fun checkTransactionStatus(@Query("data") data: String): Observable<ApiResponse>

    /***
     * two params query_id, issue_description, issue_image & transaction_id as encrypted data
     * */
    @Multipart
    @POST("support/raise-ticket/{id}/")
    fun createTicket(@Path("id") userId: String?, @Part("data") data: RequestBody, @Part file: MultipartBody.Part?): Observable<ApiResponse>

    /***
     * two params query_id, issue_description, issue_image & transaction_id as encrypted data
     * */
    @GET("support/conversation/{id}")
    fun getConversation(@Path("id") userId: String?, @Field("data") data: String): Observable<ApiResponse>

}
