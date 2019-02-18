package com.tarrakki.module.ekyc

import android.arch.lifecycle.MutableLiveData
import android.databinding.Observable
import android.databinding.ObservableField
import android.view.View
import org.supportcompact.FragmentViewModel


class KYCRegistrationBVM : FragmentViewModel() {

    var kycData = MutableLiveData<KYCData>()

    val sourceOfIncome = ObservableField("")
    val iCertify = ObservableField(true)
    val TAXSlab = ObservableField("")
    val TINVisibility = ObservableField(View.GONE)
    val isEdit = ObservableField(false)
    val alpha = ObservableField<Float>(0.4f)

    init {

        iCertify.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                TINVisibility.set(if (iCertify.get()!!) View.GONE else View.VISIBLE)
            }
        })

        isEdit.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isEdit.get()?.let {
                    alpha.set(if (it) 1f else 0.4f)
                }
            }
        })

    }
}