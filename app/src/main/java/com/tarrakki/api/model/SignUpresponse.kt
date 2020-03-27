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
        val mobile: String?,
        @SerializedName("is_mobile_verified")
        val isMobileVerified: Boolean?,
        @SerializedName("is_email_activated")
        val isEmailActivated: Boolean?,
        @SerializedName("is_kyc_verified")
        val isKycVerified: Boolean?,
        @SerializedName("complete_registration")
        val completeRegistration: Boolean?,
        @SerializedName("kyc_status")
        val kycStatus: String?,
        @SerializedName("is_remaining_fields")
        val isRemainingFields: String?
)

