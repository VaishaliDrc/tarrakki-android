package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class TarrakkiProAndEquityPricingResponse(
        @SerializedName("data")
        val tarrakkiProAndEquityPriceData:  TarrakkiProAndEquityPrice?,
)

data class TarrakkiProAndEquityPrice(
        @SerializedName("msg_for_equity_advisory")
        val msgForEquityAdvisory: String?,
        @SerializedName("msg_for_tarrakki_pro")
        val msgForTarrakkiPro: String?,
        @SerializedName("is_tarrakki_pro")
        val isTarrakkiPro: Boolean?,
        @SerializedName("is_equity_advisory")
        val isEquityAdvisory: Boolean?,
        @SerializedName("tarrakki_pro_pricing")
        val tarrakkiProPricing:  ArrayList<TarrakkiProPrice>?,
        @SerializedName("equity_advisory_pricing")
        val equityAdvisoryPricing:  ArrayList<TarrakkiProPrice>?,

)

data class TarrakkiProPrice(
        @SerializedName("price")
        val price: String?,
        @SerializedName("plan_duration")
        val planDuration: String?,

){
        val totalPrice  get() =  price!!.toInt() * planDuration!!.toInt()
}