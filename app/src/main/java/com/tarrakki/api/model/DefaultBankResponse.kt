package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName


data class DefaultBankResponse(
        @SerializedName("data")
        val `data`: DefaultBank
) {
    data class DefaultBank(
            @SerializedName("account_number")
            val accountNumber: String,
            @SerializedName("account_type_bse")
            val accountTypeBse: String,
            @SerializedName("bank_name")
            val bankName: String,
            @SerializedName("branch_name")
            val branchName: String,
            @SerializedName("holding_mode")
            val holdingMode: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("ifsc_code")
            val ifscCode: String,
            @SerializedName("url")
            val url: String
    ) {
        val bankImgUrl
            get() = url
    }
}