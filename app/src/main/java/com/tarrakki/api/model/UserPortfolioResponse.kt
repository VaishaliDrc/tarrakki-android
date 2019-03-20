package com.tarrakki.api.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.supportcompact.ktx.*
import java.math.BigInteger

data class UserPortfolioResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("direct_investment")
            val directInvestment: List<DirectInvestment>,
            @SerializedName("goal_based_investment")
            val goalBasedInvestment: List<GoalBasedInvestment>
    ) {
        data class GoalBasedInvestment(
                @SerializedName("current_value")
                val currentValue: Double,
                @SerializedName("funds")
                val funds: List<Fund>,
                @SerializedName("goal_id")
                val goalId: Int,
                @SerializedName("goal_name")
                val goalName: String,
                @SerializedName("total_investment")
                val totalInvestment: BigInteger,
                @SerializedName("xirr")
                val xirr: String
        ) {
            var xirrLabel: String = "Abs.:"
                get() = "Return:"

            var xiRR: String = ""
                get() = parseAsReturn(xirr)

            data class Fund(
                    @SerializedName("current_value")
                    val currentValue: Double,
                    @SerializedName("folio_list")
                    val folioList: List<Folio>,
                    @SerializedName("fund_id")
                    val fundId: Int,
                    @SerializedName("fund_name")
                    val fundName: String,
                    @SerializedName("total_investment")
                    val totalInvestment: BigInteger,
                    @SerializedName("xirr")
                    val xirr: String,
                    @SerializedName("pi_minimum_initial")
                    val piMinimumInitial: String?,
                    @SerializedName("iaip_aip")
                    val iaipAip: List<IaipAip>?,
                    @SerializedName("is_sip")
                    val isSIP: Boolean,
                    @SerializedName("dp_day_end_nav")
                    val todayNAV: String?,
                    @SerializedName("defer_loads")
                    val deferLoads: List<ExitLoad>?
            ) {

                var bank: DefaultBankResponse.DefaultBank? = null

                var redeemRequest: JsonObject? = null

                var redeemUnits: String? = null

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
                                        result.append("${data.value.toReturnAsPercentage()} if redeemed between $minDays to $maxDays days")
                                    } else {
                                        result.append("${data.value.toReturnAsPercentage()} if redeemed within $maxDays days")
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

                val nav
                    get() = todayNAV?.toDoubleOrNull() ?: 0.0

                var isMoreFolioList = false
                    get() = folioList.size > 1

                var xirrLabel: String = "Return:"
                    get() = "Return:"

                var xiRR: String = ""
                    get() = parseAsReturn(xirr)

                data class Folio(
                        @SerializedName("current_value")
                        val currentValue: Double,
                        @SerializedName("amount")
                        val amount: String,
                        @SerializedName("folio_no")
                        val folioNo: String,
                        @SerializedName("sip_details")
                        val sipDetails: List<SipDetail>,
                        @SerializedName("xirr")
                        val xirr: String
                ) {
                    var xiRR: String = ""
                        get() = parseAsReturn(xirr)

                    data class SipDetail(
                            @SerializedName("amount")
                            val amount: String?,
                            @SerializedName("start_date")
                            val startDate: String?,
                            @SerializedName("trans_id")
                            val transId: Int
                    )
                }

                var folioNoList: String = ""
                    get() = if (folioList.isNotEmpty()) {
                        val string = StringBuilder()
                        folioList.forEachIndexed { index, folio ->
                            string.append(folio.folioNo)
                            if (index != folioList.size - 1) {
                                string.append(", ")
                            }
                        }
                        string.toString()
                    } else {
                        ""
                    }

                var validminSIPAmount = BigInteger.ZERO
                    get() {
                        var sipAmount = BigInteger.valueOf(100)
                        if (iaipAip != null && iaipAip.isNotEmpty()) {
                            val aipAip = iaipAip.firstOrNull { it ->
                                "SIP".equals(it.siType, true)
                                        && "Monthly".equals(it.frequency, true)
                            }
                            if (aipAip != null) {
                                val maxTenure = iaipAip.maxBy { it.minTenure }
                                if (maxTenure != null) {
                                    sipAmount = maxTenure.minAmount?.toBigDecimal()?.toBigInteger()
                                }
                            }
                        }
                        return sipAmount
                    }

                var validminlumpsumAmount = BigInteger.ZERO
                    get() = piMinimumInitial?.toCurrencyBigInt()
            }
        }

        data class DirectInvestment(
                @SerializedName("current_value")
                val currentValue: Double,
                @SerializedName("folio_list")
                val folioList: List<Folio>,
                @SerializedName("fund_id")
                val fundId: Int,
                @SerializedName("fund_name")
                val fundName: String,
                @SerializedName("total_investment")
                val totalInvestment: BigInteger,
                @SerializedName("xirr")
                val xirr: String,
                @SerializedName("pi_minimum_initial")
                val piMinimumInitial: String?,
                @SerializedName("iaip_aip")
                val iaipAip: List<IaipAip>?,
                @SerializedName("is_sip")
                val isSIP: Boolean,
                @SerializedName("dp_day_end_nav")
                val todayNAV: String?,
                @SerializedName("defer_loads")
                val deferLoads: List<ExitLoad>?
        ) {

            var redeemRequest: JsonObject? = null

            var redeemUnits: String? = null

            var bank: DefaultBankResponse.DefaultBank? = null

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
                                    result.append("${data.value.toReturnAsPercentage()} if redeemed between $minDays to $maxDays days")
                                } else {
                                    result.append("${data.value.toReturnAsPercentage()} if redeemed within $maxDays days")
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

            val nav
                get() = todayNAV?.toDoubleOrNull() ?: 0.0

            var isMoreFolioList = false
                get() = folioList.size > 1
            var xirrLabel: String = "Return:"
                get() = "Return:"

            var xiRR: String = ""
                get() = parseAsReturn(xirr)

            data class Folio(
                    @SerializedName("current_value")
                    val currentValue: Double,
                    @SerializedName("amount")
                    val amount: String,
                    @SerializedName("folio_no")
                    val folioNo: String,
                    @SerializedName("sip_details")
                    val sipDetails: List<SipDetail>,
                    @SerializedName("xirr")
                    val xirr: String
            ) {
                var xiRR: String = ""
                    get() = parseAsReturn(xirr)


                data class SipDetail(
                        @SerializedName("amount")
                        val amount: String?,
                        @SerializedName("start_date")
                        val startDate: String?,
                        @SerializedName("trans_id")
                        val transId: Int
                )
            }

            var folioNoList: String = ""
                get() = if (folioList.isNotEmpty()) {
                    val string = StringBuilder()
                    folioList.forEachIndexed { index, folio ->
                        string.append(folio.folioNo)
                        if (index != folioList.size - 1) {
                            string.append(", ")
                        }
                    }
                    string.toString()
                } else {
                    ""
                }

            var validminSIPAmount = BigInteger.ZERO
                get() {
                    var sipAmount = BigInteger.valueOf(100)
                    if (iaipAip != null && iaipAip.isNotEmpty()) {
                        val aipAip = iaipAip.firstOrNull { it ->
                            "SIP".equals(it.siType, true)
                                    && "Monthly".equals(it.frequency, true)
                        }
                        if (aipAip != null) {
                            val maxTenure = iaipAip.maxBy { it.minTenure }
                            if (maxTenure != null) {
                                sipAmount = maxTenure.minAmount?.toBigDecimal()?.toBigInteger()
                            }
                        }
                    }
                    return sipAmount
                }

            var validminlumpsumAmount = BigInteger.ZERO
                get() = piMinimumInitial?.toCurrencyBigInt()
        }
    }
}

data class FolioData(
        val currentValue: Double,
        val amount: String,
        val folioNo: String,
        val sipDetails: List<SIPDetails>? = null
) {
    var cValue: String = ""
        get() {
            var value = currentValue
            if (value == null) {
                value = 0.0
            }
            return value.toString()
        }
}

data class SIPDetails(
        @SerializedName("amount")
        val amount: String?,
        @SerializedName("start_date")
        val startDate: String?,
        @SerializedName("trans_id")
        val transId: Int
) {
    var convertedDate = ""
        get() = startDate?.toDate()?.convertTo().toString()

    override fun toString(): String {
        return convertedDate
    }
}