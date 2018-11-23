package com.tarrakki.module.transactions


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tarrakki.R
import com.tarrakki.databinding.FragmentTransactionDetailsBinding
import com.tarrakki.databinding.RowTransactionListItemBinding
import kotlinx.android.synthetic.main.fragment_transaction_details.*
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
const val IS_PENDING = "isPending"

class TransactionDetailsFragment : Fragment() {

    var vm: TransactionsVM? = null
    var binder: FragmentTransactionDetailsBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (binder == null) {
            binder = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_details, container, false)
            parentFragment?.let {
                vm = ViewModelProviders.of(it).get(TransactionsVM::class.java)
            }
        }
        return binder?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm?.let { vm ->
            if (arguments?.getBoolean(IS_PENDING, true)!!) {
                rvTransactions?.setUpRecyclerView(R.layout.row_transaction_list_item, vm.pendingTransactions) { item: Transactions, binder: RowTransactionListItemBinding, position ->
                    binder.transaction = item
                    binder.executePendingBindings()
                }
            } else {
                rvTransactions?.setUpRecyclerView(R.layout.row_transaction_list_item, vm.transactions) { item: Transactions, binder: RowTransactionListItemBinding, position ->
                    binder.transaction = item
                    binder.executePendingBindings()
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment TransactionDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = TransactionDetailsFragment().apply { arguments = basket }
    }
}
