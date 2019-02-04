package com.tarrakki.module.webview

import android.arch.lifecycle.MutableLiveData
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.Event

class WebViewVM : FragmentViewModel() {

    val onPage = MutableLiveData<Event>()

}