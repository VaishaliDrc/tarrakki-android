package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class IMandateResponse(
        @SerializedName("data")
        val data_html: String
)