package com.tarrakki.api.model

import android.support.annotation.ColorRes
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import com.tarrakki.module.recommended.*

data class InvestmentRecommendFundResponse(
        @SerializedName("data")
        val `data`: List<Data>
) {
    data class Data(
            @SerializedName("scheme_type")
            val schemeType: String,
            @SerializedName("fscbi_category_name")
            val fscbiCategoryName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("lumpsum_amount")
            val lumpsum: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("sip_amount")
            val sipAmount: String,
            @SerializedName("ttr_return_5_yr")
            val ttrReturn5Yr: String,
            @SerializedName("sip_weightage")
            val sipWeightage: Int = 0,
            @SerializedName("lumpsum_weightage")
            val lumpsumWeightage: Int = 0
    ) {

        var allWeightage = ""
            get() {
                var weightage = ""
                weightage += if (sipWeightage != 0) {
                    "SIP: $sipWeightage%"
                } else {
                    "SIP:  N/A"
                }
                weightage += "\n"
                weightage += if (lumpsumWeightage != 0) {
                    "Lumpsum: $lumpsumWeightage%"
                } else {
                    "Lumpsum:  N/A"
                }
                return weightage
            }

        var returns: String = ""
            get() = if (!TextUtils.isEmpty(ttrReturn5Yr)) {
                try {
                    String.format("%.2f", ttrReturn5Yr.toDouble()).plus("%")
                } catch (e: Exception) {
                    e.printStackTrace()
                    "N/A"
                }
            } else {
                "N/A"
            }

        @ColorRes
        var fundColor: Int = R.color.balanced_fund_color
            get() = when {
                KEY_FUND_DIST_EQUITY.equals("$schemeType", true) ||
                        KEY_FUND_DIST_ELSS.equals("$schemeType", true) ||
                        KEY_FUND_DIST_HYBRID.equals("$schemeType", true) ||
                        KEY_FUND_DIST_EQUITY.equals("$schemeType", true) -> {
                    R.color.equity_fund_color
                }
                KEY_FUND_DIST_BALANCED.equals("$schemeType", true) -> {
                    R.color.balanced_fund_color
                }
                KEY_FUND_DIST_MIP.equals("$schemeType", true) ||
                        KEY_FUND_DIST_BOND.equals("$schemeType", true) ||
                        KEY_FUND_DIST_GUILT.equals("$schemeType", true) ||
                        KEY_FUND_DIST_LIQUID.equals("$schemeType", true) ||
                        KEY_FUND_DIST_STP.equals("$schemeType", true) ||
                        KEY_FUND_DIST_DEBT.equals("$schemeType", true) -> {
                    R.color.debt_fund_color
                }
                else -> {
                    R.color.fof_fund_color
                }
            }
    }
}