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
import com.tarrakki.databinding.FragmentFailedTransactionBinding
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_failed_transaction.*
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [FailedTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class FailedTransactionFragment : CoreParentFragment<TransactionsVM, FragmentFailedTransactionBinding>() {

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
        val response = Observer<TransactionApiResponse> {
            it?.let { data ->
                failedTransactions.remove(loadMore)
                loadMore.loadMore = false
                if (mRefresh?.isRefreshing == true) {
                    failedTransactions.clear()
                    mRefresh?.isRefreshing = false
                }
                if (data.transactions?.isNotEmpty() == true) {
                    failedTransactions.addAll(data.transactions)
                }
                if (failedTransactions.isNotEmpty()) {
                    failedTransactions.add(loadMore)
                }
                if (rvFailedTransactions?.adapter == null) {
                    rvFailedTransactions?.setUpMultiViewRecyclerAdapter(failedTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, View.GONE)
                        binder.executePendingBindings()
                        if (position >= 9 && failedTransactions.size - 1 == position && !loadMore.loadMore) {
                            loadMore.loadMore = true
                            loadMoreObservable.value = data.offset
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
        mRefresh?.setOnRefreshListener {
            getViewModel().getTransactions(
                    transactionType = TransactionApiResponse.FAILED,
                    mRefresh = true).observe(this, response)
        }
        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                tvNoItem?.visibility = if (failedTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        })
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
