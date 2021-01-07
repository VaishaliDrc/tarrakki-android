package com.tarrakki.api.model
import android.text.TextUtils
import com.google.gson.annotations.SerializedName


data class PrimeInvestorMutualFundsListResponse(
    @SerializedName("funds")
    val funds: ArrayList<Fundd?>,
    @SerializedName("limit")
    val limit: Int?,
    @SerializedName("offset")
    var offset: Int = 0,
    @SerializedName("total_funds")
    val totalFunds: Int,
    @SerializedName("user_info")
    val userInfo: String
)

data class Fundd(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    var isAdded: Boolean = false
){
    val status
        get() = if (isAdded) "Remove" else "Add"
}