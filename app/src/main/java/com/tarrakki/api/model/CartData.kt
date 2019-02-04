package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.tarrakki.BR
import com.tarrakki.getOrdinalFormat
import java.io.Serializable

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
                @SerializedName("lumpsum_amount")
                var lumpsumAmount: String,
                @SerializedName("order_id_id")
                val orderIdId: Int,
                @SerializedName("sip_amount")
                var sipAmount: String,
                @SerializedName("day")
                var day: String?,
                @SerializedName("iaip_aip")
                val iaipAip: List<IaipAip>?,
                @SerializedName("pi_minimum_initial")
                val piMinimumInitial: String?,
                @SerializedName("goal")
                val goal: Goal
        ) : BaseObservable(), Serializable {
            var validminSIPAmount = 0
                get() {
                    var sipAmount = 100
                    if (iaipAip != null && iaipAip.isNotEmpty()) {
                        val aipAip = iaipAip.firstOrNull { it ->
                            "SIP".equals(it.siType, true)
                                    && "Monthly".equals(it.frequency, true)
                        }
                        if (aipAip != null) {
                            val maxTenure = iaipAip.maxBy { it.minTenure }
                            if (maxTenure != null) {
                                sipAmount = maxTenure.minAmount ?: 0
                            }
                        }
                    }
                    return sipAmount
                }

            var validminlumpsumAmount = 0
                get() = piMinimumInitial?.toInt() ?: 0

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

            var frequencyDate = arrayListOf<String>()
                get() {
                    val dateList = arrayListOf<String>()
                    if (iaipAip != null && iaipAip.isNotEmpty()) {
                        val aipData12M = iaipAip.firstOrNull {
                            "SIP".equals(it.siType, true)
                                    && "Monthly".equals(it.frequency, true)
                                    && it.minTenure == 12
                        }
                        val aipData6M = iaipAip.firstOrNull {
                            "SIP".equals(it.siType, true)
                                    && "Monthly".equals(it.frequency, true)
                                    && it.minTenure == 6
                        }
                        if (aipData12M != null) {
                            val dates = aipData12M.frequencyDate.split("|")
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
                        } else if (aipData6M != null) {
                            val dates = aipData6M.frequencyDate.split("|")
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
                for (date in 1..31) {
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
            val subsquentAmount: Int
    )

    data class Goal(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("goal")
            val goal: String
    )
}