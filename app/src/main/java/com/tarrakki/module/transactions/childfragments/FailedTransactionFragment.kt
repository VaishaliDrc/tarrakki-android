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
import com.tarrakki.databinding.FragmentFailedTransactionBinding
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_failed_transaction.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event

/**
 * A simple [Fragment] subclass.
 * Use the [FailedTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class FailedTransactionFragment : CoreParentFragment<TransactionsVM, FragmentFailedTransactionBinding>() {

    lateinit var response : Observer<TransactionApiResponse>

    override fun getLayout(): Int {
        return R.layout.fragment_failed_transaction
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentFailedTransactionBinding) {

    }

    override fun createReference() {
        val failedTransactions = arrayListOf<WidgetsViewModel>()
        val loadMoreObservable = MutableLiveData<Int>()
        val loadMore = LoadMore()
        response = Observer {
            it?.let { data ->
                failedTransactions.remove(loadMore)
                loadMore.isLoading = false
                if (mRefresh?.isRefreshing == true) {
                    failedTransactions.clear()
                    mRefresh?.isRefreshing = false
                }
                if (data.transactions?.isNotEmpty() == true) {
                    failedTransactions.addAll(data.transactions)
                }
                if (failedTransactions.size >= 10 && data.totalCount > failedTransactions.size) {
                    failedTransactions.add(loadMore)
                }
                if (rvFailedTransactions?.adapter == null) {
                    rvFailedTransactions?.setUpMultiViewRecyclerAdapter(failedTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, View.GONE)
                        binder.executePendingBindings()
                        if (item is LoadMore && !item.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = position
                        }
                    }
                } else {
                    rvFailedTransactions?.adapter?.notifyDataSetChanged()
                }
                tvNoItem?.visibility = if (failedTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        getViewModel().getTransactions(transactionType = TransactionApiResponse.FAILED).observe(this, response)
        loadMoreObservable.observe(this, Observer {
            it?.let { offset ->
                Handler().postDelayed({
                    getViewModel().getTransactions(
                            transactionType = TransactionApiResponse.FAILED,
                            offset = offset).observe(this, response)
                }, 2500)
            }
        })

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                tvNoItem?.visibility = if (failedTransactions.isEmpty()) View.VISIBLE else View.GONE
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

    val refreshListener =  SwipeRefreshLayout.OnRefreshListener  {
        getViewModel().getTransactions(
                transactionType = TransactionApiResponse.FAILED,
                mRefresh = true).observe(this, response)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment FailedTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = FailedTransactionFragment().apply { arguments = basket }
    }
}
