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
    val incomeSlabs = arrayListOf<Pair<String, String>>()
    val sourcesOfIncomes = arrayListOf<Pair<String, String>>()

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

        incomeSlabs.add(Pair("31", "Below 1 Lakh"))
        incomeSlabs.add(Pair("32", "> 1 <=5 Lacs"))
        incomeSlabs.add(Pair("33", ">5 <=10 Lacs"))
        incomeSlabs.add(Pair("34", ">10 <= 25 Lacs"))
        incomeSlabs.add(Pair("35", "> 25 Lacs < = 1 Crore"))
        incomeSlabs.add(Pair("36", "Above 1 Crore"))


        sourcesOfIncomes.add(Pair("01", "Salary"))
        sourcesOfIncomes.add(Pair("02", "Business Income"))
        sourcesOfIncomes.add(Pair("03", "Gift"))
        sourcesOfIncomes.add(Pair("04", "Ancestral Property"))
        sourcesOfIncomes.add(Pair("05", "Rental Income"))
        sourcesOfIncomes.add(Pair("06", "Prize Money"))
        sourcesOfIncomes.add(Pair("07", "Royalty"))
        sourcesOfIncomes.add(Pair("08", "Others"))

    }
}

data class Pair<K, V>(val key: K, val value: V) {
    override fun toString(): String {
        return "$value"
    }
}