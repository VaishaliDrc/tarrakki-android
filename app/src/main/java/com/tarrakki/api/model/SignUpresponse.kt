package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class SignUpresponse(
        @SerializedName("data")
        val `data`: String,
        @SerializedName("status")
        val status: Status
) {
    data class Status(
            @SerializedName("code")
            val code: Int,
            @SerializedName("message")
            val message: String
    )
}