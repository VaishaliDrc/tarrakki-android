package com.tarrakki.module.transactions.childfragments


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentFailedTransactionBinding
import com.tarrakki.databinding.RowFailedTransactionsBinding
import com.tarrakki.module.transactions.Transactions
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_failed_transaction.*
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [FailedTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class FailedTransactionFragment : CoreParentFragment<TransactionsVM, FragmentFailedTransactionBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_failed_transaction
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentFailedTransactionBinding) {

    }

    override fun createReference() {
        /*rvFailedTransactions?.setUpRecyclerView(R.layout.row_failed_transactions, getViewModel().transactions) { item: Transactions, binder: RowFailedTransactionsBinding, position: Int ->
            binder.data = item
            binder.executePendingBindings()
        }*/
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment FailedTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = FailedTransactionFragment().apply { arguments = basket }
    }
}
