package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName
import org.supportcompact.ktx.parseToPercentageOrNA

data class InvestmentFunds(
        @SerializedName("fixed_deposit_return")
        val fixedDepositReturn: String?,
        @SerializedName("fscbi_broad_category_list")
        val fscbiBroadCategoryList: List<FscbiBroadCategory>,
        @SerializedName("fscbi_category_list")
        val fscbiCategoryList: List<FscbiCategory>,
        @SerializedName("funds")
        val funds: ArrayList<Fund>,
        @SerializedName("risk_levels")
        val riskLevels: List<RiskLevel>,
        @SerializedName("limit")
        val limit: Int,
        @SerializedName("offset")
        val offset: Int
) {
    data class Fund(
            @SerializedName("3y_funds")
            val yFunds: String?,
            @SerializedName("3y_returns")
            val yReturns: String?,
            @SerializedName("fscbi_broad_category_name")
            val fscbiBroadCategoryName: String,
            @SerializedName("fscbi_category_name")
            val fscbiCategoryName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("scheme_type")
            val schemeType: String,
            @SerializedName("standard_deviation_5_yr")
            val standardDeviation5Yr: Double?,
            @SerializedName("ttr_return_1_yr")
            val ttrReturn1Yr: Double?,
            @SerializedName("ttr_return_since_inception")
            val ttrReturnSinceInception: Double?
    ) {
        var FDReturn: String? = null
        //get() = parseToPercentageOrNA(fixedDepositReturn)
        var threeYearOfFundReturn: String? = null
            get() = parseToPercentageOrNA(yReturns)
        var volatility: String? = null
            get() = parseToPercentageOrNA("$standardDeviation5Yr")
        var returnInYear: String? = null
            get() = parseToPercentageOrNA("$ttrReturn1Yr")
        var description: String? = null
            get() = "$schemeType-$fscbiCategoryName"
        var currentReturn: String = ""
        /*get() = if (!TextUtils.isEmpty("$fundReturn")) {
            try {
                fundReturn.toReturnAsPercentage()
            } catch (e: Exception) {
                e.printStackTrace()
                "NA"
            }
        } else {
            "NA"
        }*/
        var hasNegativeReturn: Boolean = false
        //get() = fundReturn < 0
        var returnSinceLaunch: String? = ""
            get() = parseToPercentageOrNA("$ttrReturnSinceInception")
    }

    data class FscbiCategory(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String
    )

    data class FscbiBroadCategory(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String
    )

    data class RiskLevel(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String
    )

    fun getRiskLevelId(riskLevel: String): Int? {
        return riskLevels.firstOrNull { r -> riskLevel.equals(r.name, true) }?.id
    }

}
