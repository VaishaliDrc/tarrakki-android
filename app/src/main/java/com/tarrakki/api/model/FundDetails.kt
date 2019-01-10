package com.tarrakki.api.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import org.supportcompact.ktx.convertTo
import org.supportcompact.ktx.parseToPercentageOrNA
import org.supportcompact.ktx.toDate
import org.supportcompact.ktx.toReturn

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
        val benchmarks: List<Benchmark>,
        @SerializedName("bse_amc_code")
        val bseAmcCode: String,
        @SerializedName("channel_partner_code")
        val channelPartnerCode: String,
        @SerializedName("defer_loads")
        val deferLoads: List<Any>,
        @SerializedName("dp_day_end_nav")
        val dpDayEndNav: String,
        @SerializedName("fna_aum")
        val fnaAum: Double,
        @SerializedName("fna_surveyed_fund_net_assets")
        val fnaSurveyedFundNetAssets: Any,
        @SerializedName("fna_surveyed_fund_net_assets_date")
        val fnaSurveyedFundNetAssetsDate: Any,
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
        val iaipAip: List<IaipAip>,
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
        val managers: List<Any>,
        @SerializedName("mstar_id")
        val mstarId: Any,
        @SerializedName("offer_price")
        val offerPrice: Int,
        @SerializedName("pi_minimum_initial")
        val piMinimumInitial: Any,
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
        val standardDeviation5Yr: Double,
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

    var riskProgress = 20
        get() = when {
            "Moderately Low risk".equals(fscbiIndianRiskLevel, true) -> 40
            "Medium Risk".equals(fscbiIndianRiskLevel, true) -> 50
            "Moderately High risk".equals(fscbiIndianRiskLevel, true) -> 80
            "High Risk".equals(fscbiIndianRiskLevel, true) -> 90
            else -> 20
        }

    var hasNegativeReturn: Boolean = false
        get() = if (!TextUtils.isEmpty(dpDayEndNav) && !TextUtils.isEmpty(preDpDayEndNav)) {
            try {
                val result = (dpDayEndNav.toDouble() - preDpDayEndNav.toDouble()) * 100
                result < 0
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
}

data class IaipAip(
        @SerializedName("frequency")
        val frequency: String,
        @SerializedName("frequency_date")
        val frequencyDate: String,
        @SerializedName("min_amount")
        val minAmount: Int,
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