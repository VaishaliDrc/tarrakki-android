package com.tarrakki.module.transactions.childfragments


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
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
import org.supportcompact.BR
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.simpleAlert
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
        binding.setVariable(BR.paynow, View.OnClickListener {
            if (getViewModel().hasOptionMenu.value == true) {
                val transactions = ArrayList<TransactionApiResponse.Transaction>()
                unpaidTransactions.forEach {
                    if (it is TransactionApiResponse.Transaction && it.isSelected) {
                        transactions.add(it)
                    }
                }
                startFragment(PaymentModeFragment.newInstance(), R.id.frmContainer)
                repostSticky(transactions)
                getViewModel().hasOptionMenu.value = false
            } else {
                context?.simpleAlert(getString(R.string.please_select_fund_to_payment))
            }
        })
    }

    private val unpaidTransactions = arrayListOf<WidgetsViewModel>()
    val loadMore = LoadMore()

    lateinit var response: Observer<TransactionApiResponse>

    override fun createReference() {

        val loadMoreObservable = MutableLiveData<Int>()

        response = Observer {
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
                        binder.root.setOnLongClickListener { v: View? ->
                            if (item is TransactionApiResponse.Transaction && !getViewModel().isFromRaiseTicket) {
                                item.isSelected = !item.isSelected
                                hasSelectedItem()
                                true
                            } else {
                                false
                            }
                        }
                        binder.executePendingBindings()
                        if (!getViewModel().isFromRaiseTicket) {
                            binder.root.setOnClickListener {
                                if (item is TransactionApiResponse.Transaction) {
                                    if (getViewModel().hasOptionMenu.value == true) {
                                        item.isSelected = !item.isSelected
                                    }
                                }
                                hasSelectedItem()
                            }
                        }
                        if (item is LoadMore && !item.isLoading) {
                            loadMore.isLoading = true
                            loadMoreObservable.value = position
                        }
                    }
                } else {
                    rvUnpaidTransactions?.adapter?.notifyDataSetChanged()
                }
                tvNoItem?.visibility = if (unpaidTransactions.isEmpty()) View.VISIBLE else View.GONE
                btnPayment?.visibility = if (unpaidTransactions.isEmpty() || getViewModel().isFromRaiseTicket) View.GONE else View.VISIBLE
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
                btnPayment?.visibility = if (unpaidTransactions.isEmpty() || getViewModel().isFromRaiseTicket) View.GONE else View.VISIBLE
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

        getViewModel().onRefresh.observe(this, Observer {
            mRefresh?.post {
                mRefresh?.isRefreshing = true
                refreshListener.onRefresh()
            }
        })
    }

    val refreshListener = androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.delete_menu, menu)
        val mView = menu?.findItem(R.id.action_delete)?.actionView
        val tvDelete = mView?.findViewById<TextView>(R.id.tvDelete)
        val tvCancel = mView?.findViewById<TextView>(R.id.tvCancel)
        val cbSelectAll = mView?.findViewById<CheckBox>(R.id.cbSelectAll)
        tvDelete?.setOnClickListener {
            context?.confirmationDialog(getString(R.string.alert_are_you_sure_to_delete_transactions), btnPositiveClick = {
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

    override fun onEvent(event: Event) {
        when (event) {
            Event.RESET_OPTION_MENU -> {
                getViewModel().hasOptionMenu.value = false
            }
            else -> super.onEvent(event)
        }
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
}
