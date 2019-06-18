package com.tarrakki.fcm

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.tarrakki.App


fun onLoginEventFire(userId: String) {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", userId)
        App.INSTANCE.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun onSignUpEventFire(userId: String) {
    try {
        val bundle = Bundle()
        bundle.putString("user_id", userId)
        App.INSTANCE.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}