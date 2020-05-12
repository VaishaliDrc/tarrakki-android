package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class ValidationPaymentResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("bank_details")
            val bankDetails: BankDetails,
            @SerializedName("valid_payment_methods")
            val validPaymentMethods: List<ValidPaymentMethod>,
            @SerializedName("vpa_id")
            val vpaId: String
    ) {
        data class BankDetails(
                @SerializedName("account_number")
                val accountNumber: String,
                @SerializedName("account_type_bse")
                val accountTypeBse: String,
                @SerializedName("bank_name")
                val bankName: String,
                @SerializedName("branch_name")
                val branchName: String,
                @SerializedName("holding_mode")
                val holdingMode: Any,
                @SerializedName("id")
                val id: Int,
                @SerializedName("ifsc_code")
                val ifscCode: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("url")
                val url: String,
                @SerializedName("verification_document")
                val verificationDocument: String
        )

        data class ValidPaymentMethod(
                @SerializedName("order_id")
                val orderId: Int,
                @SerializedName("payment_method")
                val paymentMethod: List<String>,
                @SerializedName("fund_name")
                val fundName: String
        )

    }
}