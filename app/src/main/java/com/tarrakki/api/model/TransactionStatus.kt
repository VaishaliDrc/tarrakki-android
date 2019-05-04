package com.tarrakki.api.model

import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM

data class TransactionStatus(
        @SerializedName("bse_remarks")
        val bseRemarks: String,
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("transaction_id")
        val transactionId: Int,
        @SerializedName("order_type")
        val orderType: String,
        @SerializedName("scheme_name")
        val schemeName: String,
        val isSuccess: Boolean = false,
        val status: List<TransactionConfirmVM.TranscationStatuss>? = null
) {

    var btnExpandableVisibility = View.VISIBLE

    var type: String = ""
        get() = if (orderType == "1") {
            "Lumpsum"
        } else {
            "SIP"
        }
}
