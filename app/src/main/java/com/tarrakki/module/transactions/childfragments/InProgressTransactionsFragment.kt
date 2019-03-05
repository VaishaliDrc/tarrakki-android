package com.tarrakki.module.transactions.childfragments


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.TransactionApiResponse
import com.tarrakki.databinding.FragmentInProgressTransactionsBinding
import com.tarrakki.databinding.RowInprogressTransactionsBinding
import com.tarrakki.databinding.RowTransactionListStatusBinding
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_in_progress_transactions.*
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.adapters.setUpRecyclerView

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
        val inProgressTransactions = arrayListOf<WidgetsViewModel>()
        val loadMoreObservable = MutableLiveData<Int>()
        val loadMore = LoadMore()
        val response = Observer<TransactionApiResponse> {
            it?.let { data ->
                inProgressTransactions.remove(loadMore)
                loadMore.isLoading = false
                if (mRefresh?.isRefreshing == true) {
                    inProgressTransactions.clear()
                    mRefresh?.isRefreshing = false
                }
                if (data.transactions?.isNotEmpty() == true) {
                    inProgressTransactions.addAll(data.transactions)
                }
                if (inProgressTransactions.size >= 10 && data.totalCount > inProgressTransactions.size) {
                    inProgressTransactions.add(loadMore)
                }
                if (rvInProgressTransactions?.adapter == null) {
                    rvInProgressTransactions?.setUpMultiViewRecyclerAdapter(inProgressTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, View.GONE)
                        if (binder is RowInprogressTransactionsBinding && item is TransactionApiResponse.Transaction) {
                            binder.imgArrow.setOnClickListener {
                                item.isSelected = !item.isSelected
                            }
                            val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatuss>()
                            setData(statuslist, "${item.orderOperation}", item.paymentType)
                            binder.rvTransactionStatus.setUpRecyclerView(R.layout.row_transaction_list_status, statuslist) { item2: TransactionConfirmVM.TranscationStatuss, binder2: RowTransactionListStatusBinding, position2: Int ->
                                binder2.widget = item2
                                binder2.executePendingBindings()
                                if (position2 == statuslist.size - 1) {
                                    binder2.verticalDivider.visibility = View.GONE
                                } else {
                                    binder2.verticalDivider.visibility = View.VISIBLE
                                }
                            }
                        }
                        binder.executePendingBindings()
                        if (position >= 9 && inProgressTransactions.size - 1 == position && !loadMore.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = data.offset
                        }
                    }
                } else {
                    rvInProgressTransactions?.adapter?.notifyDataSetChanged()
                }
                tvNoItem?.visibility = if (inProgressTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        getViewModel().getTransactions(transactionType = TransactionApiResponse.IN_PROGRESS).observe(this, response)
        loadMoreObservable.observe(this, Observer {
            it?.let { offset ->
                Handler().postDelayed({
                    getViewModel().getTransactions(
                            transactionType = TransactionApiResponse.IN_PROGRESS,
                            offset = offset).observe(this, response)
                }, 2500)
            }
        })
        mRefresh?.setOnRefreshListener {
            getViewModel().getTransactions(
                    transactionType = TransactionApiResponse.IN_PROGRESS,
                    mRefresh = true).observe(this, response)
        }
        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                tvNoItem?.visibility = if (inProgressTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    //TODO static data
    fun setData(statuslist: ArrayList<TransactionConfirmVM.TranscationStatuss>, status: String, paymentType: String) {
        when (status) {
            "1" -> {
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Mutual Fund Payment", paymentType, "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Order Placed with AMC", "", "In progress"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Investment Confirmation", "", "Pending"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Units Alloted", "", "Pending"))
            }
            "2" -> {
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Mutual Fund Payment", paymentType, "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Order Placed with AMC", "", "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Investment Confirmation", "", "In progress"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Units Alloted", "", "Pending"))
            }
            "3" -> {
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Mutual Fund Payment", paymentType, "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Order Placed with AMC", "", "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Investment Confirmation", "", "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Units Alloted", "", "In progress"))
            }
            "4" -> {
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Mutual Fund Payment", paymentType, "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Order Placed with AMC", "", "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Investment Confirmation", "", "completed"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Units Alloted", "", "completed"))
            }
            else -> {
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Mutual Fund Payment", paymentType, "In progress"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Order Placed with AMC", "", "Pending"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Investment Confirmation", "", "Pending"))
                statuslist.add(TransactionConfirmVM.TranscationStatuss("Units Alloted", "", "Pending"))
            }
        }
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
