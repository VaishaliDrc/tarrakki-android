package com.tarrakki

import android.arch.lifecycle.MutableLiveData
import com.tarrakki.api.model.HomeData
import org.supportcompact.CoreApp
import org.supportcompact.adapters.WidgetsViewModel
import java.io.File

class App : CoreApp() {

    val cartCount = MutableLiveData<Int>()
    val isAuthorise = MutableLiveData<Boolean>().apply { value = false }
    //val isLoggedIn = MutableLiveData<Boolean>().apply { value = false }
    val widgetsViewModel = MutableLiveData<WidgetsViewModel>()
    val widgetsViewModelB = MutableLiveData<WidgetsViewModel>()
    val signatureFile = MutableLiveData<File>()
    val isRefreshing = MutableLiveData<Boolean>().apply { value = false }

    var homeData : HomeData? = null

    override fun onCreate() {
        super.onCreate()
        App.INSTANCE = this
        cartCount.value = 0
    }

    companion object {
        lateinit var INSTANCE: App
    }
}