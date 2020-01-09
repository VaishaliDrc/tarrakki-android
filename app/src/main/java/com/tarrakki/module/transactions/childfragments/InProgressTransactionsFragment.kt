package com.tarrakki.module.transactions.childfragments


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import org.supportcompact.ktx.toast

/**
 * A simple [Fragment] subclass.
 * Use the [InProgressTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class InProgressTransactionsFragment : CoreParentFragment<TransactionsVM, com.tarrakki.databinding.FragmentInProgressTransactionsBinding>() {

    lateinit var response: Observer<TransactionApiResponse>

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
        response = Observer {
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
                        if (binder is RowInprogressTransactionsBinding && item is TransactionApiResponse.Transaction && item.displayStatus) {
                            binder.imgArrow.setOnClickListener {
                                item.isSelected = !item.isSelected
                            }
                            val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatuss>()
                            if ("R".equals(item.buySell, true)) {
                                getViewModel().setRedeemData(statuslist, "${item.withdrawalSent}", "${item.withdrawalConfirm}", "${item.amountCreadited}", item.isRelianceRedemption == true)
                            } else {
                                getViewModel().setData(statuslist, "${item.payment}", "${item.orderPlaced}", "${item.unitsAllocated}", item.paymentType)
                            }
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
                        if (item is LoadMore && !item.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = position
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

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                tvNoItem?.visibility = if (inProgressTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        mRefresh?.setOnRefreshListener(refreshListener)

        getViewModel().onRefresh.observe(this, Observer {
            mRefresh?.post {
                mRefresh?.isRefreshing = true
                refreshListener.onRefresh()
            }
        })
    }

    val refreshListener = androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {
        getViewModel().getTransactions(
                transactionType = TransactionApiResponse.IN_PROGRESS,
                mRefresh = true).observe(this, response)
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
