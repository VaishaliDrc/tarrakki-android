package com.tarrakki.module.ekyc

import android.databinding.Observable
import android.databinding.ObservableField
import android.view.View
import org.supportcompact.FragmentViewModel

class KYCRegistrationBVM : FragmentViewModel() {

    val PANNumber = ObservableField("1ABCDE1234F")
    val occupationType = ObservableField("")
    val countryOfBirth = ObservableField("")
    val placeOfBirth = ObservableField("")
    val sourceOfIncome = ObservableField("")
    val iCertify = ObservableField(true)
    val TAXSlab = ObservableField("")
    val TINVisibility = ObservableField(View.GONE)
    val TINNumberA = ObservableField("")
    val issueByA = ObservableField("")
    val TINNumberB = ObservableField("")
    val issueByB = ObservableField("")
    val TINNumberC = ObservableField("")
    val issueByC = ObservableField("")
    val isEdit = ObservableField(true)
    val alpha = ObservableField<Float>(1f)

    init {

        iCertify.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                TINVisibility.set(if (iCertify.get()!!) View.GONE else View.VISIBLE)
            }
        })

    }
}