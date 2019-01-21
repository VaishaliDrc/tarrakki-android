package com.tarrakki.api.model

import android.text.TextUtils
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.supportcompact.ktx.*
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


data class FundDetails(
        @SerializedName("bank_savings_return")
        val bankSavingsReturn: String?,
        @SerializedName("fixed_deposit_return")
        val fixedDepositReturn: String?,
        @SerializedName("funds_details")
        val fundsDetails: FundsDetails?,
        @SerializedName("top_ten_holdings")
        val topTenHolding: Any
) {

    var topTenHoldings: ArrayList<TopTenHolding>? = null
        get() = if (field == null) {
            field = arrayListOf()
            if (topTenHolding is List<*>) {
                try {
                    val gson = Gson()
                    topTenHolding.forEach { item ->
                        val jsonObject = gson.toJsonTree(item).asJsonObject
                        val data = gson.fromJson(jsonObject, TopTenHolding::class.java)
                        data?.let {
                            field?.add(it)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                e("is ArrayList=>", true)
            } else {
                val data = topTenHolding.toString().parseArray<ArrayList<TopTenHolding>>()
                data?.let {
                    field?.addAll(it)
                }
            }
            field
        } else {
            field
        }

    var returnsHistory: ArrayList<ReturnsHistory>? = null
        get() = if (field == null) {
            field = arrayListOf()
            fundsDetails?.totalReturnIndex?.let {
                val listType = object : TypeToken<ArrayList<ReturnsHistory>>() {}.type
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
                    var df: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    @Throws(JsonParseException::class)
                    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
                        return try {
                            df.parse(json.asString)
                        } catch (ex: java.lang.Exception) {
                            Date()
                        }
                    }
                })
                val dateGson = gsonBuilder.create()
                val data = dateGson.fromJson<ArrayList<ReturnsHistory>>(it, listType)
                data?.let { all ->
                    field?.addAll(all)
                }
            }
            field
        } else {
            field
        }

    var tarrakkiReturn: Double = 0.0
        get() = if (field == 0.0) getReturn() else field

    fun getReturn(x: Int = 1, y: Int = 0): Double {
        var mReturn = 0.0
        val now = Calendar.getInstance()
        now.add(if (y == 0) Calendar.YEAR else Calendar.MONTH, -x)
        val date = now.time.toDate()
        val data = returnsHistory?.firstOrNull { r -> date.compareTo(r.date) == 0 }
        data?.let {
            mReturn = data.value?.toDoubleOrNull() ?: 0.0
        }
        return mReturn.decimalFormat().toCurrency()
    }
}

data class FundsDetails(
        @SerializedName("amc_name")
        val amcName: String,
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
        var fscbiIndianRiskLevel: String?,
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
        val ttrReturn1Mth: String?,
        @SerializedName("ttr_return_1_yr")
        val ttrReturn1Yr: String?,
        @SerializedName("ttr_return_3_mth")
        val ttrReturn3Mth: String?,
        @SerializedName("ttr_return_3_yr")
        val ttrReturn3Yr: String?,
        @SerializedName("ttr_return_5_yr")
        val ttrReturn5Yr: String?,
        @SerializedName("ttr_return_6_mth")
        val ttrReturn6Mth: String?,
        @SerializedName("ttr_return_since_inception")
        val ttrReturnSinceInception: String?,
        @SerializedName("investment_strategy")
        val investmentStrategy: String?
) {

    var fundObjective = ""
        get() = "$investmentStrategy".replace("\n", " ")

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
        get() = if (deferLoads != null && deferLoads.isNotEmpty()) {
            val data = deferLoads.firstOrNull { it -> it.value > 0 }
            if (data != null) {
                val intNoOfDays: Int
                val intBreatPointToConsider: Int = if (data.highBreakpoint == 0) data.lowBreakpoint else data.highBreakpoint
                intNoOfDays = when {
                    "YEARS".equals(data.breakpointUnit, true) -> intBreatPointToConsider * 365
                    "MONTHS".equals(data.breakpointUnit, true) -> intBreatPointToConsider * 12
                    else -> intBreatPointToConsider
                }
                "${data.value.toReturnAsPercentage()} if redeemed within $intNoOfDays days"
            } else {
                0.0.toReturnAsPercentage()
            }
        } else {
            0.0.toReturnAsPercentage()
        }

    var minSIPAmount = ""
        get() = if (iaipAip != null && iaipAip.isNotEmpty()) {
            iaipAip.firstOrNull { it -> "STP".equals(it.siType, true) && "Monthly".equals(it.frequency, true) }?.minAmount?.toCurrency()
                    ?: "NA"
        } else ""

    var lumpsumAmount: String = ""
        get() = piMinimumInitial?.toDoubleOrNull()?.toCurrency() ?: "NA"

    var validminSIPAmount = 0.00
        get() = if (iaipAip != null && iaipAip.isNotEmpty()) {
            iaipAip.firstOrNull { it -> "STP".equals(it.siType, true) && "Monthly".equals(it.frequency, true) }?.minAmount
                    ?: 0.00
        } else 0.00

    var validminlumpsumAmount = 0.00
        get() = piMinimumInitial?.toDouble() ?: 0.00

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
        get() = dpDayEndNav.toDoubleOrNull()?.toReturn()

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
        val value: Double
)

data class TopTenHolding(
        @SerializedName("Country")
        val country: String,
        @SerializedName("CountryId")
        val countryId: String,
        @SerializedName("Coupon")
        val coupon: String,
        @SerializedName("Currency")
        val currency: String,
        @SerializedName("CurrencyId")
        val currencyId: String,
        @SerializedName("GlobalSector")
        val globalSector: String,
        @SerializedName("GlobalSectorId")
        val globalSectorId: String,
        @SerializedName("HoldingType")
        val holdingType: String,
        @SerializedName("ISIN")
        val iSIN: String,
        @SerializedName("IndianCreditQualityClassification")
        val indianCreditQualityClassification: String,
        @SerializedName("MarketValue")
        val marketValue: String,
        @SerializedName("MaturityDate")
        val maturityDate: String,
        @SerializedName("Name")
        val name: String,
        @SerializedName("NumberOfShare")
        val numberOfShare: String,
        @SerializedName("RegionId")
        val regionId: String,
        @SerializedName("Sector")
        val sector: String,
        @SerializedName("SectorId")
        val sectorId: String,
        @SerializedName("ShareChange")
        val shareChange: String,
        @SerializedName("Stylebox")
        val stylebox: String,
        @SerializedName("Ticker")
        val ticker: String,
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

data class ReturnsHistory(
        @SerializedName("d")
        val date: Date,
        @SerializedName("v")
        val value: String?
)
