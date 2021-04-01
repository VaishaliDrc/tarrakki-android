package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class NormalLoginResponse(
        @SerializedName("data")
        val loginData:  NormalLoginData?
)

data class  NormalLoginData(
        @SerializedName("is_email_verified")
        val isEmailVerified: Boolean,
        @SerializedName("otp")
        val otp: Int,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("is_mobile")
        val isMobile: Boolean,
        @SerializedName("is_email")
        val isEmail: Boolean,
        @SerializedName("is_mobile_verified")
        val isMobileVerified: Boolean,
        @SerializedName("last_name")
        val last_name: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("otp_id")
        val otpId: Int?,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("is_registered")
        val isRegistered: Boolean
)
