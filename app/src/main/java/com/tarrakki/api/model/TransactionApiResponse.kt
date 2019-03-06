package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.StringDef
import android.support.v7.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import com.tarrakki.App
import com.tarrakki.R
import org.supportcompact.BR
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.ktx.color
import org.supportcompact.ktx.parseAsNoZiroReturnOrNA


data class TransactionApiResponse(
        @SerializedName("data")
        val transactions: List<Transaction>?,
        @SerializedName("limit")
        val limit: Int,
        @SerializedName("offset")
        val offset: Int,
        @SerializedName("total_count")
        val totalCount: Int

) {
    data class Transaction(
            @SerializedName("order_operation")
            val orderOperation: String?,
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
            @SerializedName("payment_mode", alternate = ["payment_method"])
            val paymentMode: String

    ) : BaseObservable(), WidgetsViewModel {

        @SerializedName("units")
        val units: String? = null
            get() = parseAsNoZiroReturnOrNA("$field")

        val paymentType
            get() = if (paymentMode == "DIRECT") {
                "via Net Banking"
            } else {
                "via NEFT/RTGS"
            }

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

        fun getStringRes(): Int {
            return when ("$status") {
                IN_PROGRESS -> R.string.in_progress
                COMPLETED -> R.string.completed
                UPCOMING -> R.string.upcoming
                UNPAID -> R.string.unpaid
                FAILED -> R.string.failed
                else -> R.string.in_progress
            }
        }

        fun getTextColorRes(): Int {
            return when ("$status") {
                FAILED -> App.INSTANCE.color(R.color.red)
                /*IN_PROGRESS -> R.string.in_progress
                COMPLETED -> R.string.completed
                UPCOMING -> R.string.upcoming
                UNPAID -> R.string.unpaid*/
                else -> App.INSTANCE.color(R.color.pdlg_color_green)
            }
        }

    }

    companion object {
        const val ALL = "all"
        const val IN_PROGRESS = "in_progress"
        const val COMPLETED = "completed"
        const val UPCOMING = "upcoming"
        const val UNPAID = "unpaid"
        const val FAILED = "failed"

        @StringDef(ALL, IN_PROGRESS, COMPLETED, UPCOMING, UNPAID, FAILED)
        @Retention(value = AnnotationRetention.SOURCE)
        annotation class TransactionType
    }
}