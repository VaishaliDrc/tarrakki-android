package com.tarrakki.module.transactions.childfragments


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.tarrakki.R
import com.tarrakki.databinding.FragmentUnpaidTransactionsBinding
import com.tarrakki.databinding.RowUnpaidTransactionsBinding
import com.tarrakki.module.transactions.Transactions
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_unpaid_transactions.*
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.setUpRecyclerView


/**
 * A simple [Fragment] subclass.
 * Use the [UnpaidTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UnpaidTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentUnpaidTransactionsBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_unpaid_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentUnpaidTransactionsBinding) {

    }

    override fun createReference() {

        rvUnpaidTransactions?.setUpRecyclerView(R.layout.row_unpaid_transactions, getViewModel().transactions) { item: Transactions, binder: RowUnpaidTransactionsBinding, position: Int ->
            binder.data = item
            binder.executePendingBindings()
            binder.root.setOnLongClickListener { v: View? ->
                item.isSelected = !item.isSelected
                hasSelectedItem()
                true
            }
            binder.root.setOnClickListener {
                if (getViewModel().hasOptionMenu.value == true) {
                    item.isSelected = !item.isSelected
                }
                hasSelectedItem()
            }
        }

        getViewModel().hasOptionMenu.observe(this, Observer {
            it?.let { hasOptionsMenu ->
                setHasOptionsMenu(hasOptionsMenu)
                if (!hasOptionsMenu) {
                    getViewModel().transactions.forEach { item ->
                        item.isSelected = false
                    }
                }
            }
        })
        getViewModel().onBack.observe(this, Observer {
            if (getViewModel().hasOptionMenu.value == true) {
                getViewModel().hasOptionMenu.value = false
            } else {
                onBack()
            }
        })
    }

    private fun hasSelectedItem() {
        val item = getViewModel().transactions.firstOrNull { it.isSelected }
        getViewModel().hasOptionMenu.value = item != null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.delete_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment UnpaidTransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UnpaidTransactionsFragment().apply { arguments = basket }
    }
}
