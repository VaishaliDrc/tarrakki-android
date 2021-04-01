package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class EmailOrMobileOTPVerifyResponse(
        @SerializedName("data")
        val verificationData:  OTPVerifyResponse?
)

data class  OTPVerifyResponse(
        @SerializedName("is_email_verified")
        val isEmailVerified: Boolean,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("is_mobile_verified")
        val isMobileVerified: Boolean,
        @SerializedName("last_name")
        val last_name: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("first_name")
        val firstName: String

)
