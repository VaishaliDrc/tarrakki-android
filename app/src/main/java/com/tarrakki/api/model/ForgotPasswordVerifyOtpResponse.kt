package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordVerifyOtpResponse(
        @SerializedName("token")
        val token: String
)