package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class CreateKYCApiResponse(
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
        @SerializedName("application_url")
        val applicationUrl: String?,
        @SerializedName("auto_login_urL")
        val autoLoginUrL: String?,
        @SerializedName("customer_id")
        val customerId: String?,
        @SerializedName("mobile_auto_login_url")
        val mobileAutoLoginUrl: String?,
        @SerializedName("mobile_login_url")
        val mobileLoginUrl: String?,
        @SerializedName("signzy_id")
        val signzyId: String?
    )
}