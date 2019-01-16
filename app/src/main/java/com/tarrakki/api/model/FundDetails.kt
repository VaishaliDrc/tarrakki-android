package com.tarrakki.api.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import org.supportcompact.ktx.*

data class FundDetails(
        @SerializedName("bank_savings_return")
        val bankSavingsReturn: String,
        @SerializedName("fixed_deposit_return")
        val fixedDepositReturn: String,
        @SerializedName("funds_details")
        val fundsDetails: FundsDetails?,
        @SerializedName("top_ten_holdings")
        val topTenHoldings: ArrayList<TopTenHolding>
)

data class FundsDetails(
        @SerializedName("active")
        val active: Boolean,
        @SerializedName("amc_active_flag")
        val amcActiveFlag: String,
        @SerializedName("amc_ind")
        val amcInd: String,
        @SerializedName("benchmarks")
        val benchmarks: List<Benchmark>?,
        @SerializedName("bse_amc_code")
        val bseAmcCode: String,
        @SerializedName("channel_partner_code")
        val channelPartnerCode: String,
        @SerializedName("defer_loads")
        val deferLoads: List<ExitLoad>?,
        @SerializedName("dp_day_end_nav")
        val dpDayEndNav: String,
        @SerializedName("fna_aum")
        val fnaAum: Double,
        @SerializedName("fna_surveyed_fund_net_assets")
        val fnaSurveyedFundNetAssets: String?,
        @SerializedName("fna_surveyed_fund_net_assets_date")
        val fnaSurveyedFundNetAssetsDate: String?,
        @SerializedName("fscbi_broad_category_name")
        val fscbiBroadCategoryName: String,
        @SerializedName("fscbi_category_id")
        val fscbiCategoryId: Int,
        @SerializedName("fscbi_distribution_status")
        val fscbiDistributionStatus: String,
        @SerializedName("fscbi_indian_risk_level")
        val fscbiIndianRiskLevel: String?,
        @SerializedName("fscbi_isin")
        val fscbiIsin: String,
        @SerializedName("fscbi_legal_name")
        val fscbiLegalName: String,
        @SerializedName("fscbi_legal_structure")
        val fscbiLegalStructure: String,
        @SerializedName("fscbi_provider_company_name")
        val fscbiProviderCompanyName: String,
        @SerializedName("fscbi_rta_code")
        val fscbiRtaCode: Any,
        @SerializedName("iaip_aip")
        val iaipAip: List<IaipAip>?,
        @SerializedName("id")
        val id: Int,
        @SerializedName("inception_date")
        val inceptionDate: String?,
        @SerializedName("ipo_date")
        val ipoDate: Any,
        @SerializedName("is_tarrakki_recommended")
        val isTarrakkiRecommended: Boolean,
        @SerializedName("kd_performance_start_date")
        val kdPerformanceStartDate: Any,
        @SerializedName("ls_minimum_redemption_amount")
        val lsMinimumRedemptionAmount: Any,
        @SerializedName("managers")
        val managers: List<FundManager>?,
        @SerializedName("mstar_id")
        val mstarId: Any,
        @SerializedName("offer_price")
        val offerPrice: Int,
        @SerializedName("pi_minimum_initial")
        val piMinimumInitial: String?,
        @SerializedName("pi_minimum_subsequent")
        val piMinimumSubsequent: Any,
        @SerializedName("pre_dp_day_end_nav")
        val preDpDayEndNav: String,
        @SerializedName("risk_level_id")
        val riskLevelId: Int,
        @SerializedName("rta_agent_code")
        val rtaAgentCode: String,
        @SerializedName("rta_code")
        val rtaCode: String,
        @SerializedName("scheme_plan")
        val schemePlan: String,
        @SerializedName("scheme_type")
        val schemeType: String,
        @SerializedName("standard_deviation_5_yr")
        val standardDeviation5Yr: String?,
        @SerializedName("subscription_start_date")
        val subscriptionStartDate: Any,
        @SerializedName("total_return_index")
        val totalReturnIndex: String,
        @SerializedName("ts_day_end_nav_date")
        val tsDayEndNavDate: String?,
        @SerializedName("ttr_return_10_yr")
        val ttrReturn10Yr: Int,
        @SerializedName("ttr_return_1_mth")
        val ttrReturn1Mth: Int,
        @SerializedName("ttr_return_1_yr")
        val ttrReturn1Yr: Double,
        @SerializedName("ttr_return_3_mth")
        val ttrReturn3Mth: Double,
        @SerializedName("ttr_return_3_yr")
        val ttrReturn3Yr: Double,
        @SerializedName("ttr_return_5_yr")
        val ttrReturn5Yr: Double,
        @SerializedName("ttr_return_6_mth")
        val ttrReturn6Mth: Double,
        @SerializedName("ttr_return_since_inception")
        val ttrReturnSinceInception: Double,
        @SerializedName("investment_strategy")
        val investmentStrategy: String?
) {
    var NAVDate: String = ""
        get() = tsDayEndNavDate?.toDate()?.convertTo() ?: "NA"

    var assetsDate: String = ""
        get() = fnaSurveyedFundNetAssetsDate?.toDate()?.convertTo() ?: "NA"

    var riskProgress = 20
        get() = when {
            "Moderately Low risk".equals(fscbiIndianRiskLevel, true) -> 40
            "Medium Risk".equals(fscbiIndianRiskLevel, true) -> 50
            "Moderately High risk".equals(fscbiIndianRiskLevel, true) -> 80
            "High Risk".equals(fscbiIndianRiskLevel, true) -> 90
            else -> 20
        }

    var benchmark = ""
        get() = if (benchmarks != null && benchmarks.isNotEmpty()) {
            var name = ""
            benchmarks.forEach {
                if (name.isEmpty()) {
                    name = it.indexName
                }
                name += ", ${it.indexName}"
            }
            name
        } else ""

    var netAssets: String? = ""
        get() = fnaSurveyedFundNetAssets?.toDoubleOrNull()?.toCurrency()?.plus(" (${fnaSurveyedFundNetAssetsDate?.toDate()?.convertTo()})")

    var fundManagers = ""
        get() = if (managers != null && managers.isNotEmpty()) {
            var name = ""
            managers.forEach {
                if (name.isEmpty()) {
                    name = it.name
                }
                name += ", ${it.name}"
            }
            name
        } else ""

    var exitLoad: String = ""
        get() = if (deferLoads != null && deferLoads.isNotEmpty()) parseToPercentageOrNA(deferLoads[0].value) else ""

    var minSIPAmount = ""
        get() = if (iaipAip != null && iaipAip.isNotEmpty()) {
            iaipAip.firstOrNull { it -> "STP".equals(it.siType, true) && "Monthly".equals(it.frequency, true) }?.minAmount?.toCurrency()
                    ?: "NA"
        } else ""

    var lumpsumAmount: String = ""
        get() = piMinimumInitial?.toDoubleOrNull()?.toCurrency() ?: "NA"

    var vol = ""
        get() = parseToPercentageOrNA(standardDeviation5Yr)

    var hasNegativeReturn: Boolean = false
        get() = if (!TextUtils.isEmpty(dpDayEndNav) && !TextUtils.isEmpty(preDpDayEndNav)) {
            try {
                val result = ((dpDayEndNav.toDouble() - preDpDayEndNav.toDouble()) * 100) / dpDayEndNav.toDouble()
                result < 0
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }

    var nav: String? = ""
        get() = dpDayEndNav?.toDoubleOrNull()?.toReturn()

    var currentReturn: String = ""
        get() = if (!TextUtils.isEmpty(dpDayEndNav) && !TextUtils.isEmpty(preDpDayEndNav)) {
            try {
                val result: String? = "${(dpDayEndNav.toDouble() - preDpDayEndNav.toDouble()).toReturn()} (${(((dpDayEndNav.toDouble() - preDpDayEndNav.toDouble()) * 100) / dpDayEndNav.toDouble()).toReturnAsPercentage()})"
                result ?: "NA"
            } catch (e: Exception) {
                e.printStackTrace()
                "NA"
            }
        } else {
            "NA"
        }

}

data class IaipAip(
        @SerializedName("frequency")
        val frequency: String,
        @SerializedName("frequency_date")
        val frequencyDate: String,
        @SerializedName("min_amount")
        val minAmount: Double?,
        @SerializedName("min_tenure")
        val minTenure: Int,
        @SerializedName("si_type")
        val siType: String,
        @SerializedName("subsquent_amount")
        val subsquentAmount: Int
)

data class Benchmark(
        @SerializedName("index_id")
        val indexId: String,
        @SerializedName("index_name")
        val indexName: String,
        @SerializedName("weighting")
        val weighting: Int
)

data class FundManager(
        @SerializedName("display")
        val display: String,
        @SerializedName("manager_id")
        val managerId: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("role")
        val role: String,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("tenure")
        val tenure: Double
)

data class ExitLoad(
        @SerializedName("breakpoint_unit")
        val breakpointUnit: String,
        @SerializedName("high_breakpoint")
        val highBreakpoint: Int,
        @SerializedName("low_breakpoint")
        val lowBreakpoint: Int,
        @SerializedName("unit")
        val unit: String,
        @SerializedName("value")
        val value: String?
)

data class TopTenHolding(
        @SerializedName("Name")
        val name: String,
        @SerializedName("Weighting")
        val weighting: String
) {
    var percentage: String? = null
        get() = parseToPercentageOrNA(weighting)
    var progress: Int = 0
        get() = try {
            (weighting.toDouble().toReturn().toDouble() * 1000).toInt()
        } catch (e: Exception) {
            0
        }
}