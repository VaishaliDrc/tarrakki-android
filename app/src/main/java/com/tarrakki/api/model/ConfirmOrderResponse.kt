package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.text.TextUtils
import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.BR
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.ktx.convertTo
import org.supportcompact.ktx.toCurrency
import org.supportcompact.ktx.toDate

data class ConfirmOrderResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("id")
            val id: Int,
            @SerializedName("mandate_id")
            val mandateId: Int?,
            @SerializedName("bank_name")
            val bankName: String?,
            @SerializedName("order_lines")
            val orderLines: ArrayList<OrderLine>,
            @SerializedName("total_lumpsum")
            val totalLumpsum: Double,
            @SerializedName("total_sip")
            val totalSip: Double,
            @SerializedName("user_id")
            val userId: Int
    ) {
        data class OrderLine(
                @SerializedName("fund_id__fscbi_legal_name")
                val name: String,
                @SerializedName("fund_id__id")
                val fundIdId: Int,
                @SerializedName("fund_id__scheme_type")
                val fundIdSchemeType: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("lumpsum_order_state")
                val lumpsumOrderState: String,
                @SerializedName("order_id_id")
                val orderIdId: Int,
                @SerializedName("sip_order_state")
                val sipOrderState: String
        ) : BaseObservable(), WidgetsViewModel {

            @SerializedName("lumpsum_amount")
            var lumpsumAmount: String? = null
                get() = field?.toDoubleOrNull()?.toCurrency()

            @SerializedName("sip_amount")
            var sipAmount: String? = null
                get() = field?.toDoubleOrNull()?.toCurrency()

            var isGuideline : Boolean? = false
                get() {
                    if (hasSIP == View.VISIBLE){
                        if (hasLumpsum == View.VISIBLE){
                            return true
                        }else{
                            return false
                        }
                    }else{
                        return false
                    }
                }

            val hasSIP
                get() = if (TextUtils.isEmpty(sipAmount) || "₹0" == sipAmount) View.GONE else View.VISIBLE

            val hasLumpsum
                get() = if (TextUtils.isEmpty(lumpsumAmount) || "₹0" == lumpsumAmount) View.GONE else View.VISIBLE

            val haveSIPAndLumpsum: Int
                get() = if (hasSIP == View.VISIBLE && hasLumpsum == View.VISIBLE) View.VISIBLE else View.GONE

            val title: Int
                get() = if (hasSIP == View.VISIBLE) R.string.sip else R.string.lumpsum

            val amount: String?
                get() = if (hasSIP == View.VISIBLE) sipAmount else lumpsumAmount

            @SerializedName("first_order_today")
            @get:Bindable
            var isFirstInstallmentSIP: Boolean = false
                set(value) {
                    field = value
                    notifyPropertyChanged(BR.firstInstallmentSIP)
                }

            @SerializedName("start_date")
            val startDate: String? = null
                get() = field?.toDate()?.convertTo()

            override fun layoutId(): Int {
                return R.layout.row_confirm_order
            }

        }
    }
}