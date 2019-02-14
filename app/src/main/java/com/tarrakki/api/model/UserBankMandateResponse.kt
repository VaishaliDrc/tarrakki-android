package com.tarrakki.api.model

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import com.tarrakki.getBankMandateStatus

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
        var info: Int = R.string.normally_take_
            get() {
                return if (mandateType == "X"){
                    R.string.normally_take_
                }else{
                    R.string.normally_take_nach
                }
            }

        var type: Int = R.string.bank_mandate_type
            get() {
                return if (mandateType == "X"){
                    R.string.nach_mandate
                }else{
                    R.string.sip_mandate
                }
            }

        @DrawableRes var statuscolor: Int = R.drawable.shape_success_bg
            get() =  when (status) {
                "CANCELLED","FAILED" -> R.drawable.shape_failed_bg
                "APPROVED" -> R.drawable.shape_success_bg
                else -> R.drawable.shape_pending_bg
            }

        var actualStatus: String = ""
            get() =  when (status) {
                "CANCELLED","FAILED" -> "FAILED"
                "APPROVED" -> "APPROVED"
                else -> "PENDING"
            }

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