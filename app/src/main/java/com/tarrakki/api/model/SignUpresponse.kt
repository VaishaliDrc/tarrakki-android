package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class SignUpresponse(
        @SerializedName("token")
        val token: String,
        @SerializedName("user_id")
        val userId: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("mobile_number")
        val mobile: String?
)

