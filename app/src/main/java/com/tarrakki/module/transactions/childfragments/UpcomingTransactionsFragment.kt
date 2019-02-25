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
import com.tarrakki.databinding.FragmentUpcomingTransactionsBinding
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_upcoming_transactions.*
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [UpcomingTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UpcomingTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentUpcomingTransactionsBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_upcoming_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentUpcomingTransactionsBinding) {

    }

    override fun createReference() {

        val upcomingTransactions = arrayListOf<WidgetsViewModel>()
        val loadMoreObservable = MutableLiveData<Int>()
        val loadMore = LoadMore()
        val response = Observer<TransactionApiResponse> {
            it?.let { data ->
                upcomingTransactions.remove(loadMore)
                loadMore.isLoading = false
                if (mRefresh?.isRefreshing == true) {
                    upcomingTransactions.clear()
                    mRefresh?.isRefreshing = false
                }
                if (data.transactions?.isNotEmpty() == true) {
                    upcomingTransactions.addAll(data.transactions)
                }
                if (upcomingTransactions.size >= 10) {
                    upcomingTransactions.add(loadMore)
                }
                if (rvUpcomingTransactions?.adapter == null) {
                    rvUpcomingTransactions?.setUpMultiViewRecyclerAdapter(upcomingTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, View.GONE)
                        binder.executePendingBindings()
                        if (position >= 9 && upcomingTransactions.size - 1 == position && !loadMore.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = data.offset
                        }
                    }
                } else {
                    rvUpcomingTransactions?.adapter?.notifyDataSetChanged()
                }
                tvNoItem?.visibility = if (upcomingTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        getViewModel().getTransactions(transactionType = TransactionApiResponse.UPCOMING).observe(this, response)
        loadMoreObservable.observe(this, Observer {
            it?.let { offset ->
                Handler().postDelayed({
                    getViewModel().getTransactions(
                            transactionType = TransactionApiResponse.UPCOMING,
                            offset = offset).observe(this, response)
                }, 2500)
            }
        })
        mRefresh?.setOnRefreshListener {
            getViewModel().getTransactions(
                    transactionType = TransactionApiResponse.UPCOMING,
                    mRefresh = true).observe(this, response)
        }
        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                tvNoItem?.visibility = if (upcomingTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment UpcomingTransactionsFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UpcomingTransactionsFragment().apply { arguments = basket }
    }
}
