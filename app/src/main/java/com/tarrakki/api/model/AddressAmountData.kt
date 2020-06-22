package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class AddressAmountData(@SerializedName("data")
                             val data: Data) {

    data class Data(@SerializedName("cashfree_amount")
                    val cashfreeAmount: String = "",
                    @SerializedName("user_address")
                    val userAddress: String = "")

}


