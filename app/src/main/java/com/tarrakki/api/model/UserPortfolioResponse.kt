package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName
import org.supportcompact.ktx.convertTo
import org.supportcompact.ktx.toCurrencyBigInt
import org.supportcompact.ktx.toDate
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
                val currentValue: BigInteger,
                @SerializedName("funds")
                val funds: List<Fund>,
                @SerializedName("goal_id")
                val goalId: Int,
                @SerializedName("goal_name")
                val goalName: String,
                @SerializedName("total_investment")
                val totalInvestment: BigInteger,
                @SerializedName("xirr")
                val xirr: BigInteger
        ) {
            data class Fund(
                    @SerializedName("current_value")
                    val currentValue: BigInteger,
                    @SerializedName("folio_list")
                    val folioList: List<Folio>,
                    @SerializedName("fund_id")
                    val fundId: Int,
                    @SerializedName("fund_name")
                    val fundName: String,
                    @SerializedName("total_investment")
                    val totalInvestment: BigInteger,
                    @SerializedName("xirr")
                    val xirr: BigInteger,
                    @SerializedName("pi_minimum_initial")
                    val piMinimumInitial: String?,
                    @SerializedName("iaip_aip")
                    val iaipAip: List<IaipAip>?,
                    @SerializedName("is_sip")
                    val isSIP: Boolean
            ) {
                data class Folio(
                        @SerializedName("amount")
                        val amount: String,
                        @SerializedName("folio_no")
                        val folioNo: String,
                        @SerializedName("sip_details")
                        val sipDetails: List<SipDetail>
                ) {
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
                val currentValue: BigInteger,
                @SerializedName("folio_list")
                val folioList: List<Folio>,
                @SerializedName("fund_id")
                val fundId: Int,
                @SerializedName("fund_name")
                val fundName: String,
                @SerializedName("total_investment")
                val totalInvestment: BigInteger,
                @SerializedName("xirr")
                val xirr: BigInteger,
                @SerializedName("pi_minimum_initial")
                val piMinimumInitial: String?,
                @SerializedName("iaip_aip")
                val iaipAip: List<IaipAip>?,
                @SerializedName("is_sip")
                val isSIP: Boolean
        ) {
            data class Folio(
                    @SerializedName("amount")
                    val amount: String,
                    @SerializedName("folio_no")
                    val folioNo: String,
                    @SerializedName("sip_details")
                    val sipDetails: List<SipDetail>
            ) {
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
        val amount: String,
        val folioNo: String,
        val sipDetails: List<SIPDetails>? = null
)

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