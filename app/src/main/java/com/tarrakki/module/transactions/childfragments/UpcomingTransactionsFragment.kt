package com.tarrakki.module.transactions.childfragments


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentUnpaidTransactionsBinding
import com.tarrakki.databinding.FragmentUpcomingTransactionsBinding
import com.tarrakki.databinding.RowUpcomingTransactionsBinding
import com.tarrakki.module.transactions.Transactions
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_upcoming_transactions.*
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [UpcomingTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UpcomingTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentUpcomingTransactionsBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_upcoming_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentUpcomingTransactionsBinding) {

    }

    override fun createReference() {
        rvUpcomingTransactions?.setUpRecyclerView(R.layout.row_upcoming_transactions, getViewModel().transactions) { item: Transactions, binder: RowUpcomingTransactionsBinding, position: Int ->
            binder.data = item
            binder.executePendingBindings()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment UpcomingTransactionsFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UpcomingTransactionsFragment().apply { arguments = basket }
    }
}
