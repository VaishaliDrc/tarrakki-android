package com.tarrakki.module.transactions.childfragments


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.TransactionApiResponse
import com.tarrakki.databinding.FragmentCompletedTransactionsBinding
import com.tarrakki.databinding.RowCompletedTransactionsBinding
import com.tarrakki.databinding.RowTransactionListStatusBinding
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_completed_transactions.*
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [CompletedTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CompletedTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentCompletedTransactionsBinding>() {

    lateinit var response: Observer<TransactionApiResponse>

    override fun getLayout(): Int {
        return R.layout.fragment_completed_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentCompletedTransactionsBinding) {

    }

    override fun createReference() {

        val completedTransactions = arrayListOf<WidgetsViewModel>()
        val loadMoreObservable = MutableLiveData<Int>()
        val loadMore = LoadMore()
        response = Observer {
            it?.let { data ->
                completedTransactions.remove(loadMore)
                loadMore.isLoading = false
                if (mRefresh?.isRefreshing == true) {
                    completedTransactions.clear()
                    mRefresh?.isRefreshing = false
                }
                if (data.transactions?.isNotEmpty() == true) {
                    completedTransactions.addAll(data.transactions)
                }
                if (completedTransactions.size >= 10 && data.totalCount > completedTransactions.size) {
                    completedTransactions.add(loadMore)
                }
                if (rvCompletedTransactions?.adapter == null) {
                    rvCompletedTransactions?.setUpMultiViewRecyclerAdapter(completedTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, View.GONE)
                        if (binder is RowCompletedTransactionsBinding && item is TransactionApiResponse.Transaction && item.displayStatus) {
                            binder.imgArrow.setOnClickListener {
                                item.isSelected = !item.isSelected
                            }
                            val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatuss>()
                            if ("R".equals(item.buySell, true)) {
                                getViewModel().setRedeemData(statuslist, "${item.withdrawalSent}", "${item.withdrawalConfirm}", "${item.amountCreadited}", item.isRelianceRedemption == true)
                            } else {
                                getViewModel().setData(statuslist, "${item.payment}", "${item.orderPlaced}", "${item.unitsAllocated}", item.paymentType)
                            }
                            val adapter = binder.rvTransactionStatus.setUpRecyclerView(R.layout.row_transaction_list_status, statuslist)
                            { item2: TransactionConfirmVM.TranscationStatuss, binder2: RowTransactionListStatusBinding, position2: Int ->
                                binder2.widget = item2
                                binder2.executePendingBindings()
                                if (position2 == statuslist.size - 1) {
                                    binder2.verticalDivider.visibility = View.GONE
                                } else {
                                    binder2.verticalDivider.visibility = View.VISIBLE
                                }
                            }
                            binder.rvTransactionStatus.adapter = adapter
                        }
                        binder.executePendingBindings()
                        if (item is LoadMore && !item.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = position
                        }
                    }
                } else {
                    rvCompletedTransactions?.adapter?.notifyDataSetChanged()
                }
                tvNoItem?.visibility = if (completedTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        getViewModel().getTransactions(transactionType = TransactionApiResponse.COMPLETED).observe(this, response)
        loadMoreObservable.observe(this, Observer {
            it?.let { offset ->
                Handler().postDelayed({
                    getViewModel().getTransactions(
                            transactionType = TransactionApiResponse.COMPLETED,
                            offset = offset).observe(this, response)
                }, 2500)
            }
        })

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                tvNoItem?.visibility = if (completedTransactions.isEmpty()) View.VISIBLE else View.GONE
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

    val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        getViewModel().getTransactions(
                transactionType = TransactionApiResponse.COMPLETED,
                mRefresh = true).observe(this, response)
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
