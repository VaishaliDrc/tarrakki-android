package com.tarrakki.module.transactions

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.IntDef
import com.tarrakki.R
import org.supportcompact.BR
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class TransactionsVM : FragmentViewModel() {

    val transactions = arrayListOf<Transactions>()
    val pendingTransactions = arrayListOf<WidgetsViewModel>()
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
                    25.00).apply {
                when (count) {
                    2 -> transactionType = Transactions.COMPLETED
                    3 -> transactionType = Transactions.UPCOMING
                    4 -> transactionType = Transactions.UNPAID
                }
            })
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
) : BaseObservable(), WidgetsViewModel {

    companion object {
        const val IN_PROGRESS = 0
        const val COMPLETED = 1
        const val UPCOMING = 2
        const val UNPAID = 3

        @IntDef(IN_PROGRESS, COMPLETED, UPCOMING, UNPAID)
        @Retention(value = AnnotationRetention.SOURCE)
        annotation class TransactionType
    }

    @TransactionType
    var transactionType = R.layout.row_inprogress_transactions

    override fun layoutId(): Int {
        return when (transactionType) {
            IN_PROGRESS -> R.layout.row_inprogress_transactions
            COMPLETED -> R.layout.row_completed_transactions
            UPCOMING -> R.layout.row_upcoming_transactions
            UNPAID -> R.layout.row_unpaid_transactions
            else -> R.layout.row_inprogress_transactions
        }
    }

    @get:Bindable
    var isSelected = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

}