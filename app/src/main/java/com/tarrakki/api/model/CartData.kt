package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.tarrakki.BR
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
                val lumpsumAmount: Int,
                @SerializedName("order_id_id")
                val orderIdId: Int,
                @SerializedName("sip_amount")
                val sipAmount: Int,
                @SerializedName("start_date")
                var startDate: String
        ) : BaseObservable(), Serializable {
            @get:Bindable
            var hasOneTimeAmount: Boolean = false
                get() = lumpsumAmount > 0
                set(value) {
                    field = value
                    notifyPropertyChanged(BR.hasOneTimeAmount)
                }

            @get:Bindable
            var date: String? = startDate
                get() = startDate.toDate().convertTo()
                set(value) {
                    field = value
                    notifyPropertyChanged(BR.date)
                }
        }

    }
}