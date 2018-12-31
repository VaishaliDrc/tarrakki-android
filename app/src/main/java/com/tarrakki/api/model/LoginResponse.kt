package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName


data class LoginResponse(
        @SerializedName("token")
        val token: String,
        @SerializedName("status")
        val status: Status?
) {
    data class Status(
            @SerializedName("code")
            val code: Int,
            @SerializedName("message")
            val message: String
    )
}