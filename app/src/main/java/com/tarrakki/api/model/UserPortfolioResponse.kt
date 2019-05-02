package com.tarrakki.api.model

import android.view.View
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
            val goalBasedInvestment: List<GoalBasedInvestment>,
            @SerializedName("tarrakki_zyaada_investments")
            val tarrakkiZyaadaInvestment: List<TarrakkiZyaadaInvestment>
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
                val totalInvestment: Double?,
                @SerializedName("xirr")
                val xirr: String
        ) {
            var xirrLabel: String = "Abs.:"
                get() = "Return:"

            var xiRR: String = ""
                get() = parseAsReturn(xirr)

            data class Fund(
                    @SerializedName("insta_redeem")
                    val instaRedeem: Boolean?,
                    @SerializedName("current_value")
                    val currentValue: Double?,
                    @SerializedName("folio_list")
                    val folioList: List<Folio>,
                    @SerializedName("fund_id")
                    val fundId: Int,
                    @SerializedName("fund_name")
                    val fundName: String,
                    @SerializedName("total_investment")
                    val totalInvestment: Double?,
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
                    val deferLoads: List<ExitLoad>?,
                    @SerializedName("units")
                    val totalUnits: Double?,
                    @SerializedName("reliance_debit_fund")
                    val relianceDebitFund: Boolean?
            ) {

                var redeemedStatus: RedeemedStatus? = null

                val applyForDebitCartBtnVisibility
                    get() = if (relianceDebitFund == true) View.VISIBLE else View.GONE

                var bank: DefaultBankResponse.DefaultBank? = null

                var folioData: ArrayList<FolioData>? = null
                    get() = if (field == null) {
                        field = arrayListOf()
                        folioList?.forEach { folio ->
                            if (folio.isApplyDebitCard == false) {
                                field?.add(FolioData(folio.folioId, folio.currentValue, folio.units, folio.folioNo))
                            }
                        }
                        field
                    } else {
                        field
                    }

                var redeemRequest: JsonObject? = null

                var redeemUnits: String? = null

                var isInstaRedeem = false


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
                        @SerializedName("folio_id")
                        val folioId: String?,
                        @SerializedName("current_value")
                        val currentValue: Double?,
                        @SerializedName("amount")
                        val amount: String,
                        @SerializedName("total_investment")
                        val totalInvestment: Double?,
                        @SerializedName("units")
                        val units: String?,
                        @SerializedName("folio_no")
                        val folioNo: String,
                        @SerializedName("sip_details")
                        val sipDetails: List<SipDetail>,
                        @SerializedName("xirr")
                        val xirr: String?,
                        @SerializedName("is_apply_debit_card")
                        val isApplyDebitCard: Boolean?
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
                            val aipAip = iaipAip.filter {
                                "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true)
                            }
                            val maxTenure = aipAip.maxBy { it.minTenure }
                            if (maxTenure != null) {
                                sipAmount = maxTenure.minAmount?.toBigDecimal()?.toBigInteger()
                            }
                        }
                        return sipAmount
                    }

                var validminlumpsumAmount = BigInteger.ZERO
                    get() = piMinimumInitial?.toCurrencyBigInt()
            }
        }

        data class TarrakkiZyaadaInvestment(
                @SerializedName("current_value")
                val currentValue: Double?,
                @SerializedName("folio_list")
                val folioList: List<Folio>,
                @SerializedName("fund_id")
                val fundId: Int,
                @SerializedName("fund_name")
                val fundName: String,
                @SerializedName("total_investment")
                val totalInvestment: Double?,
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
                val deferLoads: List<ExitLoad>?,
                @SerializedName("units")
                val totalUnits: Double?,
                @SerializedName("tz_id")
                val tzId: String?,
                @SerializedName("reliance_debit_fund")
                val relianceDebitFund: Boolean?
        ) {

            var redeemedStatus: RedeemedStatus? = null

            val applyForDebitCartBtnVisibility
                get() = if (relianceDebitFund == true) View.VISIBLE else View.GONE

            var redeemRequest: JsonObject? = null

            var redeemUnits: String? = null

            var isInstaRedeem = false

            var bank: DefaultBankResponse.DefaultBank? = null

            var folioData: ArrayList<FolioData>? = null
                get() = if (field == null) {
                    field = arrayListOf()
                    folioList?.forEach { folio ->
                        if (folio.isApplyDebitCard == false) {
                            field?.add(FolioData(folio.folioId, folio.currentValue, folio.units, folio.folioNo))
                        }
                    }
                    field
                } else {
                    field
                }

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
                    @SerializedName("folio_id")
                    val folioId: String?,
                    @SerializedName("current_value")
                    val currentValue: Double?,
                    @SerializedName("amount")
                    val amount: String,
                    @SerializedName("total_investment")
                    val totalInvestment: Double?,
                    @SerializedName("units")
                    val units: String?,
                    @SerializedName("folio_no")
                    val folioNo: String,
                    @SerializedName("sip_details")
                    val sipDetails: List<SipDetail>,
                    @SerializedName("xirr")
                    val xirr: String?,
                    @SerializedName("is_apply_debit_card")
                    val isApplyDebitCard: Boolean?
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
                        val aipAip = iaipAip.filter {
                            "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true)
                        }
                        val maxTenure = aipAip.maxBy { it.minTenure }
                        if (maxTenure != null) {
                            sipAmount = maxTenure.minAmount?.toBigDecimal()?.toBigInteger()
                        }
                    }
                    return sipAmount
                }

            var validminlumpsumAmount = BigInteger.ZERO
                get() = piMinimumInitial?.toCurrencyBigInt()
        }


        data class DirectInvestment(
                @SerializedName("insta_redeem")
                val instaRedeem: Boolean?,
                @SerializedName("current_value")
                val currentValue: Double?,
                @SerializedName("folio_list")
                val folioList: List<Folio>,
                @SerializedName("fund_id")
                val fundId: Int,
                @SerializedName("fund_name")
                val fundName: String,
                @SerializedName("total_investment")
                val totalInvestment: Double?,
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
                val deferLoads: List<ExitLoad>?,
                @SerializedName("units")
                val totalUnits: Double?,
                @SerializedName("reliance_debit_fund")
                val relianceDebitFund: Boolean?
        ) {

            var redeemedStatus: RedeemedStatus? = null

            val applyForDebitCartBtnVisibility
                get() = if (relianceDebitFund == true) View.VISIBLE else View.GONE

            var redeemRequest: JsonObject? = null

            var redeemUnits: String? = null

            var isInstaRedeem = false

            var bank: DefaultBankResponse.DefaultBank? = null

            var folioData: ArrayList<FolioData>? = null
                get() = if (field == null) {
                    field = arrayListOf()
                    folioList?.forEach { folio ->
                        if (folio.isApplyDebitCard == false) {
                            field?.add(FolioData(folio.folioId, folio.currentValue, folio.units, folio.folioNo))
                        }
                    }
                    field
                } else {
                    field
                }

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
                    @SerializedName("folio_id")
                    val folioId: String?,
                    @SerializedName("current_value")
                    val currentValue: Double?,
                    @SerializedName("total_investment")
                    val totalInvestment: Double?,
                    @SerializedName("amount")
                    val amount: String?,
                    @SerializedName("units")
                    val units: String?,
                    @SerializedName("folio_no")
                    val folioNo: String,
                    @SerializedName("sip_details")
                    val sipDetails: List<SipDetail>,
                    @SerializedName("xirr")
                    val xirr: String?,
                    @SerializedName("is_apply_debit_card")
                    val isApplyDebitCard: Boolean?
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
                        val aipAip = iaipAip.filter {
                            "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true)
                        }
                        val maxTenure = aipAip.maxBy { it.minTenure }
                        if (maxTenure != null) {
                            sipAmount = maxTenure.minAmount?.toBigDecimal()?.toBigInteger()
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
        val folioId: String?,
        val currentValue: Double?,
        val units: String?,
        val folioNo: String?,
        val sipDetails: List<SIPDetails>? = null
) {
    val amount: String? = null
    var cValue: String = ""
        get() {
            var value = currentValue
            if (value == null) {
                value = 0.0
            }
            return value.toString()
        }

    override fun toString(): String {
        return folioNo ?: ""
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