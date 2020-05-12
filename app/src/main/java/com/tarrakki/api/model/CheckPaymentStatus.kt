package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

  data class CheckPaymentStatus(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("payment")
            val payment: Boolean
    )
}