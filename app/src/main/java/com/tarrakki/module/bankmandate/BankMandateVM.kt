package com.tarrakki.module.bankmandate

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.tarrakki.BR
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class BankMandateVM : FragmentViewModel() {

    val bankMandate = arrayListOf<WidgetsViewModel>()

    init {
        bankMandate.add(BankMandate(
                "ICICI Bank",
                "1210000091052",
                "Ahmedabad",
                "IC1C00000457",
                true))
        bankMandate.add(object : WidgetsViewModel {
            override fun layoutId(): Int {
                return R.layout.btn_add_next_bank_mandate
            }
        })
    }

}

data class BankMandate(var name: String,
                       var accountNumber: String,
                       var breachName: String,
                       var IFSCCode: String,
                       var isDefault: Boolean = false
) : WidgetsViewModel, BaseObservable() {

    @get:Bindable
    var isPending: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.pending)
        }

    override fun layoutId(): Int {
        return R.layout.row_bank_mandate_list_item
    }
}