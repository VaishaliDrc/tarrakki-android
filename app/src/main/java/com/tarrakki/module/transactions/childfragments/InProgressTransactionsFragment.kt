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
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_in_progress_transactions.*
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter

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
                loadMore.loadMore = false
                if (mRefresh?.isRefreshing == true) {
                    inProgressTransactions.clear()
                    mRefresh?.isRefreshing = false
                }
                if (data.transactions?.isNotEmpty() == true) {
                    inProgressTransactions.addAll(data.transactions)
                }
                if (inProgressTransactions.isNotEmpty()) {
                    inProgressTransactions.add(loadMore)
                }
                if (rvInProgressTransactions?.adapter == null) {
                    rvInProgressTransactions?.setUpMultiViewRecyclerAdapter(inProgressTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, View.GONE)
                        binder.executePendingBindings()
                        if (position >= 9 && inProgressTransactions.size - 1 == position && !loadMore.loadMore) {
                            loadMore.loadMore = true
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
