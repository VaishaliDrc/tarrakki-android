package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("email")
            val email: String,
            @SerializedName("guardian_name")
            val guardianName: String,
            @SerializedName("guardian_pan")
            val guardianPan: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("kyc_detail")
            val kycDetail: KycDetail,
            @SerializedName("mobile_number")
            val mobileNumber: String,
            @SerializedName("nominee_name")
            val nomineeName: String,
            @SerializedName("nominee_relationship")
            val nomineeRelationship: String,
            @SerializedName("user_profile_image")
            val userProfileImage: String,
            @SerializedName("is_email_activated")
            val isEmailActivated: Boolean,
            @SerializedName("is_mobile_verified")
            val isMobileVerified: Boolean,
            @SerializedName("corr_address")
            val address: String,
            @SerializedName("corr_city")
            val city: String,
            @SerializedName("corr_pincode")
            val pincode: String,
            @SerializedName("corr_state")
            val state: String,
            @SerializedName("corr_country")
            val country: String
    ) {
        data class KycDetail(
                @SerializedName("full_name")
                val fullName: String,
                @SerializedName("pan")
                val pan: String,
                @SerializedName("pan_name")
                val panName: String
        )
    }
}