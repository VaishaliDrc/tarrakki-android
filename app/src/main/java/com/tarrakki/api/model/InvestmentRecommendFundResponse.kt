package com.tarrakki.api.model

import android.support.annotation.ColorRes
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.tarrakki.R

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
            val lumpsum: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("sip_amount")
            val sipAmount: Int,
            @SerializedName("ttr_return_5_yr")
            val ttrReturn5Yr: String,
            @SerializedName("weightage")
            val weightage: Int
    ) {
        var returns: String = ""
            get() = if (!TextUtils.isEmpty(ttrReturn5Yr)) {
                try {
                    String.format("%.2f", ttrReturn5Yr.toDouble()).plus("%")
                } catch (e: Exception) {
                    e.printStackTrace()
                    "NA"
                }
            } else {
                "NA"
            }

        @ColorRes
        var fundColor: Int = R.color.balanced_fund_color
            get() = when {
                "EQUITY".equals("$schemeType", true) -> R.color.equity_fund_color
                "DEBT".equals("$schemeType", true) -> R.color.debt_fund_color
                "FOF".equals("$schemeType", true) -> R.color.fof_fund_color
                else -> R.color.balanced_fund_color
            }
    }
}