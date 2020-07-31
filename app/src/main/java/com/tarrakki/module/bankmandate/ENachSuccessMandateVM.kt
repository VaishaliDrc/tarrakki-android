package com.tarrakki.module.bankmandate

import androidx.databinding.ObservableField
import com.tarrakki.api.model.BankDetail
import org.supportcompact.FragmentViewModel

class ENachSuccessMandateVM : FragmentViewModel() {

    val isIMandate = ObservableField<Boolean>(true)
    val bankMandate = ObservableField<BankDetail>()
    val bankLogo = ObservableField<String>("")
    val bankName = ObservableField<String>("")
    val branchName = ObservableField<String>("")
    val accountNumber = ObservableField<String>("")
}