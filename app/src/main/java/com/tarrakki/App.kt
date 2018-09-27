package com.tarrakki

import android.app.Application
import android.arch.lifecycle.MutableLiveData

class App : Application() {

    val cartCount = MutableLiveData<Int>()

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