package com.tarrakki.module.webviewActivity

import android.arch.lifecycle.MutableLiveData
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.Event

class WebviewVM : ActivityViewModel() {
    val onPage = MutableLiveData<Event>()
}