package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName


data class LoginResponse(
        @SerializedName("token")
        val token: String,
        @SerializedName("user_id")
        val userId: String?
)