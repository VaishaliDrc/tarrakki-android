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

        val allTransactions = arrayListOf<WidgetsViewModel>()
        val loadMoreObservable = MutableLiveData<Int>()
        val loadMore = LoadMore()
        val response = Observer<TransactionApiResponse> {
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
                        binder.setVariable(BR.statusVisibility, View.VISIBLE)
                        binder.executePendingBindings()
                        if (position >= 9 && allTransactions.size - 1 == position && !loadMore.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = data.offset
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
        mRefresh?.setOnRefreshListener {
            getViewModel().getTransactions(mRefresh = true).observe(this, response)
        }
        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                //tvNoItem?.visibility = if (allTransactions.isEmpty()) View.VISIBLE else View.GONE
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
