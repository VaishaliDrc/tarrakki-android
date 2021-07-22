package com.tarrakki

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.tarrakki.api.model.Fundd
import com.tarrakki.api.model.HomeData
import io.branch.referral.Branch
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
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
    lateinit var fbAppEventsLogger : AppEventsLogger
    private val devKey = "RyRefh8bWTSFpGFbuwPDVM"

    val primeInvestorList : ArrayList<Fundd?> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        fbAppEventsLogger = AppEventsLogger.newLogger(this)
        cartCount.value = 0

        // This is needed to deferred deep link from an Android Instant App to a full app
        // It tells the Branch initialization to wait for the Google Play Referrer before proceeding.
        // Branch.setPlayStoreReferrerCheckTimeout(1000L)

        // Initialize the Branch SDK
        if (BuildConfig.FLAVOR.isTarrakki()) {
            // Branch logging for debugging
            Branch.getAutoInstance(this)
            initializeAppsflyer()
        }
        //Sentry Tracking
        Sentry.init("https://e33bb6ed55444fc69342ecfe5f38c2ed@sentry.drcsystems.com/13", AndroidSentryClientFactory(this))


    }

    private fun initializeAppsflyer() {
        val conversionDataListener  = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let { cvData ->
                    cvData.map {
                        Log.e("AppsflyerLog", "conversion_attribute:  ${it.key} = ${it.value}")
                    }
                }
            }
            override fun onConversionDataFail(error: String?) {
                Log.e("AppsflyerLog", "error onAttributionFailure :  $error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    Log.d("AppsflyerLog", "onAppOpen_attribute: ${it.key} = ${it.value}")
                }
            }

            override fun onAttributionFailure(error: String?) {
                Log.e("AppsflyerLog", "error onAttributionFailure :  $error")
            }
        }

        AppsFlyerLib.getInstance().init(devKey, conversionDataListener, this)
        AppsFlyerLib.getInstance().setDebugLog(false)
        AppsFlyerLib.getInstance().setCollectIMEI(false)
        AppsFlyerLib.getInstance().setCollectAndroidID(false)
        AppsFlyerLib.getInstance().start(this)
    }

    companion object {
        lateinit var INSTANCE: App

        var piMinimumInitialMultiple = BigInteger.ONE
        var piMinimumSubsequentMultiple = BigInteger.ONE
        var additionalSIPMultiplier = BigInteger.ONE
    }
}