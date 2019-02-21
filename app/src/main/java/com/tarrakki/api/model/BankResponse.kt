package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class BankResponse(
        @SerializedName("data")
        val `data`: List<Data>?
) {
    data class Data(
            @SerializedName("bank_logo")
            val bankLogo: String,
            @SerializedName("bank_name")
            val bankName: String,
            @SerializedName("bse_bank_code")
            val bseBankCode: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("payment_mode")
            val paymentMode: String
    )

    var banks: ArrayList<String>? = null
        get() = if (field == null) {
            field = arrayListOf()
            data?.let {
                it.forEach {
                    field?.add(it.bankName)
                }
            }
            field
        } else {
            field
        }

    fun bankId(name: String?) = data?.firstOrNull { b -> b.bankName == name }?.id.toString()
}