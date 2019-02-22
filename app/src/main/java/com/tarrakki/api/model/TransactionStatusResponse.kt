package com.tarrakki.api.model
import com.google.gson.annotations.SerializedName

data class TransactionStatusResponse(
    @SerializedName("data")
    val `data`: List<Data>
) {
    data class Data(
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("investment_confirmation")
        val investmentConfirmation: String,
        @SerializedName("order_placed")
        val orderPlaced: String,
        @SerializedName("order_type")
        val orderType: String,
        @SerializedName("payment")
        val payment: String,
        @SerializedName("payment_mode")
        val paymentMode: String,
        @SerializedName("scheme_name")
        val schemeName: String,
        @SerializedName("units_alloted")
        val unitsAlloted: String
    ){
        var paymentType = ""
        get() = if (paymentMode=="DIRECT"){
            "via Net Banking"
        }else{
            "via NEFT/RTGS"
        }
    }
}