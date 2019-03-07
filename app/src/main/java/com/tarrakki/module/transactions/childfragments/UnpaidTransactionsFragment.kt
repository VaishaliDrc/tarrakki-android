package com.tarrakki.module.transactions.childfragments


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.google.gson.JsonArray
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.TransactionApiResponse
import com.tarrakki.databinding.FragmentUnpaidTransactionsBinding
import com.tarrakki.module.paymentmode.PaymentModeFragment
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.module.transactions.TransactionsVM
import kotlinx.android.synthetic.main.fragment_unpaid_transactions.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.startFragment


/**
 * A simple [Fragment] subclass.
 * Use the [UnpaidTransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UnpaidTransactionsFragment : CoreParentFragment<TransactionsVM, FragmentUnpaidTransactionsBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_unpaid_transactions
    }

    override fun createViewModel(): Class<out TransactionsVM> {
        return TransactionsVM::class.java
    }

    override fun setVM(binding: FragmentUnpaidTransactionsBinding) {

    }

    private val unpaidTransactions = arrayListOf<WidgetsViewModel>()
    val loadMore = LoadMore()

    lateinit var response : Observer<TransactionApiResponse>

    override fun createReference() {

        val loadMoreObservable = MutableLiveData<Int>()

        response = Observer<TransactionApiResponse> {
            it?.let { data ->
                unpaidTransactions.remove(loadMore)
                loadMore.isLoading = false
                if (mRefresh?.isRefreshing == true) {
                    unpaidTransactions.clear()
                    mRefresh?.isRefreshing = false
                }
                if (data.transactions?.isNotEmpty() == true) {
                    unpaidTransactions.addAll(data.transactions)
                }
                if (unpaidTransactions.size >= 10 && data.totalCount > unpaidTransactions.size) {
                    unpaidTransactions.add(loadMore)
                }
                if (rvUnpaidTransactions?.adapter == null) {
                    rvUnpaidTransactions?.setUpMultiViewRecyclerAdapter(unpaidTransactions) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                        binder.setVariable(BR.data, item)
                        binder.setVariable(BR.statusVisibility, View.GONE)
                        binder.setVariable(BR.paynow, View.OnClickListener {
                            startFragment(PaymentModeFragment.newInstance(), R.id.frmContainer)
                            postSticky(item as TransactionApiResponse.Transaction)
                        })

                        binder.root.setOnLongClickListener { v: View? ->
                            if (item is TransactionApiResponse.Transaction) {
                                item.isSelected = !item.isSelected
                                hasSelectedItem()
                                true
                            } else {
                                false
                            }
                        }
                        binder.root.setOnClickListener {
                            if (item is TransactionApiResponse.Transaction) {
                                if (getViewModel().hasOptionMenu.value == true) {
                                    item.isSelected = !item.isSelected
                                }
                            }
                            hasSelectedItem()
                        }
                        binder.executePendingBindings()
                        if (item is LoadMore && !item.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = position
                        }
                    }
                } else {
                    rvUnpaidTransactions?.adapter?.notifyDataSetChanged()
                }
                tvNoItem?.visibility = if (unpaidTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        getViewModel().getTransactions(transactionType = TransactionApiResponse.UNPAID).observe(this, response)
        loadMoreObservable.observe(this, Observer {
            it?.let { offset ->
                Handler().postDelayed({
                    getViewModel().getTransactions(
                            transactionType = TransactionApiResponse.UNPAID,
                            offset = offset).observe(this, response)
                }, 2500)
            }
        })

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let {
                mRefresh?.isRefreshing = false
                tvNoItem?.visibility = if (unpaidTransactions.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        getViewModel().hasOptionMenu.observe(this, Observer {
            it?.let { hasOptionsMenu ->
                coreActivityVM?.titleVisibility?.set(!hasOptionsMenu)
                setHasOptionsMenu(hasOptionsMenu)
                if (!hasOptionsMenu) {
                    unpaidTransactions.forEach { item ->
                        if (item is TransactionApiResponse.Transaction)
                            item.isSelected = false
                    }
                }
                if (activity is AppCompatActivity) {
                    (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(hasOptionsMenu)
                }
            }
        })
        getViewModel().onBack.observe(this, Observer {
            if (getViewModel().hasOptionMenu.value == true) {
                getViewModel().hasOptionMenu.value = false
            } else {
                onBack()
            }
        })

        mRefresh?.setOnRefreshListener(refreshListener)
    }

    val refreshListener =  SwipeRefreshLayout.OnRefreshListener  {
        getViewModel().hasOptionMenu.value = false
        getViewModel().getTransactions(
                transactionType = TransactionApiResponse.UNPAID,
                mRefresh = true).observe(this, response)
    }

    private fun hasSelectedItem() {
        val count = unpaidTransactions.count {
            it is TransactionApiResponse.Transaction && it.isSelected
        }
        getViewModel().hasOptionMenu.value = count != 0
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = count.toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.delete_menu, menu)
        val mView = menu?.findItem(R.id.action_delete)?.actionView
        val tvDelete = mView?.findViewById<TextView>(R.id.tvDelete)
        val tvCancel = mView?.findViewById<TextView>(R.id.tvCancel)
        val cbSelectAll = mView?.findViewById<CheckBox>(R.id.cbSelectAll)
        tvDelete?.setOnClickListener {
            context?.confirmationDialog("Are you sure to delete the selected transaction records?", btnPositiveClick = {
                val jsonArray = JsonArray()
                unpaidTransactions.forEach {
                    if (it is TransactionApiResponse.Transaction && it.isSelected) {
                        jsonArray.add(it.id)
                    }
                }
                getViewModel().deleteTransactions(jsonArray).observe(this, Observer {
                    it?.let {
                        val deleteData = unpaidTransactions.filter { it is TransactionApiResponse.Transaction && it.isSelected }
                        unpaidTransactions.removeAll(deleteData)
                        if (unpaidTransactions.size <= 10) {
                            unpaidTransactions.remove(loadMore)
                        }
                        tvNoItem?.visibility = if (unpaidTransactions.isEmpty()) View.VISIBLE else View.GONE
                        rvUnpaidTransactions?.adapter?.notifyDataSetChanged()
                        getViewModel().hasOptionMenu.value = false
                    }
                })
            })
        }
        tvCancel?.setOnClickListener {
            getViewModel().hasOptionMenu.value = false
        }
        cbSelectAll?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                unpaidTransactions.forEach {
                    if (it is TransactionApiResponse.Transaction)
                        it.isSelected = true
                }
                hasSelectedItem()
            } else {
                getViewModel().hasOptionMenu.value = false
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment UnpaidTransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UnpaidTransactionsFragment().apply { arguments = basket }
    }

    @Subscribe(sticky = true)
    fun onEventData(event: Event) {
        if (event==Event.ISFROMTRANSACTIONSUCCESS){
            mRefresh?.post {
                mRefresh?.isRefreshing = true
                refreshListener.onRefresh()
            }
        }
    }
}
