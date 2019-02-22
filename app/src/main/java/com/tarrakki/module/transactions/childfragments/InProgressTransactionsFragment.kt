package com.tarrakki.module.transactions.childfragments


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentInProgressTransactionsBinding
import com.tarrakki.module.transactions.TransactionsVM
import org.supportcompact.CoreParentFragment

/**
 * A simple [Fragment] subclass.
 * Use the [InProgressTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class InProgressTransactionsFragment : CoreParentFragment<TransactionsVM, com.tarrakki.databinding.FragmentInProgressTransactionsBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_in_progress_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentInProgressTransactionsBinding) {

    }

    override fun createReference() {
        /*val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatus>()
        statuslist.add(TransactionConfirmVM.TranscationStatus("Mutual Fund Payment", "via Net Banking", 1))
        statuslist.add(TransactionConfirmVM.TranscationStatus("Order Placed with AMC", "", 2))
        statuslist.add(TransactionConfirmVM.TranscationStatus("Investment Confirmation", "", 3))
        statuslist.add(TransactionConfirmVM.TranscationStatus("Units Alloted", "", 3))
        rvInProgressTransactions?.setUpRecyclerView(R.layout.row_inprogress_transactions, getViewModel().transactions) { item: Transactions, binder: RowInprogressTransactionsBinding, position: Int ->
            binder.data = item
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
            binder.executePendingBindings()
        }*/
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment InProgressTransactionsFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = InProgressTransactionsFragment().apply { arguments = basket }
    }
}
