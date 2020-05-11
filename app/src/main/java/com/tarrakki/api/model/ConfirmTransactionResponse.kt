package com.tarrakki.api.model

import android.text.TextUtils
import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.App
import com.tarrakki.R
import org.json.JSONArray
import org.supportcompact.ktx.toCurrency
import java.math.BigInteger

data class ConfirmTransactionResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("cart_count")
            val cartCount: Int,
            @SerializedName("account_number")
            val accountNumber: String,
            @SerializedName("bank_name")
            val bankName: String,
            @SerializedName("orders")
            val orders: List<Order>?,
            @SerializedName("total_payable_amount")
            val totalPayableAmount: BigInteger,
            @SerializedName("failed_transactions")
            val failedTransactions: List<TransactionStatus>?
    ) {

        data class Order(
                @SerializedName("lumpsum_transaction_id")
                val lumpsumTransactionId: Int,
                @SerializedName("scheme_name")
                val schemeName: String,
                @SerializedName("sip_transaction_id")
                val sipTransactionId: Int,
                @SerializedName("lumpsum_amount")
                var lumpsum_amount: String?,
                @SerializedName("sip_amount")
                var sip_amount: String?,
                @SerializedName("first_order_flag")
                var isFirstSIP: String? = null,
                @SerializedName("order_id")
                var order_id: String? = null
        ) {

            @SerializedName("sip_start_date")
            val sipStartDate = ""

            //var isFirstInstallmentSIP: Boolean = isFirstSIP == true

            val sipStartMsg
                get() = App.INSTANCE.getString(R.string.sip_start_date).plus(" $sipStartDate")


            var lumpsumAmount: String? = ""
                get() = lumpsum_amount?.toDoubleOrNull()?.toCurrency()

            var sipAmount: String? = ""
                get() = sip_amount?.toDoubleOrNull()?.toCurrency()

            val hasSIP
                get() = if (TextUtils.isEmpty(sipAmount) || "₹0" == sipAmount) View.GONE else View.VISIBLE

            val hasLumpsum
                get() = if (TextUtils.isEmpty(lumpsumAmount) || "₹0" == lumpsumAmount) View.GONE else View.VISIBLE

            val haveSIPAndLumpsum: Int
                get() = if (hasSIP == View.VISIBLE && hasLumpsum == View.VISIBLE) View.VISIBLE else View.GONE

            val title: Int
                get() = if (hasSIP == View.VISIBLE) R.string.sip else R.string.lumpsum

            val amount: String?
                get() = if (hasSIP == View.VISIBLE)
                    if ("Y".equals(isFirstSIP, true)) sipAmount else "₹0"
                else
                    lumpsumAmount

            val sipFirstMsgVisibility
                get() = if (hasSIP == View.VISIBLE && !"Y".equals(isFirstSIP, true)) View.VISIBLE else View.GONE

        }
    }
}