package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName
import org.supportcompact.ktx.toReturnAsPercentage


data class InvestmentFunds(
        @SerializedName("fixed_deposit_return")
        val fixedDepositReturn: String?,
        @SerializedName("fscbi_broad_category_list")
        val fscbiBroadCategoryList: List<FscbiBroadCategory>,
        @SerializedName("fscbi_category_list")
        val fscbiCategoryList: List<FscbiCategory>,
        @SerializedName("funds")
        val funds: ArrayList<Fund>,
        @SerializedName("limit")
        val limit: Int,
        @SerializedName("offset")
        val offset: Int
) {
    inner class Fund(
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
        var FDReturn: String? = fixedDepositReturn?.toDoubleOrNull()?.toReturnAsPercentage()
        var threeYearOfFundReturn: String? = yReturns?.toDoubleOrNull()?.toReturnAsPercentage()
        var volatility: String? = standardDeviation5Yr?.toReturnAsPercentage()
        var returnInYear: String? = ttrReturn1Yr?.toReturnAsPercentage()
        var description: String = ""
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
            get() = ttrReturnSinceInception?.toReturnAsPercentage()
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
}

/*
data class InvestmentFunds(
        @SerializedName("funds")
        val funds: ArrayList<Fund>,
        @SerializedName("limit")
        val limit: Int,
        @SerializedName("offset")
        val offset: Int
) {
    var fundReturn = 0.0
    var FDReturns: Double? = 0.0
    inner class Fund(
            @SerializedName("3y_funds")
            val yFunds: Double?,
            @SerializedName("3y_returns")
            val yReturns: String,
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
        var FDReturn: String? = FDReturns?.toReturnAsPercentage()
        var threeYearOfFundReturn: String? = yFunds?.toReturnAsPercentage()
        var volatility: String? = standardDeviation5Yr?.toReturnAsPercentage()
        var returnInYear: String? = ttrReturn1Yr?.toReturnAsPercentage()
        var description: String = ""
            get() = "$schemeType-$fscbiCategoryName"
        var currentReturn: String = ""
            get() = if (!TextUtils.isEmpty("$fundReturn")) {
                try {
                    fundReturn.toReturnAsPercentage()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "NA"
                }
            } else {
                "NA"
            }
        var hasNegativeReturn: Boolean = false
            get() = fundReturn < 0
        var returnSinceLaunch: String? = ""
            get() = ttrReturnSinceInception?.toReturnAsPercentage()
    }
}*/
