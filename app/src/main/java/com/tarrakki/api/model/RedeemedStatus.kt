package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class RedeemedStatus(
        @SerializedName("data")
        val `data`: Data?
) {
    data class Data(
            @SerializedName("withdrawal_confirm")
            val withdrawalConfirm: String?,
            @SerializedName("withdrawal_sent")
            val withdrawalSent: String?,
            @SerializedName("amount_creadited")
            val amountCreadited: String?,
            @SerializedName("Date_Time")
            val dateTime: String?,
            @SerializedName("bse_remarks")
            val remarks: String
    )
}