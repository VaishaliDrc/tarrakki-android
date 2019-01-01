package com.tarrakki.api.model

import android.support.annotation.ColorRes
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.tarrakki.R

data class RecommendedFunds(
        @SerializedName("data")
        val `data`: ArrayList<Fund>
)

data class Fund(
        @SerializedName("amount")
        val amount: Int,
        @SerializedName("fscbi_broad_category_name")
        val fscbiBroadCategoryName: String,
        @SerializedName("fscbi_category_name")
        val fscbiCategoryName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("lumpsum")
        val lumpsum: Int,
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
        get() = when ("$schemeType") {
            "EQUITY" -> R.color.equity_fund_color
            "DEBT" -> R.color.debt_fund_color
            else -> R.color.balanced_fund_color
        }
}