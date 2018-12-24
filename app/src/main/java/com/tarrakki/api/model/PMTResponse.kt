package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName


data class PMTResponse(
        @SerializedName("future_value")
        val futureValue: Double,
        @SerializedName("pmt")
        val pmt: Double
)