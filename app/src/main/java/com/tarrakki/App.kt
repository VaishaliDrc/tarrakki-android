package com.tarrakki

import android.arch.lifecycle.MutableLiveData
import org.supportcompact.CoreApp
import org.supportcompact.adapters.WidgetsViewModel
import java.io.File

class App : CoreApp() {

    val cartCount = MutableLiveData<Int>()
    val isAuthorise = MutableLiveData<Boolean>().apply { value = false }
    val isLoggedIn = MutableLiveData<Boolean>().apply { value = false }
    val widgetsViewModel = MutableLiveData<WidgetsViewModel>()
    val widgetsViewModelB = MutableLiveData<WidgetsViewModel>()
    val signatureFile = MutableLiveData<File>()

    init {
        App.INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        cartCount.value = 0
    }

    companion object {
        lateinit var INSTANCE: App
    }
}