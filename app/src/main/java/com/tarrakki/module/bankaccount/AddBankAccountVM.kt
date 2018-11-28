package com.tarrakki.module.bankaccount

import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class AddBankAccountVM : FragmentViewModel() {

    val name = ObservableField<String>("")
    val accountNo = ObservableField<String>("")
    val reenterAccountNo = ObservableField<String>("")
    val breachName = ObservableField<String>("")
    val accountType = ObservableField<String>("")
    val IFSCCode = ObservableField<String>("")

}