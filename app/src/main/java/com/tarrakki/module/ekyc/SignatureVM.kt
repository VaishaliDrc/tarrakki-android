package com.tarrakki.module.ekyc

import android.databinding.Observable
import android.databinding.ObservableField
import org.supportcompact.ActivityViewModel

class SignatureVM : ActivityViewModel() {

    val isEdit = ObservableField(false)
    val alpha = ObservableField<Float>(0.4f)

    init {
        isEdit.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isEdit.get()?.let {
                    alpha.set(if (it) 1f else 0.4f)
                }
            }
        })
    }

}