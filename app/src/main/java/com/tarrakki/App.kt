package com.tarrakki

import androidx.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.tarrakki.api.model.HomeData
import io.branch.referral.Branch
import org.supportcompact.CoreApp
import org.supportcompact.adapters.WidgetsViewModel
import java.io.File
import java.math.BigInteger


class App : CoreApp() {

    val cartCount = MutableLiveData<Int>()
    val isAuthorise = MutableLiveData<Boolean>().apply { value = false }
    val widgetsViewModel = MutableLiveData<WidgetsViewModel>()
    val widgetsViewModelB = MutableLiveData<WidgetsViewModel>()
    val signatureFile = MutableLiveData<File>()
    val isRefreshing = MutableLiveData<Boolean>().apply { value = false }
    var homeData: HomeData? = null
    var needToLoadTransactionScreen = -1
    var openChat: Pair<Boolean, String>? = null
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        cartCount.value = 0

        // Branch logging for debugging
        if (BuildConfig.DEBUG) {
            Branch.enableDebugMode()
        }
        // Branch object initialization
        Branch.getAutoInstance(this)
    }

    companion object {
        lateinit var INSTANCE: App

        var piMinimumInitialMultiple = BigInteger.ONE
        var piMinimumSubsequentMultiple = BigInteger.ONE
        var additionalSIPMultiplier = BigInteger.ONE
    }
}