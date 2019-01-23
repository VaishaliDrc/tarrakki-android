package org.supportcompact

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View

open class ActivityViewModel : ViewModel() {

    val progressBar = ObservableField(View.GONE)
    val title = ObservableField<String>()
    val isBackEnabled = MutableLiveData<Boolean>()
    val footerVisibility = ObservableField<Int>(View.VISIBLE)
    val isEmpty = MutableLiveData<Boolean>()
    val isEmptyText = ObservableField<String>("No data found.")
    val onNewBank = MutableLiveData<Boolean>()

    fun emptyView(isShow: Boolean, string: String = "No data found.") {
        isEmpty.value = isShow
        isEmptyText.set(string)
    }
}