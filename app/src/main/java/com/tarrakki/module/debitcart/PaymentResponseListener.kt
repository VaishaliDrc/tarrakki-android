package com.tarrakki.module.debitcart

interface PaymentResponseListener {
    fun onPaymentSuccess()
    fun onPaymentFailure(message: String)
}