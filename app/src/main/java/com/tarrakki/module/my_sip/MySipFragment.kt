package com.tarrakki.module.my_sip


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.MenuItem
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.*
import com.tarrakki.databinding.FragmentMySipBinding
import com.tarrakki.databinding.RowCompletedTransactionsBinding
import com.tarrakki.databinding.RowTransactionListStatusBinding
import com.tarrakki.module.portfolio.StopSIP
import com.tarrakki.module.redeem.RedeemStopConfirmationFragment
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.stopFundPortfolioDialog
import kotlinx.android.synthetic.main.fragment_completed_transactions.*
import kotlinx.android.synthetic.main.fragment_completed_transactions.mRefresh
import kotlinx.android.synthetic.main.fragment_completed_transactions.tvNoItem
import kotlinx.android.synthetic.main.fragment_my_sip.*
import kotlinx.android.synthetic.main.row_my_sip.view.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.BR
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.events.Event
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
const val SET_SELECTED_PAGE = "set_selected_page"

class MySipFragment : CoreFragment<MySipVM, FragmentMySipBinding>() {

    lateinit var response: Observer<MySipApiResponse>

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.my_sip)

    override fun getLayout(): Int {
        return R.layout.fragment_my_sip
    }

    override fun createViewModel(): Class<out MySipVM> {
        return MySipVM::class.java
    }

    override fun setVM(binding: FragmentMySipBinding) {

    }
    val completedTransactions = arrayListOf<WidgetsViewModel>()

    override fun createReference() {
        setHasOptionsMenu(true)

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
                if (data.mySipData?.isNotEmpty() == true) {
                    completedTransactions.addAll(data.mySipData)
                }
                if (completedTransactions.size >= 10 && data.totalCount > completedTransactions.size) {
                    completedTransactions.add(loadMore)
                }
                if (rvMySip?.adapter == null) {
                    rvMySip?.setUpMultiViewRecyclerAdapter(completedTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.onStopClick, View.OnClickListener {

                            val item1 = item as MySipData
                            context?.stopFundPortfolioDialog(item1) { transactionId, folio, date ->

                                val data = StopSIP(transactionId, folio, date)
                                startFragment(RedeemStopConfirmationFragment.newInstance(isRedeemReq = false), R.id.frmContainer)
                                repostSticky(data)
                            }

                        })
                        binder.executePendingBindings()

                        if (item is LoadMore && !item.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = position
                        }
                    }
                } else {
                    rvMySip?.adapter?.notifyDataSetChanged()
                }
                tvNoItem?.visibility = if (completedTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        loadMoreObservable.observe(this, Observer {
            it?.let { offset ->
                Handler().postDelayed({
                    getViewModel().getMySipRecords(
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
        getViewModel().getMySipRecords().observe(this, response)
        getViewModel().onRefresh.observe(this, Observer {
            mRefresh?.post {
                mRefresh?.isRefreshing = true
                refreshListener.onRefresh()
            }
        })

    }

    val refreshListener = androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {
        getViewModel().getMySipRecords(
                mRefresh = true).observe(this, response)
    }

    override fun onResume() {
        super.onResume()
        getBinding().root.requestFocus()
        if (App.INSTANCE.needToLoadTransactionScreen >= 0) {
            completedTransactions.clear()
            rvMySip?.adapter?.notifyDataSetChanged()
            getViewModel().getMySipRecords().observe(this, response)
            App.INSTANCE.needToLoadTransactionScreen = -1
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
//                if (mPager.currentItem == 4) {
//                    getViewModel().onBack.value = true
//                } else {
                onBack()
//                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param basket As Bundle.
         * @return A new instance of fragment TransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = MySipFragment().apply { arguments = basket }
    }

    @Subscribe(sticky = true)
    fun onEventData(event: Event) {
        if (event == Event.ISFROMTRANSACTIONSUCCESS) {
            getViewModel().onRefresh.value = true
        }
        removeStickyEvent(event)
    }
}
