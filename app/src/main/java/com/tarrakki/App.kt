package com.tarrakki

import android.arch.lifecycle.MutableLiveData
import org.supportcompact.CoreApp
import org.supportcompact.adapters.WidgetsViewModel

class App : CoreApp() {

    val cartCount = MutableLiveData<Int>()
    val isAuthorise = MutableLiveData<Boolean>().apply { value = false }
    val isLoggedIn = MutableLiveData<Boolean>().apply { value = false }
    val widgetsViewModel = MutableLiveData<WidgetsViewModel>()
    val widgetsViewModelB = MutableLiveData<WidgetsViewModel>()


    init {
        App.INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        cartCount.value = 1
    }

    companion object {
        lateinit var INSTANCE: App
    }
}