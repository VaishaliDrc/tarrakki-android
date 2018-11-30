package com.tarrakki.module.bankaccount

import android.support.annotation.StringRes
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class BankAccountsVM : FragmentViewModel() {

    val banks = arrayListOf<WidgetsViewModel>()

    init {
        banks.add(object : WidgetsViewModel {
            override fun layoutId(): Int {
                return R.layout.row_no_bank_account_yet
            }
        })
        banks.add(SingleButton(R.string.add_new_bank_account))
    }
}

data class SingleButton(@StringRes var title: Int) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.btn_add_bank_account
    }
}

data class Bank(
        var name: String,
        var accountNumber: String,
        var breachName: String,
        var IFSCCode: String,
        var isDefault: Boolean = false
) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_bank_account_list_item
    }
}