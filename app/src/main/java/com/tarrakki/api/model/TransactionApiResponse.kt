package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Bundle
import android.support.annotation.StringDef
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.module.funddetails.FundDetailsFragment
import com.tarrakki.module.funddetails.ITEM_ID
import org.greenrobot.eventbus.EventBus
import org.supportcompact.BR
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.Event
import org.supportcompact.ktx.color
import org.supportcompact.ktx.format
import org.supportcompact.ktx.parseAsReturnOrNA
import org.supportcompact.ktx.startFragment


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
            val folioNo: String,
            @SerializedName("fund_id")
            val fundId: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("nav")
            val nav: String?,
            @SerializedName("order_id")
            val orderId: String,
            @SerializedName("order_number")
            val orderNo: String?,
            @SerializedName("order_type")
            val orderType: String?,
            @SerializedName("remarks")
            val remarksTxt: String?,
            @SerializedName("status")
            val status: String?,
            @SerializedName("type")
            val type: String,
            @SerializedName("payment_mode", alternate = ["payment_method"])
            val paymentMode: String,
            @SerializedName("payment")
            val payment: String?,
            @SerializedName("order_placed")
            val orderPlaced: String?,
            @SerializedName("units_alloted")
            val unitsAllocated: String?,
            @SerializedName("display_status")
            val displayStatus: Boolean,
            @SerializedName("withdrawal_confirm")
            val withdrawalConfirm: String?,
            @SerializedName("withdrawal_sent")
            val withdrawalSent: String?,
            @SerializedName("amount_creadited")
            val amountCreadited: String?,
            @SerializedName("is_reliance_redemption")
            val isRelianceRedemption: Boolean?,
            @SerializedName("buy_sell")
            val buySell: String?,
            @SerializedName("next_sip_date")
            val nextSIPDate: String

    ) : BaseObservable(), WidgetsViewModel {

        val remarks: String?
            get() = if (TextUtils.isEmpty(remarksTxt)) "N/A" else remarksTxt


        var isFromRaiseTicket: Boolean? = false

        var orderNumber: String? = null
            get() = if (TextUtils.isEmpty(orderNo)) orderId else orderNo

        val expandBtnVisibility
            get() = if (displayStatus) View.VISIBLE else View.GONE

        @SerializedName("units")
        val units: String? = null
            get() = if (TextUtils.isEmpty(field)) {
                "N/A"
            } else {
                try {
                    val temp: Double? = field?.toDoubleOrNull()
                    if (temp == null || temp == 0.0) {
                        "N/A"
                    } else {
                        if (field.contains(".")) {
                            "${field.substringBefore(".").toDouble().format()}${field.substring(field.indexOf("."))}"
                        } else {
                            temp.format()
                        }
                    }
                } catch (e: java.lang.Exception) {
                    "N/A"
                }
            }

        val todayNAV
            get() = parseAsReturnOrNA(nav)

        val folioNumber
            get() = if ((IN_PROGRESS.equals("$status", true) || IN_PROGRESS1.equals("$status", true)) && TextUtils.isEmpty(folioNo)) {
                "Under Process"
            } else {
                if (TextUtils.isEmpty(folioNo)) "N/A" else folioNo
            }

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
            return when {
                IN_PROGRESS.equals("$status", true)
                        || IN_PROGRESS1.equals("$status", true) -> R.layout.row_inprogress_transactions
                COMPLETED.equals("$status", true) -> R.layout.row_completed_transactions
                UPCOMING.equals("$status", true) -> R.layout.row_upcoming_transactions
                UNPAID.equals("$status", true) -> R.layout.row_unpaid_transactions
                FAILED.equals("$status", true) -> R.layout.row_failed_transactions
                else -> R.layout.row_inprogress_transactions
            }
        }

        fun getStringRes(): Int {
            return when {
                IN_PROGRESS.equals("$status", true)
                        || IN_PROGRESS1.equals("$status", true) -> R.string.in_progress
                COMPLETED.equals("$status", true) -> R.string.completed
                UPCOMING.equals("$status", true) -> R.string.upcoming
                UNPAID.equals("$status", true) -> R.string.unpaid
                FAILED.equals("$status", true) -> R.string.failed
                else -> R.string.in_progress
            }
        }

        fun getTextColorRes(): Int {
            return when {
                FAILED.equals("$status", true) -> App.INSTANCE.color(R.color.red)
                /*IN_PROGRESS -> R.string.in_progress
                COMPLETED -> R.string.completed
                UPCOMING -> R.string.upcoming
                UNPAID -> R.string.unpaid*/
                else -> App.INSTANCE.color(R.color.pdlg_color_green)
            }
        }

        val onSelected: View.OnClickListener
            get() = View.OnClickListener { v ->
                val mContext = v.context
                if (mContext is FragmentActivity && isFromRaiseTicket == true) {
                    EventBus.getDefault().post(Event.RESET_OPTION_MENU)
                    EventBus.getDefault().postSticky(this@Transaction)
                    mContext.onBackPressed()
                }
            }

        val openFundDetails: View.OnClickListener
            get() = View.OnClickListener { v ->
                val mContext = v.context
                if (mContext is FragmentActivity && isFromRaiseTicket == false) {
                    EventBus.getDefault().post(Event.RESET_OPTION_MENU)
                    mContext.startFragment(FundDetailsFragment.newInstance(Bundle().apply {
                        putString(ITEM_ID, "${fundId}")
                    }), R.id.frmContainer)
                } else if (isFromRaiseTicket == true) {
                    onSelected.onClick(v)
                }
            }

    }

    companion object {
        const val ALL = "all"
        const val IN_PROGRESS = "in_progress"
        const val IN_PROGRESS1 = "In Progress"
        const val COMPLETED = "completed"
        const val UPCOMING = "upcoming"
        const val UNPAID = "unpaid"
        const val FAILED = "failed"

        @StringDef(ALL, IN_PROGRESS, IN_PROGRESS1, COMPLETED, UPCOMING, UNPAID, FAILED)
        @Retention(value = AnnotationRetention.SOURCE)
        annotation class TransactionType
    }
}