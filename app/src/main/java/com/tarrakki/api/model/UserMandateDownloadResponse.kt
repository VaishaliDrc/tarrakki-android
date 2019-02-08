package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class UserMandateDownloadResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("amount")
            val amount: Int,
            @SerializedName("approved_date")
            val approvedDate: Any?,
            @SerializedName("bank_details")
            val bankDetails: BankDetails,
            @SerializedName("bse_message")
            val bseMessage: Any?,
            @SerializedName("bse_remarks")
            val bseRemarks: Any?,
            @SerializedName("collection_type")
            val collectionType: Any?,
            @SerializedName("id")
            val id: Int,
            @SerializedName("mandate_file")
            val mandateFile: String,
            @SerializedName("mandate_id")
            val mandateId: String,
            @SerializedName("mandate_type")
            val mandateType: String,
            @SerializedName("mandate_upload_date")
            val mandateUploadDate: Any?,
            @SerializedName("registration_date")
            val registrationDate: Any?,
            @SerializedName("status")
            val status: String,
            @SerializedName("umr_no")
            val umrNo: Any?,
            @SerializedName("user")
            val user: Int
    ) {
        data class BankDetails(
                @SerializedName("account_number")
                val accountNumber: String,
                @SerializedName("bank_id")
                val bankId: Int,
                @SerializedName("bank_name")
                val bankName: String,
                @SerializedName("branch_name")
                val branchName: String,
                @SerializedName("ifsc_code")
                val ifscCode: String
        )
    }
}