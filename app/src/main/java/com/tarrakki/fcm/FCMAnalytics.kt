package com.tarrakki.fcm

import android.os.Bundle
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.analytics.FirebaseAnalytics
import com.tarrakki.App
import com.tarrakki.BuildConfig
import com.tarrakki.bundleToMap
import org.supportcompact.ktx.getEmail
import org.supportcompact.ktx.getMobile
import org.supportcompact.ktx.getUserId


const val PANCARDKYCVERIFIED = "PANCardKYCVerified"
const val PANCARDKYCNOTVERIFIED = "PANCardKYCNotVerified"
const val INVESTINMUTUALFUNDS = "InvestInMutualFunds"
const val LOGIN = "Login"
const val TZDebitCardRequest = "TZ Debit Card request"
const val BSERegistration = "BSE Registration"
const val TARRAKKIPROSIGNUP = "TarrakkiProSignUp"
const val EQUITYADVISORYSIGNUP = "EquityAdvisorySignUp"
//const val USERSCOULDNOTREGISTER = "usersCouldNotRegister"

fun onLoginEventFire(bundle: Bundle) {
    try {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
        logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        logFbEvent(LOGIN, bundle)
        AppsFlyerLib.getInstance().setCustomerUserId(bundle.getString("user_id", ""))
        logAppsflyerEvents(FirebaseAnalytics.Event.LOGIN)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun onSignUpEventFire(bundle: Bundle) {
    try {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
        logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        AppsFlyerLib.getInstance().setCustomerUserId(bundle.getString("user_id", ""))
        logAppsflyerEvents(FirebaseAnalytics.Event.SIGN_UP)
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
        logAppsflyerEvents(PANCARDKYCVERIFIED)

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
        logAppsflyerEvents(PANCARDKYCNOTVERIFIED)
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
        logAppsflyerEvents(TZDebitCardRequest)
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
        logAppsflyerEvents(BSERegistration)
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
        logAppsflyerEvents(INVESTINMUTUALFUNDS)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun onTarrakkiProSignUp() {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", App.INSTANCE.getUserId())
        bundle.putString("email_id", App.INSTANCE.getEmail())
        bundle.putString("mobile_number", App.INSTANCE.getMobile())
        logAppsflyerEvents(TARRAKKIPROSIGNUP)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun onEquityAdvisorySignUp() {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", App.INSTANCE.getUserId())
        bundle.putString("email_id", App.INSTANCE.getEmail())
        bundle.putString("mobile_number", App.INSTANCE.getMobile())
        logAppsflyerEvents(EQUITYADVISORYSIGNUP)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun logEvent(key: String, bundle: Bundle) {
    try {
        if (!BuildConfig.DEBUG) {
            App.INSTANCE.firebaseAnalytics.logEvent(key, bundle)
        }
    } catch (e: Exception) {

    }
}

fun logFbEvent(key: String, bundle: Bundle) {
    try {
        if (!BuildConfig.DEBUG) {
            App.INSTANCE.fbAppEventsLogger.logEvent(key, bundle)
        }
    } catch (e: Exception) {

    }
}

fun logAppsflyerEvents(key: String) {
    try {
        if (!BuildConfig.DEBUG) {
            AppsFlyerLib.getInstance().logEvent(getApplicationContext(), key, null)
        }
    } catch (e: Exception) {

    }
}