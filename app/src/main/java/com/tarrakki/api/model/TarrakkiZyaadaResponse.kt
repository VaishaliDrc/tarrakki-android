package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class TarrakkiZyaadaResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("funds")
            val funds: List<Fund>?,
            @SerializedName("tarrakki_zyaada_id")
            val tarrakkiZyaadaId: Int
    ) {
        data class Fund(
                @SerializedName("dp_day_end_nav")
                val dpDayEndNav: String?,
                @SerializedName("fscbi_legal_name")
                val fscbiLegalName: String,
                @SerializedName("id")
                val id: Int,
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
                val ttrReturnSinceInception: String?
        ) {

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

        }
    }
}