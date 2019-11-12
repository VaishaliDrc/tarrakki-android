package com.tarrakki.api.model

import android.text.TextUtils
import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.ktx.convertToWithComma
import org.supportcompact.ktx.toDate


data class MySipApiResponse(
        @SerializedName("data")
        val mySipData: ArrayList<MySipData>?,
        @SerializedName("limit")
        val limit: Int?,
        @SerializedName("offset")
        val offset: Int?,
        @SerializedName("total_count")
        val totalCount: Int
)

data class MySipData(
        @SerializedName("amount")
        val amount: Double?,
        @SerializedName("created")
        val created: String?,
        @SerializedName("first_order_flag")
        val firstOrderFlag: String?,
        @SerializedName("folio_number")
        val folioNumber: String?,
        @SerializedName("fund_id")
        val fundId: Int?,
        @SerializedName("fund_name")
        val fundName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("mandate_id")
        val mandateId: String?,
        @SerializedName("mandate_type")
        val mandateType: String?,
        @SerializedName("sip_start_date")
        val sipStartDate: String?,
        @SerializedName("trans_code")
        val transCode: String?
) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_my_sip
    }

    val folioNo
        get() = if (TextUtils.isEmpty(folioNumber)) "N/A" else folioNumber

    val mandateRes
        get() = if ("x".equals(mandateType, true)) R.string.nach_mandate else R.string.sip_mandate

    val status
        get() = when {
            "New".equals(transCode, true) -> R.string.active
            "CXL".equals(transCode, true) -> R.string.stopped
            "AUTOCXL".equals(transCode, true) -> R.string.auto_cancel
            else -> R.string.NA
        }

    val status_color
        get() = when {
            "New".equals(transCode, true) -> R.color.active
            "CXL".equals(transCode, true) -> R.color.stopped
            "AUTOCXL".equals(transCode, true) -> R.color.auto_cancel
            else -> R.color.colorPrimary
        }

    val stopVisibility
        get() = if ("New".equals(transCode, true)) View.VISIBLE else View.GONE

    val startDate
        get() = sipStartDate?.toDate()?.convertToWithComma()

    val registerDate
        get() = created?.toDate()?.convertToWithComma()
}