package com.tarrakki.module.transactions.childfragments


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentCompletedTransactionsBinding
import com.tarrakki.databinding.RowCompletedTransactionsBinding
import com.tarrakki.module.transactions.Transactions
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_completed_transactions.*
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [CompletedTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CompletedTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentCompletedTransactionsBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_completed_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentCompletedTransactionsBinding) {

    }

    override fun createReference() {
        rvCompletedTransactions?.setUpRecyclerView(R.layout.row_completed_transactions, getViewModel().transactions) { item: Transactions, binder: RowCompletedTransactionsBinding, position: Int ->
            binder.data = item
            binder.executePendingBindings()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param basket As Bundle.
         * @return A new instance of fragment CompletedTransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = CompletedTransactionsFragment().apply { arguments = basket }
    }
}
