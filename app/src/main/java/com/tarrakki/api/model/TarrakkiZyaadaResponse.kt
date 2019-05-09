package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName
import org.supportcompact.ktx.toCalendar
import org.supportcompact.ktx.toCurrencyBigInt
import org.supportcompact.ktx.toDate
import java.math.BigInteger
import java.util.*

data class TarrakkiZyaadaResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("funds")
            val funds: List<Fund>?,
            @SerializedName("tarrakki_zyaada_id")
            val tarrakkiZyaadaId: Int?,
            @SerializedName("bank_savings_return")
            val bankSavingsReturn: String?,
            @SerializedName("fixed_deposit_return")
            val fixedDepositReturn: String?,
            @SerializedName("folio_list")
            val folios: List<String>?
    ) {
        data class Fund(
                @SerializedName("dp_day_end_nav")
                val dpDayEndNav: String?,
                @SerializedName("ts_day_end_nav_date")
                val tsDayEndNavDate: String?,
                @SerializedName("fscbi_legal_name")
                val fscbiLegalName: String,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("total_return_index")
                val totalReturnIndex: String?,
                @SerializedName("ttr_return_10_yr")
                val ttrReturn10Yr: String?,
                @SerializedName("ttr_return_1_yr")
                val ttrReturn1Yr: String?,
                @SerializedName("ttr_return_3_yr")
                val ttrReturn3Yr: String?,
                @SerializedName("ttr_return_5_yr")
                val ttrReturn5Yr: String?,
                @SerializedName("ttr_return_since_inception")
                val ttrReturnSinceInception: String?,
                @SerializedName("iaip_aip")
                val iaipAip: List<IaipAip>?,
                @SerializedName("pi_minimum_initial")
                val piMinimumInitial: String?
        ) {

            var validminSIPAmount = BigInteger.ZERO
                get() {
                    var sipAmount = BigInteger.valueOf(100)
                    if (iaipAip != null && iaipAip.isNotEmpty()) {
                        val aipAip = iaipAip.filter {
                            "SIP".equals(it.siType, true) && "Monthly".equals(it.frequency, true)
                        }
                        val maxTenure = aipAip.maxBy { it.minTenure }
                        if (maxTenure != null) {
                            sipAmount = maxTenure.minAmount.toString().toCurrencyBigInt()
                        }
                    }
                    return sipAmount
                }

            var validminlumpsumAmount = BigInteger.ZERO
                get() = piMinimumInitial?.toCurrencyBigInt()

            var returnsHistory: ArrayList<ReturnsHistory>? = null
                get() = if (field == null) {
                    field = arrayListOf()
                    totalReturnIndex?.let {
                        val listType = object : com.google.gson.reflect.TypeToken<java.util.ArrayList<com.tarrakki.api.model.ReturnsHistory>>() {}.type
                        val gsonBuilder = com.google.gson.GsonBuilder()
                        gsonBuilder.registerTypeAdapter(java.util.Date::class.java, object : com.google.gson.JsonDeserializer<java.util.Date> {
                            var df: java.text.DateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH)
                            @kotlin.jvm.Throws(com.google.gson.JsonParseException::class)
                            override fun deserialize(json: com.google.gson.JsonElement, typeOfT: java.lang.reflect.Type, context: com.google.gson.JsonDeserializationContext): java.util.Date? {
                                return try {
                                    df.parse(json.asString)
                                } catch (ex: java.lang.Exception) {
                                    java.util.Date()
                                }
                            }
                        })
                        val dateGson = gsonBuilder.create()
                        val data = dateGson.fromJson<java.util.ArrayList<com.tarrakki.api.model.ReturnsHistory>>(it, listType)
                        data?.let { all ->
                            field?.addAll(all)
                        }
                    }
                    field
                } else {
                    field
                }

            fun getReturn(x: Int = 1, y: Int = 1): Double {
                var mReturn = 0.0
                val now = tsDayEndNavDate?.toDate()?.toCalendar() ?: Calendar.getInstance()
                now.add(if (y == 1) Calendar.YEAR else Calendar.MONTH, -x)
                val date = now.time.toDate()
                val data = returnsHistory?.firstOrNull { r -> date.compareTo(r.date) == 0 }
                data?.let {
                    try {
                        val todayReturn = dpDayEndNav?.toDoubleOrNull()
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
    }
}