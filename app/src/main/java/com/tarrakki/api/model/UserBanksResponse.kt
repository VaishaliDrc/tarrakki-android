package com.tarrakki.api.model

import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel

data class UserBanksResponse(
        @SerializedName("data")
        val `data`: Data
)

data class Data(
        @SerializedName("bank_details")
        val bankDetails: ArrayList<BankDetail>,
        @SerializedName("bank_detail")
        var bankDetail: BankDetail
)

data class BankDetail(
        @SerializedName("account_number")
        var accountNumber: String,
        @SerializedName("account_type_bse")
        var accountTypeBse: String,
        @SerializedName("branch__bank_id__bank_name")
        var branchBankIdBankName: String,
        @SerializedName("branch__branch_name")
        var branchBranchName: String,
        @SerializedName("branch__ifsc_code")
        var branchIfscCode: String,
        @SerializedName("holding_mode")
        var holdingMode: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_default")
        var isDefault: Boolean,
        @SerializedName("user__email")
        val userEmail: String,
        @SerializedName("bank_logo")
        val bankLogo: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("ifsc_code")
        var ifsc_code: String,
        @SerializedName("verification_document")
        var verificationDocument: String
) : WidgetsViewModel {

    override fun layoutId(): Int {
        return R.layout.row_bank_account_list_item
    }

    var isValid = false
        get() = isDefault && (status.equals("VERIFIED", true) || status.equals("UPLOADEDTOBSE", true))

    var isDefaultVisivibility: Int = View.GONE
        get() = if (isDefault && (status.equals("VERIFIED", true) || status.equals("UPLOADEDTOBSE", true))) View.VISIBLE else View.GONE

    var checkBoxVisibility = View.GONE
        get() = if (status.equals("VERIFIED", true) || status.equals("UPLOADEDTOBSE", true)) View.VISIBLE else View.GONE

    var isUpdateVisivibility: Int = View.GONE
        get() = if (status.equals("VERIFIED", true) || status.equals("UPLOADEDTOBSE", true)) View.GONE else View.VISIBLE

    var docStatus: String = status
        get() {
            if (status.equals("VERIFIED", true) || status.equals("UPLOADEDTOBSE", true)) {
                return "Verified"
            } else if (status.equals("Rejected", true)) {
                return "Rejected"
            } else {
                return "Pending"
            }
        }

    var updateButtonlabel: String = status
        get() {
            if (status.equals("VERIFIED", true) || status.equals("UPLOADEDTOBSE", true)) {
                return "Update"
            } else if (status.equals("Rejected", true)) {
                return "Update for Rejected"
            } else {
                return "Update for Pending"
            }
        }

}