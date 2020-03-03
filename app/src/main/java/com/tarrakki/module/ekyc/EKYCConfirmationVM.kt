package com.tarrakki.module.ekyc

import androidx.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class EKYCConfirmationVM : FragmentViewModel() {

    var kycData: KYCData? = null
    val sourceOfIncome = ObservableField("")
    val TAXSlab = ObservableField("")

    val incomeSlabs = arrayListOf<Pair<String, String>>()
    val sourcesOfIncomes = arrayListOf<Pair<String, String>>()

    init {
        incomeSlabs.setIncomeSlabs()
        sourcesOfIncomes.setSourceOfIncome()
    }

}