package com.tarrakki.fcm

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.tarrakki.App
import com.tarrakki.module.ekyc.KYCData
import com.tarrakki.module.ekyc.USERSCOULDNOTREGISTER
import org.supportcompact.ktx.email
import org.supportcompact.ktx.getEmail
import org.supportcompact.ktx.getMobile
import org.supportcompact.ktx.getUserId


const val PANCARDKYCVERIFIED = "PANCardKYCVerified"
const val PANCARDKYCNOTVERIFIED = "PANCardKYCNotVerified"
const val INVESTINMUTUALFUNDS = "InvestInMutualFunds"
//const val USERSCOULDNOTREGISTER = "usersCouldNotRegister"

fun onLoginEventFire(bundle: Bundle) {
    try {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
        App.INSTANCE.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun onSignUpEventFire(bundle: Bundle) {
    try {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
        App.INSTANCE.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun eventPanKYCVerified() {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", App.INSTANCE.getUserId())
        bundle.putString("email_id", App.INSTANCE.getEmail())
        bundle.putString("mobile_number", App.INSTANCE.getMobile())
        App.INSTANCE.firebaseAnalytics.logEvent(PANCARDKYCVERIFIED, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun eventPanKYCNonVerified() {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", App.INSTANCE.getUserId())
        bundle.putString("email_id", App.INSTANCE.getEmail())
        bundle.putString("mobile_number", App.INSTANCE.getMobile())
        App.INSTANCE.firebaseAnalytics.logEvent(PANCARDKYCNOTVERIFIED, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun eventInvestInMutualFund() {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", App.INSTANCE.getUserId())
        bundle.putString("email_id", App.INSTANCE.getEmail())
        bundle.putString("mobile_number", App.INSTANCE.getMobile())
        App.INSTANCE.firebaseAnalytics.logEvent(INVESTINMUTUALFUNDS, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}