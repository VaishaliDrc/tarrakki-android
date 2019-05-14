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
import com.tarrakki.databinding.*
import com.tarrakki.module.paymentmode.PaymentModeFragment
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_all_transactions.*
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [AllTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentAllTransactionsBinding>() {

    lateinit var response: Observer<TransactionApiResponse>

    override fun getLayout(): Int {
        return R.layout.fragment_all_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentAllTransactionsBinding) {

    }

    override fun createReference() {

        val allTransactions = arrayListOf<WidgetsViewModel>()
        val loadMoreObservable = MutableLiveData<Int>()
        val loadMore = LoadMore()
        response = Observer {
            it?.let { data ->
                allTransactions.remove(loadMore)
                loadMore.isLoading = false
                if (mRefresh?.isRefreshing == true) {
                    allTransactions.clear()
                    mRefresh?.isRefreshing = false
                }
                if (data.transactions?.isNotEmpty() == true) {
                    allTransactions.addAll(data.transactions)
                }
                if (allTransactions.size >= 10 && data.totalCount > allTransactions.size) {
                    allTransactions.add(loadMore)
                }
                if (rvAllTransactions?.adapter == null) {
                    rvAllTransactions?.setUpMultiViewRecyclerAdapter(allTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, if (getViewModel().isFromRaiseTicket) View.GONE else View.VISIBLE)

                        if (binder is RowUnpaidTransactionsBinding) {
                            binder.setVariable(BR.paynow, View.OnClickListener {
                                startFragment(PaymentModeFragment.newInstance(), R.id.frmContainer)
                                postSticky(item as TransactionApiResponse.Transaction)
                            })
                        }
                        if (item is TransactionApiResponse.Transaction && item.displayStatus) {
                            setStatusView(binder, item)
                        }
                        binder.executePendingBindings()
                        if (item is LoadMore && !item.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = position
                        }
                    }
                } else {
                    rvAllTransactions?.adapter?.notifyDataSetChanged()
                }
                tvNoItem?.visibility = if (allTransactions.isEmpty()) View.VISIBLE else View.GONE
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
        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                //tvNoItem?.visibility = if (allTransactions.isEmpty()) View.VISIBLE else View.GONE
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
        getViewModel().getTransactions(mRefresh = true).observe(this, response)
    }

    private fun <T : ViewDataBinding> setStatusView(binder: T, data: TransactionApiResponse.Transaction) {
        if (binder is RowInprogressTransactionsBinding) {
            binder.imgArrow.setOnClickListener {
                data.isSelected = !data.isSelected
            }
            val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatuss>()
            if ("R".equals(data.buySell, true)) {
                getViewModel().setRedeemData(statuslist, "${data.withdrawalSent}", "${data.withdrawalConfirm}", "${data.amountCreadited}", data.isRelianceRedemption == true)
            } else {
                getViewModel().setData(statuslist, "${data.payment}", "${data.orderPlaced}", "${data.unitsAllocated}", data.paymentType)
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
        } else if (binder is RowCompletedTransactionsBinding) {
            binder.imgArrow.setOnClickListener {
                data.isSelected = !data.isSelected
            }
            val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatuss>()
            if ("R".equals(data.buySell, true)) {
                getViewModel().setRedeemData(statuslist, "${data.withdrawalSent}", "${data.withdrawalConfirm}", "${data.amountCreadited}", data.isRelianceRedemption == true)
            } else {
                getViewModel().setData(statuslist, "${data.payment}", "${data.orderPlaced}", "${data.unitsAllocated}", data.paymentType)
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
