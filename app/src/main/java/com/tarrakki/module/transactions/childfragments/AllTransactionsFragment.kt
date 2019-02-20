package com.tarrakki.module.transactions.childfragments


import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.R
import com.tarrakki.databinding.FragmentAllTransactionsBinding
import com.tarrakki.databinding.RowInprogressTransactionsBinding
import com.tarrakki.databinding.RowTranscationStatusBinding
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM
import com.tarrakki.module.transactions.Transactions
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_all_transactions.*
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [AllTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentAllTransactionsBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_all_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentAllTransactionsBinding) {

    }

    override fun createReference() {
        val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatus>()
        statuslist.add(TransactionConfirmVM.TranscationStatus("Mutual Fund Payment", "via Net Banking", 1))
        statuslist.add(TransactionConfirmVM.TranscationStatus("Order Placed with AMC", "", 2))
        statuslist.add(TransactionConfirmVM.TranscationStatus("Investment Confirmation", "", 3))
        statuslist.add(TransactionConfirmVM.TranscationStatus("Units Alloted", "", 3))
        rvAllTransactions?.setUpMultiViewRecyclerAdapter(getViewModel().pendingTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.data, item)
            if (binder is RowInprogressTransactionsBinding && item is Transactions) {
                binder.imgArrow.setOnClickListener {
                    item.isSelected = !item.isSelected
                }
                binder.rvTransactionStatus.setUpRecyclerView(R.layout.row_transcation_status, statuslist) { transaction: TransactionConfirmVM.TranscationStatus, tBinder: RowTranscationStatusBinding, position: Int ->
                    tBinder.widget = transaction
                    if (position == statuslist.size - 1) {
                        tBinder.verticalDivider.visibility = View.GONE
                    } else {
                        tBinder.verticalDivider.visibility = View.VISIBLE
                    }
                    tBinder.executePendingBindings()
                }
            }
            binder.executePendingBindings()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment AllTransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AllTransactionsFragment().apply { arguments = basket }
    }
}
