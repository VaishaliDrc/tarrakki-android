package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.tarrakki.BR
import com.tarrakki.getOrdinalFormat
import org.supportcompact.ktx.toCurrency
import org.supportcompact.ktx.toCurrencyBigInt
import java.io.Serializable
import java.math.BigInteger

data class CartData(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("order_lines")
            val orderLines: List<OrderLine>,
            @SerializedName("total_lumpsum")
            val totalLumpsum: Double?,
            @SerializedName("total_sip")
            val totalSip: Double?,
            @SerializedName("user_id")
            val userId: Int
    ) {
        data class OrderLine(
                @SerializedName("fund_id__fscbi_legal_name")
                val fundIdFscbiLegalName: String,
                @SerializedName("fund_id__id")
                val fundIdId: Int,
                @SerializedName("fund_id__scheme_type")
                val fundIdSchemeType: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("order_id_id")
                val orderIdId: Int,
                @SerializedName("day")
                var day: String?,
                @SerializedName("iaip_aip")
                val iaipAip: List<IaipAip>?,
                @SerializedName("pi_minimum_initial")
                val piMinimumInitial: String?,
                @SerializedName("goal")
                val goal: Goal,
                @SerializedName("tarrakki_zyaada")
                val tarrakkiZyaada: TarrakkiZyaada?,
                @SerializedName("folio_number")
                val folioNumber: String,
                @SerializedName("bse_data")
                val bseData: BSEData?,
                @SerializedName("lumpsum_additional_min_amount")
                val lumpsumAdditionalMinAmount: String?

        ) : BaseObservable(), Serializable {

            var additionalSIPAmount: BigInteger = BigInteger.ZERO
                get() {
                    var sipAmount = BigInteger.valueOf(100)
                    if (iaipAip != null && iaipAip.isNotEmpty()) {
                        val aipAip = iaipAip.filter {
                            "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true)
                        }
                        val maxTenure = aipAip.maxBy { it.minTenure }
                        if (maxTenure != null) {
                            sipAmount = maxTenure.subsquentAmount?.toCurrencyBigInt()
                                    ?: BigInteger.valueOf(1)
                        }
                    }
                    return sipAmount
                }

            val additionalMinLumpsum: BigInteger
                get() = lumpsumAdditionalMinAmount?.toCurrencyBigInt() ?: BigInteger.ZERO

            @SerializedName("lumpsum_amount")
            var lumpsumAmount: String = ""
                get() = "$field".toCurrency().toCurrency()

            @SerializedName("sip_amount")
            var sipAmount: String = ""
                get() = "$field".toCurrency().toCurrency()

            /*var lumpsum: String = ""
                get() = "$lumpsumAmount".toCurrency().format()

            var sip: String = ""
                get() = "$sipAmount".toCurrency().format()*/

            var actualfolioNumber: String = ""
                get() = if (!TextUtils.isEmpty(folioNumber)) {
                    folioNumber
                } else {
                    "NEW"
                }

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
                            sipAmount = maxTenure.minAmount?.toBigInteger() ?: BigInteger.ZERO
                        }
                    }
                    return sipAmount
                }

            var validminlumpsumAmount = BigInteger.ZERO
                get() = piMinimumInitial?.toBigInteger() ?: BigInteger.ZERO

            @get:Bindable
            var hasOneTimeAmount: Boolean = false
                //get() = lumpsumAmount > "0".toInt().toString()
                set(value) {
                    field = value
                    /*field = try {
                        val num = lumpsumAmount.toDoubleOrNull() ?: 0.0
                        num > 0
                    } catch (e: java.lang.Exception) {
                        value
                    }*/
                    notifyPropertyChanged(BR.hasOneTimeAmount)
                }

            @get:Bindable
            var reuestToEdit: Boolean = false
                set(value) {
                    field = value
                    notifyPropertyChanged(BR.reuestToEdit)
                }

            var frequencyDate = arrayListOf<String>()
                get() {
                    val dateList = arrayListOf<String>()
                    if (iaipAip != null && iaipAip.isNotEmpty()) {
                        val aipAipTemp = iaipAip.filter {
                            "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true)
                        }
                        val maxTenure = aipAipTemp.maxBy { it.minTenure }
                        /*val aipData12M = iaipAip.firstOrNull {
                            "SIP".equals(it.siType, true)
                                    && "Monthly".equals(it.frequency, true)
                                    && it.minTenure == 12
                        }
                        val aipData6M = iaipAip.firstOrNull {
                            "SIP".equals(it.siType, true)
                                    && "Monthly".equals(it.frequency, true)
                                    && it.minTenure == 6
                        }*/
                        if (maxTenure != null) {
                            val dates = maxTenure.frequencyDate.split("|")
                            val isDay = dates.find { it.contains("day", false) }
                            if (isDay != null) {
                                dateList.addAll(getDummyDates())
                            } else {
                                try {
                                    for (date in dates) {
                                        dateList.add(getOrdinalFormat(date.toInt()))
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        } else {
                            dateList.addAll(getDummyDates())
                        }
                    } else {
                        dateList.addAll(getDummyDates())
                    }
                    return dateList
                }

            private fun getDummyDates(): ArrayList<String> {
                val dateList = arrayListOf<String>()
                val dates = arrayListOf<String>()
                for (date in 1..28) {
                    dates.add(date.toString())
                }
                try {
                    for (date in dates) {
                        dateList.add(getOrdinalFormat(date.toInt()))
                    }
                } catch (e: Exception) {

                }
                return dateList
            }

        }

    }

    data class IaipAip(
            @SerializedName("frequency")
            val frequency: String,
            @SerializedName("frequency_date")
            val frequencyDate: String,
            @SerializedName("min_amount")
            val minAmount: Int?,
            @SerializedName("min_tenure")
            val minTenure: Int,
            @SerializedName("si_type")
            val siType: String,
            @SerializedName("subsquent_amount")
            val subsquentAmount: String?
    )

    data class Goal(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("goal")
            val goal: String
    )

    data class TarrakkiZyaada(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String
    )
}