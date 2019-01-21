package com.tarrakki

import android.arch.lifecycle.MutableLiveData
import org.supportcompact.CoreApp

class App : CoreApp() {

    val cartCount = MutableLiveData<Int>()
    val isAuthorise = MutableLiveData<Boolean>().apply { value = false }
    val isLoggedIn = MutableLiveData<Boolean>().apply { value = false }

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