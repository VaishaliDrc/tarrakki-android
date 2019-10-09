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
        val bankDetail: BankDetail
)

data class BankDetail(
        @SerializedName("account_number")
        val accountNumber: String,
        @SerializedName("account_type_bse")
        val accountTypeBse: String,
        @SerializedName("branch__bank_id__bank_name")
        val branchBankIdBankName: String,
        @SerializedName("branch__branch_name")
        val branchBranchName: String,
        @SerializedName("branch__ifsc_code")
        val branchIfscCode: String,
        @SerializedName("holding_mode")
        val holdingMode: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_default")
        val isDefault: Boolean,
        @SerializedName("user__email")
        val userEmail: String,
        @SerializedName("bank_logo")
        val bankLogo: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("ifsc_code")
        val ifsc_code: String
) : WidgetsViewModel {

    override fun layoutId(): Int {
        return R.layout.row_bank_account_list_item
    }

    var isDefaultVisivibility: Int = View.GONE
        get() = if (isDefault) View.VISIBLE else View.GONE

}