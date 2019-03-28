package com.tarrakki.module.transactions.childfragments


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.TransactionApiResponse
import com.tarrakki.databinding.FragmentUpcomingTransactionsBinding
import com.tarrakki.module.portfolio.PortfolioVM
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_upcoming_transactions.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event

/**
 * A simple [Fragment] subclass.
 * Use the [UpcomingTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UpcomingTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentUpcomingTransactionsBinding>() {

    lateinit var response : Observer<TransactionApiResponse>

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
        response = Observer {
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
                if (upcomingTransactions.size >= 10 && data.totalCount > upcomingTransactions.size) {
                    upcomingTransactions.add(loadMore)
                }
                if (rvUpcomingTransactions?.adapter == null) {
                    rvUpcomingTransactions?.setUpMultiViewRecyclerAdapter(upcomingTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, View.GONE)
                        binder.executePendingBindings()
                        if (item is LoadMore && !item.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = position
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

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                tvNoItem?.visibility = if (upcomingTransactions.isEmpty()) View.VISIBLE else View.GONE
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
                transactionType = TransactionApiResponse.UPCOMING,
                mRefresh = true).observe(this, response)
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
