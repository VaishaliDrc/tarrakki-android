package com.tarrakki.module.transactions.childfragments


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.TransactionApiResponse
import com.tarrakki.databinding.FragmentAllTransactionsBinding
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_all_transactions.*
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter

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
        /*val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatus>()
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
        }*/

        val allTransactions = arrayListOf<WidgetsViewModel>()
        val loadMoreObservable = MutableLiveData<Int>()
        val loadMore = LoadMore()
        val response = Observer<TransactionApiResponse> {
            it?.let { data ->
                allTransactions.remove(loadMore)
                loadMore.loadMore = false
                if (data.transactions?.isNotEmpty() == true) {
                    allTransactions.addAll(data.transactions)
                }
                allTransactions.add(loadMore)
                if (rvAllTransactions.adapter == null) {
                    rvAllTransactions?.setUpMultiViewRecyclerAdapter(allTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.executePendingBindings()
                        if (position >= 9 && allTransactions.size - 1 == position && !loadMore.loadMore) {
                            loadMore.loadMore = true
                            loadMoreObservable.value = data.offset
                        }
                    }
                } else {
                    rvAllTransactions.adapter?.notifyDataSetChanged()
                }
            }
        }
        getViewModel().getTransactions().observe(this, response)
        loadMoreObservable.observe(this, Observer {
            it?.let { offset ->
                Handler().postDelayed({
                    getViewModel().getTransactions(offset = offset).observe(this, response)
                }, 2500)
            }
        })
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
