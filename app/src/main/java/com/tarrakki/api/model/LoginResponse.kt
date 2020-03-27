package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
        @SerializedName("complete_registration")
        val completeRegistration: Boolean?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("is_email_activated")
        val isEmailActivated: Boolean?,
        @SerializedName("is_kyc_verified")
        val isKycVerified: Boolean?,
        @SerializedName("is_mobile_verified")
        val isMobileVerified: Boolean?,
        @SerializedName("mobile_number")
        val mobile: String?,
        @SerializedName("token")
        val token: String?,
        @SerializedName("user_id")
        val userId: String?,
        @SerializedName("ready_to_invest")
        val readyToInvest: Boolean?,
        @SerializedName("kyc_status")
        val kycStatus: String?,
        @SerializedName("is_remaining_fields")
        val isRemainingFields: String?
)
/*
data class LoginResponse(
        @SerializedName("token")
        val token: String,
        @SerializedName("user_id")
        val userId: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("mobile_number")
        val mobile: String?
)*/
