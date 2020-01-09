package com.tarrakki.module.bankmandate

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import com.tarrakki.BR
import com.tarrakki.api.model.BankDetail
import org.supportcompact.FragmentViewModel

class AutoMandateVM : FragmentViewModel() {

    val ammounts = arrayListOf<AutoDebitAmount>()
    val amount = ObservableField("")
    val bankMandate = ObservableField<BankDetail>()

    init {

        ammounts.add(AutoDebitAmount(10000))
        ammounts.add(AutoDebitAmount(25000))
        ammounts.add(AutoDebitAmount(50000))
        ammounts.add(AutoDebitAmount(100000,true))
        ammounts.add(AutoDebitAmount(500000))

    }
}

data class AutoDebitAmount(
        var amount: Int,
        var _isSelected: Boolean = false
) : BaseObservable() {
    @get:Bindable
    var isSelected: Boolean = _isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}

