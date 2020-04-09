package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName


/*
data class KYCStatusApiResponse(
        val `data`: Data?
) {
    data class Data(
            val completeRegistration: Boolean?, // false
            val isKycVerified: Boolean?, // false
            val isRemainingFields: String?, // 0
            val kraRemark: String?,
            val kycRemark: String?,
            val kycStatus: String?,
            val readyToInvest: Boolean? // false
    )
}*/
data class KYCStatusApiResponse(
        val `data`: Data?
) {
    data class Data(
            @SerializedName("complete_registration")
            val completeRegistration: Boolean?, // false
            @SerializedName("is_kyc_verified")
            val isKycVerified: Boolean?, // false
            @SerializedName("is_remaining_fields")
            val isRemainingFields: String?, // 2
            @SerializedName("kra_remark")
            val kraRemark: String?,
            @SerializedName("kyc_remark")
            val kycRemark: String?,
            @SerializedName("kyc_status")
            val kycStatus: String?, // underprocess
            @SerializedName("ready_to_invest")
            val readyToInvest: Boolean? // false
    )
}