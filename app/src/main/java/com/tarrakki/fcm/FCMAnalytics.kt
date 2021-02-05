package com.tarrakki.fcm

import android.os.Bundle
import android.util.Log
import com.tarrakki.BuildConfig
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
const val LOGIN = "Login"
const val TZDebitCardRequest = "TZ Debit Card request"
const val BSERegistration = "BSE Registration"
//const val USERSCOULDNOTREGISTER = "usersCouldNotRegister"

fun onLoginEventFire(bundle: Bundle) {
    try {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
        logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        logFbEvent(LOGIN,bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun onSignUpEventFire(bundle: Bundle) {
    try {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
        logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
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
        logEvent(PANCARDKYCVERIFIED, bundle)
        logFbEvent(PANCARDKYCVERIFIED, bundle)

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
        logEvent(PANCARDKYCNOTVERIFIED, bundle)
        logFbEvent(PANCARDKYCNOTVERIFIED, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun eventTZDebitCardRequest() {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", App.INSTANCE.getUserId())
        bundle.putString("email_id", App.INSTANCE.getEmail())
        bundle.putString("mobile_number", App.INSTANCE.getMobile())
        logEvent(TZDebitCardRequest, bundle)
        logFbEvent(TZDebitCardRequest, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun eventBSERegistration() {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", App.INSTANCE.getUserId())
        bundle.putString("email_id", App.INSTANCE.getEmail())
        bundle.putString("mobile_number", App.INSTANCE.getMobile())
        logEvent(BSERegistration, bundle)
        logFbEvent(BSERegistration, bundle)
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
        logEvent(INVESTINMUTUALFUNDS, bundle)
        logFbEvent(INVESTINMUTUALFUNDS, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun logEvent(key: String,bundle: Bundle){
    try {
        if (!BuildConfig.DEBUG) {
            App.INSTANCE.firebaseAnalytics.logEvent(key,bundle)
        }
    }
    catch (e:Exception){

    }
}

fun logFbEvent(key: String,bundle: Bundle){
    try {
        if (!BuildConfig.DEBUG) {
            Log.e(key,bundle.toString())
            App.INSTANCE.fbAppEventsLogger.logEvent(key,bundle)
        }
    }
    catch (e:Exception){

    }
}