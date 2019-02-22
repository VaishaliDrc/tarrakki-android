package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.StringDef
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.BR
import org.supportcompact.adapters.WidgetsViewModel


data class TransactionApiResponse(
        @SerializedName("data")
        val transactions: List<Transaction>?,
        @SerializedName("limit")
        val limit: Int,
        @SerializedName("offset")
        val offset: Int

) {
    data class Transaction(
            @SerializedName("amount")
            val amount: Double?,
            @SerializedName("created")
            val created: String?,
            @SerializedName("folio_number")
            val folioNumber: String,
            @SerializedName("fund_id")
            val fundId: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("nav")
            val nav: Double?,
            @SerializedName("order_id")
            val orderId: String,
            @SerializedName("order_number")
            val orderNumber: String?,
            @SerializedName("order_type")
            val orderType: String?,
            @SerializedName("remarks")
            val remarks: String,
            @SerializedName("status")
            val status: String?,
            @SerializedName("type")
            val type: String,
            @SerializedName("units")
            val units: String?
    ) : BaseObservable(), WidgetsViewModel {

        @get:Bindable
        var isSelected = false
            set(value) {
                field = value
                notifyPropertyChanged(BR.selected)
            }

        override fun layoutId(): Int {
            return when ("$status") {
                IN_PROGRESS -> R.layout.row_inprogress_transactions
                COMPLETED -> R.layout.row_completed_transactions
                UPCOMING -> R.layout.row_upcoming_transactions
                UNPAID -> R.layout.row_unpaid_transactions
                FAILED -> R.layout.row_failed_transactions
                else -> R.layout.row_inprogress_transactions
            }
        }

    }

    companion object {
        const val ALL = "all"
        const val IN_PROGRESS = "in_process"
        const val COMPLETED = "completed"
        const val UPCOMING = "upcoming"
        const val UNPAID = "unpaid"
        const val FAILED = "failed"

        @StringDef(ALL, IN_PROGRESS, COMPLETED, UPCOMING, UNPAID, FAILED)
        @Retention(value = AnnotationRetention.SOURCE)
        annotation class TransactionType
    }
}