package com.tarrakki.module.ekyc

import android.arch.lifecycle.MutableLiveData
import android.databinding.Observable
import android.databinding.ObservableField
import android.view.View
import org.supportcompact.FragmentViewModel

class KYCRegistrationAVM : FragmentViewModel() {

    var kycData = MutableLiveData<KYCData>()
    val guardianVisibility = ObservableField(View.GONE)
    val isEdit = ObservableField(true)
    val alpha = ObservableField<Float>(1f)
    val ivEmailVerifiedVisibility = ObservableField(false)

    init {
        isEdit.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isEdit.get()?.let {
                    alpha.set(if (it) 1f else {
                        0.4f
                    })
                }
            }
        })
    }
}