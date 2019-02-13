package com.tarrakki.module.bankmandate

import android.databinding.ObservableField
import com.tarrakki.api.model.BankDetail
import org.supportcompact.FragmentViewModel

class BankMandateSuccessVM : FragmentViewModel() {

    val isIMandate = ObservableField<Boolean>(true)
    val bankMandate = ObservableField<BankDetail>()
    val bankName = ObservableField<String>("")
    val branchName = ObservableField<String>("")
    val accountNumber = ObservableField<String>("")
}