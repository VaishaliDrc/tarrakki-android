package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class SignUpresponse(
        @SerializedName("token")
        val token: String
)
