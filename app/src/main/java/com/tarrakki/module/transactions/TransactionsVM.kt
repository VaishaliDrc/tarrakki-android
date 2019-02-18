package com.tarrakki.module.transactions

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import org.supportcompact.BR
import org.supportcompact.FragmentViewModel

class TransactionsVM : FragmentViewModel() {

    val transactions = arrayListOf<Transactions>()
    val pendingTransactions = arrayListOf<Transactions>()
    val hasOptionMenu = MutableLiveData<Boolean>()
    val onBack = MutableLiveData<Boolean>()

    init {

        for (count in 1..10) {
            transactions.add(Transactions(
                    "SBI Banking and Financial Services Growth Direct Plan",
                    25000.00,
                    "PIP (SIP)",
                    "Ex.Physical",
                    "Oct 10, 2018 - 10:15 AM",
                    100,
                    25.00,
                    false))
        }

        for (count in 1..10) {
            pendingTransactions.add(Transactions(
                    "SBI Banking and Financial Services Growth Direct Plan",
                    25000.00,
                    "PIP (SIP)",
                    "Ex.Physical",
                    "Oct 10, 2018 - 10:15 AM",
                    100,
                    25.00))
        }
    }
}

data class Transactions(
        var name: String,
        var amount: Double,
        var type: String,
        var mode: String,
        var date: String,
        var units: Int,
        var NAV: Double,
        val isPending: Boolean = true
) : BaseObservable() {
    @get:Bindable
    var isSelected = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

}