package com.tarrakki.api.model

import android.graphics.Color
import android.text.TextUtils
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.supportcompact.ktx.*
import java.lang.reflect.Type
import java.math.BigInteger
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
        val topTenHolding: Any?,
        @SerializedName("folio_list")
        val folios: List<String>?,
        @SerializedName("bse_data")
        val bseData: BSEData?,
        @SerializedName("lumpsum_additional_min_amount")
        val lumpsumAdditionalMinAmount: String?,
        @SerializedName("risk_profile")
        val riskProfile: String?
) {

    val additionalMinLumpsum: BigInteger
        get() = lumpsumAdditionalMinAmount?.toCurrencyBigInt() ?: BigInteger.ZERO

    val riskProfileLevel: Float
        get() = when {
            "Aggressive".equals("$riskProfile", true) -> 90f
            "Moderately Aggressive".equals("$riskProfile", true) -> 70f
            "Balanced".equals("$riskProfile", true) -> 50f
            "Moderately Conservative".equals("$riskProfile", true) -> 30f
            "Conservative".equals("$riskProfile", true) -> 10f
            else -> 0f
        }

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
                val data = topTenHolding?.toString()?.parseArray<ArrayList<TopTenHolding>>()
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

    var YTDReturn = ""
        get() = if (TextUtils.isEmpty(field)) {
            var mReturn = 0.0
            val now = Calendar.getInstance()
            now.set(Calendar.DAY_OF_YEAR, 1)
            val date = now.time.toDate()
            val data = returnsHistory?.firstOrNull { r -> date.compareTo(r.date) == 0 }
            data?.let {
                try {
                    val todayReturn = fundsDetails?.dpDayEndNav?.toDoubleOrNull()
                    if (todayReturn != null) {
                        val pReturn = data.value ?: 0.0
                        mReturn = ((todayReturn - pReturn) * 100) / pReturn
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            mReturn.toReturnAsPercentage()
        } else field

    fun getReturn(x: Int = 1, y: Int = 1): Double {
        var mReturn = 0.0
        val now = fundsDetails?.tsDayEndNavDate?.toDate()?.toCalendar() ?: Calendar.getInstance()
        now.add(if (y == 1) Calendar.YEAR else Calendar.MONTH, -x)
        val date = now.time.toDate()
        val data = returnsHistory?.firstOrNull { r -> date.compareTo(r.date) == 0 }
        data?.let {
            try {
                val todayReturn = fundsDetails?.dpDayEndNav?.toDoubleOrNull()
                if (todayReturn != null) {
                    val pReturn = data.value ?: 0.0
                    mReturn = if (y == 1 && x > 1 || y == 0 && x > 12) {
                        // The below calculation is done using CAGR Formula. e.g CAGR is [(F/S) ^ (1/n)]-1, where F = Final value, S = Initial Value and n = holding period.
                        (Math.pow((todayReturn / pReturn), ((1 / if (y == 1) x.toDouble() else (x.toDouble() / 12)))) - 1) * 100
                    } else {
                        ((todayReturn - pReturn) * 100) / pReturn
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return mReturn/*.decimalFormat().toCurrency()*/
    }
}

data class FundsDetails(
        @SerializedName("amc_name")
        val amcName: String,
        @SerializedName("active")
        val active: Boolean,
        @SerializedName("amc_active_flag")
        val amcActiveFlag: String,
        @SerializedName("amc_link")
        val amcLink: String,
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
        val fnaAum: String?,
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
        val riskLevelId: String,
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
        val ttrReturn10Yr: String?,
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
        val investmentStrategy: String?,
        @SerializedName("interim_net_expense_ratio")
        val interimNetExpenseRatio: String?,
        @SerializedName("interim_net_expense_ratio_date")
        val interimNetExpenseRatioDate: String?,
        @SerializedName("net_expense_ratio")
        val netExpenseRatio: String?,
        @SerializedName("pi_minimum_initial_multiple")
        val piMinimumInitialMultiple: String?,
        @SerializedName("pi_minimum_subsequent_multiple")
        val piMinimumSubsequentMultiple: String?
) {

    var fundObjective = ""
        get() = "$investmentStrategy".replace("\n", " ")

    var NAVDate: String = ""
        get() = tsDayEndNavDate?.toDate()?.convertTo() ?: "N/A"

    var assetsDate: String = ""
        get() = fnaSurveyedFundNetAssetsDate?.toDate()?.convertTo() ?: "N/A"

    var riskProgress = 20
        get() = when {
            "Low Risk".equals(fscbiIndianRiskLevel, true) -> 20
            "Moderately Low risk".equals(fscbiIndianRiskLevel, true) -> 40
            "Medium Risk".equals(fscbiIndianRiskLevel, true) -> 60
            "Moderate Risk".equals(fscbiIndianRiskLevel, true) -> 60
            "Moderately High risk".equals(fscbiIndianRiskLevel, true) -> 80
            "High Risk".equals(fscbiIndianRiskLevel, true) -> 100
            else -> 20
        }

    val riskProgressColor
        get() = when {
            "Low Risk".equals(fscbiIndianRiskLevel, true) -> Color.parseColor("#00CB00")
            "Moderately Low risk".equals(fscbiIndianRiskLevel, true) -> Color.parseColor("#5DFE5D")
            "Medium Risk".equals(fscbiIndianRiskLevel, true) -> Color.parseColor("#FDDD18")
            "Moderate Risk".equals(fscbiIndianRiskLevel, true) -> Color.parseColor("#FDDD18")
            "Moderately High risk".equals(fscbiIndianRiskLevel, true) -> Color.parseColor("#FE860D")
            "High Risk".equals(fscbiIndianRiskLevel, true) -> Color.parseColor("#FC0000")
            else -> Color.parseColor("#00CB00")
        }

    val fscbiIndianRiskLevelName
        get() = if ("Medium Risk".equals("$fscbiIndianRiskLevel", true)) "Moderate Risk" else fscbiIndianRiskLevel

    var benchmark = ""
        get() = if (benchmarks != null && benchmarks.isNotEmpty()) {
            var name = ""
            benchmarks.forEach {
                if (name.isEmpty()) {
                    name = it.indexName
                    return@forEach
                }
                name += ", ${it.indexName}"
            }
            name
        } else ""

    var netAssets: String? = ""
        get() = fnaAum?.toDoubleOrNull()?.toCr()//?.plus(" (${fnaSurveyedFundNetAssetsDate?.toDate()?.convertTo()})")

    var fundManagers = ""
        get() = if (managers != null && managers.isNotEmpty()) {
            var name = ""
            managers.forEach {
                if (name.isEmpty()) {
                    name = it.name
                    return@forEach
                }
                name += ", ${it.name}"
            }
            name
        } else ""

    var exitLoad: String = ""
        get() = if (deferLoads != null && deferLoads.isNotEmpty()) {
            val result = StringBuilder()
            deferLoads.forEachIndexed { index, exitLoad ->
                val data = exitLoad
                if (data != null) {
                    if (exitLoad.value > 0) {
                        var intMinDays: Int = data.lowBreakpoint
                        var intMaxDays: Int = data.highBreakpoint
                        var maxDays: Int = 0
                        var minDays: Int = 0

                        if (intMaxDays != 0) {
                            maxDays = when {
                                "YEARS".equals(data.breakpointUnit, true) -> intMaxDays * 365
                                "MONTHS".equals(data.breakpointUnit, true) -> intMaxDays * 30
                                else -> intMaxDays
                            }
                        }

                        if (intMinDays != 0) {
                            minDays = when {
                                "YEARS".equals(data.breakpointUnit, true) -> intMinDays * 365
                                "MONTHS".equals(data.breakpointUnit, true) -> intMinDays * 30
                                else -> intMinDays
                            }
                        }
                        if (intMinDays != 0) {
                            result.append("${data.value.toCharges()} if redeemed between $minDays to $maxDays days")
                        } else {
                            result.append("${data.value.toCharges()} if redeemed within $maxDays days")
                        }

                        val exitLoadData = deferLoads.filter { it.value > 0 }
                        if (index != exitLoadData.size - 1) {
                            result.append("\n")
                        }
                    }
                }
            }
            if (result.isEmpty()) {
                result.append(parseToPercentageOrNA(null))
            }
            result.toString()
        } else {
            0.0.toReturnAsPercentage()
        }

    var minSIPAmount = ""
        get() {
            var sipAmount = "N/A"
            if (iaipAip != null && iaipAip.isNotEmpty()) {
                val aipAip = iaipAip.filter {
                    "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true)
                }
                val maxTenure = aipAip.maxBy { it.minTenure }
                if (maxTenure != null) {
                    sipAmount = maxTenure.minAmount?.toCurrency()?.toString() ?: "N/A"
                }
            }
            return sipAmount
        }

    var additionalSIPMultiplier: BigInteger = BigInteger.ZERO
        get() {
            var sipAmount = BigInteger.valueOf(1)
            if (iaipAip != null && iaipAip.isNotEmpty()) {
                val aipAip = iaipAip.filter {
                    "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true)
                }
                val maxTenure = aipAip.maxBy { it.minTenure }
                if (maxTenure != null) {
                    sipAmount = toBigInt(maxTenure.subsquentAmount)
                }
            }
            return sipAmount
        }

    var lumpsumAmount: String = ""
        get() = piMinimumInitial?.toDoubleOrNull()?.toCurrency() ?: "N/A"

    var expenseRatio: String? = null
        get() = parseToPercentageOrNA(interimNetExpenseRatio)

    /*var validminSIPAmount = 0.00
        get() = if (iaipAip != null && iaipAip.isNotEmpty()) {
            iaipAip.firstOrNull { it -> "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true) && (it.minTenure == 12 || it.minTenure == 6) }?.minAmount
                    ?: 0.00
        } else 0.00*/
    var validminSIPAmount = BigInteger.ZERO
        get() {
            var sipAmount = BigInteger.valueOf(100)
            if (iaipAip != null && iaipAip.isNotEmpty()) {
                val aipAip = iaipAip.filter { it ->
                    "SIP".equals(it.siType, true)
                            && "Monthly".equals(it.frequency, true)
                }
                val maxTenure = aipAip.maxBy { it.minTenure }
                if (maxTenure != null) {
                    sipAmount = maxTenure.minAmount?.toDoubleOrNull()?.toBigDecimal()?.toBigInteger()
                            ?: BigInteger.valueOf(100)
                }
            }
            return sipAmount
        }

    var validminlumpsumAmount = BigInteger.ZERO
        get() = piMinimumInitial?.toCurrencyBigInt()

    var vol = ""
        get() = parseToPercentageOrNA(standardDeviation5Yr)

    var hasNegativeReturn: Boolean = false
        get() = if (!TextUtils.isEmpty(dpDayEndNav) && !TextUtils.isEmpty(preDpDayEndNav)) {
            try {
                val result = ((dpDayEndNav.toDouble() - preDpDayEndNav.toDouble()) * 100) / preDpDayEndNav.toDouble()
                result < 0
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }

    var nav: String? = ""
        get() = dpDayEndNav.toDoubleOrNull()?.toReturn()

    var lastPrice = ""
        get() = dpDayEndNav?.toDoubleOrNull()?.toDecimalCurrency() ?: "N/A"

    var oneDayChange = ""
        get() = try {
            (((dpDayEndNav.toDouble() - preDpDayEndNav.toDouble()) * 100) / preDpDayEndNav.toDouble()).toReturnAsPercentage()
        } catch (e: java.lang.Exception) {
            "N/A"
        }

    var currentReturn: String = ""
        get() = if (!TextUtils.isEmpty(dpDayEndNav) && !TextUtils.isEmpty(preDpDayEndNav)) {
            try {
                val result: String? = "${(dpDayEndNav.toDouble() - preDpDayEndNav.toDouble()).toReturn().replace("-", "")} (${(((dpDayEndNav.toDouble() - preDpDayEndNav.toDouble()) * 100) / preDpDayEndNav.toDouble()).toReturnAsPercentage()})"
                result ?: "N/A"
            } catch (e: Exception) {
                e.printStackTrace()
                "N/A"
            }
        } else {
            "N/A"
        }

}

data class IaipAip(
        @SerializedName("frequency")
        val frequency: String,
        @SerializedName("frequency_date")
        val frequencyDate: String,
        @SerializedName("min_amount")
        val minAmount: String?,
        @SerializedName("min_tenure")
        val minTenure: Int,
        @SerializedName("si_type")
        val siType: String,
        @SerializedName("subsquent_amount")
        val subsquentAmount: String?
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
        val value: Double?
)

data class BSEData(
        @SerializedName("lumpsum_allowed")
        val lumpsumAllowed: String?,
        @SerializedName("lumpsum_check_flag")
        val lumpsumCheckFlag: Boolean?,
        @SerializedName("lumpsum_list")
        val lumpsumList: List<Lumpsum>?,
        @SerializedName("sip_allowed")
        val sipAllowed: String?,
        @SerializedName("sip_check_flag")
        val sipCheckFlag: Boolean?,
        @SerializedName("sip_list")
        val sipList: List<Sip>?
) {
    var isAdditional: Boolean? = false
    var isTarrakkiZyaada: Boolean? = false

    data class Sip(
            @SerializedName("max_amount")
            val maxAmount: BigInteger?,
            @SerializedName("min_amount")
            val minAmount: BigInteger?,
            @SerializedName("sip_additional_min_amount")
            val sipAdditionalMinAmount: BigInteger?,
            @SerializedName("sip_allowed")
            val sipAllowed: String?
    )

    data class Lumpsum(
            @SerializedName("lumpsum_additional_min_amount")
            val lumpsumAdditionalMinAmount: BigInteger?,
            @SerializedName("lumpsum_allowed")
            val lumpsumAllowed: String?,
            @SerializedName("max_amount")
            val maxAmount: BigInteger?,
            @SerializedName("min_amount")
            val minAmount: BigInteger?
    )
}