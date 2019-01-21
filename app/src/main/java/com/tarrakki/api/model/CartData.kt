package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.tarrakki.BR
import com.tarrakki.getOrdinalFormat
import org.supportcompact.ktx.convertTo
import org.supportcompact.ktx.toDate
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
                @SerializedName("start_date")
                var startDate: String?,
                @SerializedName("iaip_aip")
                val iaipAip: List<IaipAip>?
        ) : BaseObservable(), Serializable {

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
            var date: String? = startDate?.toDate()?.convertTo()
                get() = if (field == null) startDate?.toDate()?.convertTo() else field
                set(value) {
                    field = value
                    notifyPropertyChanged(BR.date)
                }

            var frequencyDate = arrayListOf<String>()
                get() {
                    val dateList = arrayListOf<String>()
                    if (iaipAip != null && iaipAip.isNotEmpty()) {
                        val aipData = iaipAip.firstOrNull {
                            "SIP".equals(it.siType, true)
                                    && "Monthly".equals(it.frequency, true)
                                    && it.minTenure == 6
                        }
                        if (aipData != null) {
                            val dates = aipData.frequencyDate.split("|")
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
                    }
                    return dateList
                }

            fun getDummyDates(): ArrayList<String> {
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

            /*fun getDateFormat(date : String) : String{
                val hunRem = date.toInt() % 100
                val tenRem = date.toInt() % 10

                if (hunRem - tenRem == 10) {
                    return date+"th"
                }
                when (tenRem) {
                    1 -> return date+"st"
                    2 -> return date+"nd"
                    3 -> return date+"rd"
                    else -> return date+"th"
                }
            }*/

        }

    }


}