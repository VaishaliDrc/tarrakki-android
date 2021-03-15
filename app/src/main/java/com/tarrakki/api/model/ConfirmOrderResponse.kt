package com.tarrakki.api.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import android.text.TextUtils
import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.App
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
            val mandateId: String?,
            @SerializedName("bank_name")
            val bank: String?,
            @SerializedName("order_lines")
            val orderLines: ArrayList<OrderLine>,
            @SerializedName("total_lumpsum")
            val totalLumpsum: Double,
            @SerializedName("total_sip")
            val totalSip: Double,
            @SerializedName("user_id")
            val userId: Int,
            @SerializedName("user_trials")
            val userTrials: Int,
            @SerializedName("mandate_status")
            val mandateStatus: String,
            @SerializedName("is_sip")
            val isSIP: Boolean,
            @SerializedName("is_tarrakki_pro")
            val isTarrakkiPro: Boolean,
            @SerializedName("mandate_type")
            val typeOfMandate: String?,
            @SerializedName("pro_price")
            val proPrice: String?

    ) {

        val bankName: String?
            get() = "$mandateType-".plus(bank ?: "")

        val mandateType: String?
            get() = if ("X".equals(typeOfMandate, true)) {
                App.INSTANCE.getString(R.string.nach_mandate)
            } else if("N".equals(typeOfMandate,true)){
                App.INSTANCE.getString(R.string.e_nach_mandate)
            }else if("I".equals(typeOfMandate,true)){
                App.INSTANCE.getString(R.string.sip_mandate)
            }else ""

        val isApproveBank: Boolean?
            get() = if ("APPROVED" == mandateStatus) {
                true
            } else {
                false
            }

        data class OrderLine(
                @SerializedName("fund_id__fscbi_legal_name")
                val name: String,
                @SerializedName("fund_id__id")
                val fundIdId: Int,
                @SerializedName("fund_id__scheme_type")
                val fundIdSchemeType: String,
                @SerializedName("prime_rating")
                val primeRatingData: PrimeRating,
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

            var isGuideline: Boolean? = false
                get() {
                    if (hasSIP == View.VISIBLE) {
                        if (hasLumpsum == View.VISIBLE) {
                            return true
                        } else {
                            return false
                        }
                    } else {
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
                get() = if (hasLumpsum == View.VISIBLE) R.string.lumpsum else R.string.sip

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

        data class PrimeRating(
                @SerializedName("prime_rating")
                val primeRating: String,
                @SerializedName("prime_review")
                val primeReview: String,
        )/*{
            val rating = primeRating?.toFloatOrNull()

            val isRatingEmptyVisible : Boolean
                get() = rating == null|| rating == 0.0f

            val ratingEmptyText : String get() =
                    if(rating == null|| rating == 0.0f) primeRating else ""
//            val isRatingBarVisible : Boolean
//                get() = !(rating == null|| rating == 0.0f)
            val starRating : Float get() =
                if(rating == null) 0.0f else rating



        }*/
    }
}


/*
if(rating == null){
    binder.tvRatingEmpty.visibility = View.VISIBLE
    binder.ratingBar.visibility = View.GONE
    binder.tvRatingEmpty.text = item?.primeRating
}
else{
    if(rating == 0.0f){
        binder.tvRatingEmpty.visibility = View.VISIBLE
        binder.ratingBar.visibility = View.GONE
        binder.tvRatingEmpty.text = "Unrated"
    }
    else{
        binder.tvRatingEmpty.visibility = View.GONE
        binder.ratingBar.visibility = View.VISIBLE
        binder.ratingBar.rating = rating
    }
}*/
