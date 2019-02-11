package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class UserBankMandateResponse(
        @SerializedName("data")
        val `data`: List<Data>
) {
    data class Data(
            @SerializedName("amount")
            val amount: Double,
            @SerializedName("approved_date")
            val approvedDate: String,
            @SerializedName("bank_details")
            val bankDetails: BankDetails,
            @SerializedName("bse_message")
            val bseMessage: String,
            @SerializedName("bse_remarks")
            val bseRemarks: String,
            @SerializedName("collection_type")
            val collectionType: Any?,
            @SerializedName("id")
            val id: Int,
            @SerializedName("mandate_file")
            val mandateFile: Any?,
            @SerializedName("mandate_id")
            val mandateId: String,
            @SerializedName("mandate_type")
            val mandateType: String,
            @SerializedName("mandate_upload_date")
            val mandateUploadDate: String,
            @SerializedName("registration_date")
            val registrationDate: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("umr_no")
            val umrNo: String,
            @SerializedName("user")
            val user: Int,
            @SerializedName("is_mandate_uploaded")
            val isMandateUpload: Boolean
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