package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordEmailResponse(
        @SerializedName("otp")
        val otp: String?,
        @SerializedName("otp_id")
        val otpId: String?
)