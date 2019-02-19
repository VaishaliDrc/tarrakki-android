package com.tarrakki.module.transactionConfirm

import android.support.annotation.DrawableRes
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import java.util.*

class TransactionConfirmVM : FragmentViewModel() {

    val list = ArrayList<TransactionConfirm>()

    init {
        val statuslist = arrayListOf<TranscationStatus>()
        statuslist.add(TranscationStatus("Mutual Fund Payment", "via Net Banking", 1))
        statuslist.add(TranscationStatus("Order Placed with AMC", "", 2))
        statuslist.add(TranscationStatus("Investment Confirmation", "", 3))
        statuslist.add(TranscationStatus("Units Alloted", "", 3))

        list.add(TransactionConfirm("HDFC GOLD Fund Direct Growth", "Lumpsump", "10000", true, statuslist))
        list.add(TransactionConfirm("HDFC GOLD Fund Direct Growth", "SIP", "20000", false, statuslist))
    }

    data class TransactionConfirm(val name: String, val type: String, val amount: String,
                                  val isSuccess: Boolean, val status: ArrayList<TranscationStatus>)

    data class TranscationStatus(val name: String, val description: String, val status: Int) {

        var actualStatus: String = ""
            get() = when (status) {
                1 -> "Completed"
                2 -> "In Progress"
                else -> "Pending"
            }

        @DrawableRes
        var actualStatusDrawable: Int = R.drawable.shape_pending_bg
            get() = when (status) {
                1 -> R.drawable.shape_completed_bg
                2 -> R.drawable.shape_progress_bg
                else -> R.drawable.shape_pending_bg
            }

        @DrawableRes
        var actualStatusIcon: Int = R.drawable.ic_round_pending
            get() = when (status) {
                1 -> R.drawable.ic_round_completed
                2 -> R.drawable.in_round_progress
                else -> R.drawable.ic_round_pending
            }
    }
}