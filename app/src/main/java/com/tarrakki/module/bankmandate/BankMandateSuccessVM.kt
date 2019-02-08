package com.tarrakki.module.bankmandate

import android.databinding.ObservableField
import com.tarrakki.api.model.BankDetail
import org.supportcompact.FragmentViewModel

class BankMandateSuccessVM : FragmentViewModel() {

    val isIMandate = ObservableField<Boolean>(true)
    val bankMandate = ObservableField<BankDetail>()

}