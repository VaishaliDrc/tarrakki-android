package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordEmailResponse(
        @SerializedName("otp")
        val otp: Int,
        @SerializedName("otp_id")
        val otpId: Int
)