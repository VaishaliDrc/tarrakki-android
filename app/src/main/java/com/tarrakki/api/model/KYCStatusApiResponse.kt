package com.tarrakki.api.model

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
}