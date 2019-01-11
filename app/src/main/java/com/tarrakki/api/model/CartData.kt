package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

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
                val lumpsumAmount: Int,
                @SerializedName("order_id_id")
                val orderIdId: Int,
                @SerializedName("sip_amount")
                val sipAmount: Int,
                @SerializedName("start_date")
                var startDate: String
        )
    }
}