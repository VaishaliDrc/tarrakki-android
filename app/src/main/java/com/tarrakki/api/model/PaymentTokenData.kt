package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName


data class PaymentTokenData(@SerializedName("data")
                            val data: Data) {

    data class Data(@SerializedName("callback_url")
                    val callbackUrl: String = "",
                    @SerializedName("amount")
                    val amount: String = "",
                    @SerializedName("x-client-secret")
                    val xClientSecret: String = "",
                    @SerializedName("x-client-id")
                    val xClientId: String = "",
                    @SerializedName("cftoken")
                    val cftoken: String = "",
                    @SerializedName("order_id")
                    val orderId: String = "")
}


