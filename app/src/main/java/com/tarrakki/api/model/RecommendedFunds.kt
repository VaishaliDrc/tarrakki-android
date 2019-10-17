package com.tarrakki.api.model

import android.support.annotation.ColorRes
import com.google.gson.annotations.SerializedName
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.module.recommended.*
import org.supportcompact.ktx.color
import org.supportcompact.ktx.parseToPercentageOrNA
import org.supportcompact.ktx.toCurrency

data class RecommendedFundsData(
        @SerializedName("data")
        val `data`: RecommendedFunds
)

/*data class RecommendedFunds(
        @SerializedName("funds")
        val funds: List<Fund>,
        @SerializedName("user_goal_id")
        val userGoalId: Int
)*/

data class RecommendedFunds(
        @SerializedName("funds")
        val `data`: ArrayList<Fund>,
        @SerializedName("user_goal_id")
        val userGoalId: Int
)

data class Fund(
        @SerializedName("sip_amount")
        val amount: String?,
        @SerializedName("fscbi_broad_category_name")
        val fscbiBroadCategoryName: String,
        @SerializedName("fscbi_category_name")
        val fscbiCategoryName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("lumpsum")
        val lumpsum: Double,
        @SerializedName("name")
        val name: String,
        @SerializedName("scheme_type")
        val schemeType: String,
        @SerializedName("ttr_return_5_yr")
        val ttrReturn5Yr: String,
        @SerializedName("weightage")
        val weightage: Int
) {

    var description: String = ""
        get() = "$schemeType-$fscbiCategoryName"

    var returns: String = ""
        get() = parseToPercentageOrNA(ttrReturn5Yr)

    val sipAmount
        get() = amount?.toDoubleOrNull()?.toCurrency()

    @ColorRes
    var fundColor: Int = R.color.balanced_fund_color
        get() = App.INSTANCE.color(when {
            KEY_FUND_DIST_EQUITY.equals("$schemeType", true) ||
                    KEY_FUND_DIST_ELSS.equals("$schemeType", true) ||
                    KEY_FUND_DIST_HYBRID.equals("$schemeType", true) ||
                    "$schemeType".contains(KEY_FUND_DIST_EQUITY, true) -> {
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
        })
}
