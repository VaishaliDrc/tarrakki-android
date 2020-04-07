package org.supportcompact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View

open class ActivityViewModel : ViewModel() {

    val progressBar = ObservableField(View.GONE)
    val title = ObservableField<String>()
    val titleVisibility = ObservableField(true)
    val isBackEnabled = MutableLiveData<Boolean>()
    val footerVisibility = ObservableField(View.VISIBLE)
    val logoVisibility = ObservableField(View.GONE)
    val isEmpty = MutableLiveData<Boolean>()
    val isEmptyText = ObservableField("No data found.")
    val onNewBank = MutableLiveData<Boolean>()
    val timerValue = ObservableField<SpannableStringBuilder>()
    val isTarrakkiBase = ObservableField<Boolean>(true)

    fun emptyView(isShow: Boolean, string: String = "No data found.") {
        isEmpty.value = isShow
        isEmptyText.set(string)
    }
}