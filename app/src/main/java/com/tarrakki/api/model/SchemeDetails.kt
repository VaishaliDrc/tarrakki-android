package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class SchemeDetails(
        @SerializedName("data")
        val `data`: Data?
) {
    data class Data(
            @SerializedName("ARN_Code")
            val aRNCode: String?,
            @SerializedName("AgentName")
            val agentName: String?,
            @SerializedName("BankName")
            val bankName: String?,
            @SerializedName("EUIN")
            val eUIN: String?,
            @SerializedName("EasyInvestFlag")
            val easyInvestFlag: String?,
            @SerializedName("Folio")
            val folio: String?,
            @SerializedName("FreeAmt")
            val freeAmt: String?,
            @SerializedName("FreeSwpAmt")
            val freeSwpAmt: String?,
            @SerializedName("Freeunits")
            val freeunits: String?,
            @SerializedName("InProcessAmt")
            val inProcessAmt: String?,
            @SerializedName("Insta_Amount")
            val instaAmount: String?,
            @SerializedName("InstaRedeemEligible")
            val instaRedeemEligible: String?,
            @SerializedName("Insta_units")
            val instaUnits: String?,
            @SerializedName("InvestedAmt")
            val investedAmt: String?,
            @SerializedName("LockinAmt")
            val lockinAmt: String?,
            @SerializedName("LockinSWPAmt")
            val lockinSWPAmt: String?,
            @SerializedName("Lockinunits")
            val lockinunits: String?,
            @SerializedName("MinAmt")
            val minAmt: String?,
            @SerializedName("MinUnits")
            val minUnits: String?,
            @SerializedName("Msg1")
            val msg1: String?,
            @SerializedName("Msg2")
            val msg2: String?,
            @SerializedName("Nav")
            val nav: String?,
            @SerializedName("Nav_Date")
            val navDate: String?,
            @SerializedName("Option_code")
            val optionCode: String?,
            @SerializedName("Plan_code")
            val planCode: String?,
            @SerializedName("PledgeUnits")
            val pledgeUnits: String?,
            @SerializedName("RestrictedAmt")
            val restrictedAmt: String?,
            @SerializedName("RestrictedUnits")
            val restrictedUnits: String?,
            @SerializedName("Return_code")
            val returnCode: String?,
            @SerializedName("Return_Msg")
            val returnMsg: String?,
            @SerializedName("SUBARNCode")
            val sUBARNCode: String?,
            @SerializedName("SchemeCategory")
            val schemeCategory: String?,
            @SerializedName("Scheme_code")
            val schemeCode: String?,
            @SerializedName("SchemeDescription")
            val schemeDescription: String?,
            @SerializedName("TotalAmt")
            val totalAmt: String?,
            @SerializedName("Totalunits")
            val totalunits: String?
    )
}