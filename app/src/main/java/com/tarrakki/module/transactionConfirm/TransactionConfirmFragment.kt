package com.tarrakki.module.transactionConfirm


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.model.TransactionStatus
import com.tarrakki.api.model.printRequest
import com.tarrakki.databinding.FragmentTransactionConfirmBinding
import com.tarrakki.databinding.RowTransactionConfirmBinding
import com.tarrakki.databinding.RowTransactionListStatusBinding
import com.tarrakki.module.account.AccountActivity
import com.tarrakki.module.confirmorder.ConfirmOrderFragment
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.invest.InvestActivity
import com.tarrakki.module.paymentmode.ISFROMTRANSACTIONMODE
import com.tarrakki.module.paymentmode.SUCCESSTRANSACTION
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_transaction_confirm.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONObject
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.e
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.startActivity

class TransactionConfirmFragment : CoreFragment<TransactionConfirmVM, FragmentTransactionConfirmBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.transaction_confirm)

    var isFromPaymentMode : Boolean? = false

    override fun getLayout(): Int {
        return R.layout.fragment_transaction_confirm
    }

    var transactionList: List<TransactionStatus> = arrayListOf<TransactionStatus>()

    override fun createReference() {
        setHasOptionsMenu(true)

        val success_transactions = arguments?.getString(SUCCESSTRANSACTION, "")
        if (!success_transactions.isNullOrEmpty()) {
            isFromPaymentMode = true
            val json = JSONObject()
            json.put("user_id", context?.getUserId())
            json.put("success_transaction_ids", JSONArray(success_transactions))
            val authData = AES.encrypt(json.toString())
            json.toString().printRequest()
            getViewModel().getTransactionStatus(authData).observe(this, Observer {
                if (it?.data?.isNotEmpty() == true) {
                    val transactionStatus: MutableList<TransactionStatus> = arrayListOf()
                    for (funds in it.data) {
                        val statuslist = arrayListOf<TransactionConfirmVM.TranscationStatuss>()
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Mutual Fund Payment", funds.paymentType, funds.payment))
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Order Placed with AMC", "", funds.orderPlaced))
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Investment Confirmation", "", funds.investmentConfirmation))
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Units Alloted", "", funds.unitsAlloted))
                        transactionStatus.add(TransactionStatus("", funds.amount, 0, funds.orderType, funds.schemeName, true, statuslist as MutableList<TransactionConfirmVM.TranscationStatuss>))
                    }
                    if (transactionList.isNotEmpty()) {
                        if (arguments?.getBoolean(ISFROMTRANSACTIONMODE)!=true) {
                            transactionStatus.addAll(transactionList)
                        }
                    }
                    setOrderItemsAdapter(transactionStatus)
                }
            })
        }

        btnExploreAllFunds?.setOnClickListener {
            onExploreFunds()
        }

        getBinding().root.isFocusableInTouchMode = true
        getBinding().root.requestFocus()
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPress()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun onExploreFunds(){
        activity?.let {
            if (it is BaseActivity) {
                if (it is HomeActivity || it is AccountActivity) {
                    it.startActivity<InvestActivity>()
                } else {
                    it.supportFragmentManager?.backStackEntryCount?.minus(1)?.let { it1 -> onBack(it1) }
                }
            }
        }
    }

    override fun createViewModel(): Class<out TransactionConfirmVM> {
        return TransactionConfirmVM::class.java
    }

    override fun setVM(binding: FragmentTransactionConfirmBinding) {
        getBinding().vm = getViewModel()
        getBinding().executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = TransactionConfirmFragment().apply { arguments = basket }
    }

    private fun setOrderItemsAdapter(list: List<TransactionStatus>) {
        val adapter = setUpAdapter(list as MutableList<TransactionStatus>,
                ChoiceMode.NONE,
                R.layout.row_transaction_confirm,
                { item, binder: RowTransactionConfirmBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()

                    binder?.imgArrow?.setOnClickListener {
                        binder.expStatus.toggle()
                        binder.isExpanded = binder.expStatus.isExpanded
                    }

                    if (item.isSuccess) {
                        val transactionAdapter = setUpAdapter(item.status as
                                MutableList<TransactionConfirmVM.TranscationStatuss>,
                                ChoiceMode.NONE,
                                R.layout.row_transaction_list_status, { item1, binder1: RowTransactionListStatusBinding?, position1, adapter1 ->
                            binder1?.widget = item1
                            binder1?.executePendingBindings()
                            if (position1 == item.status.size - 1) {
                                binder1?.verticalDivider?.visibility = View.GONE
                            } else {
                                binder1?.verticalDivider?.visibility = View.VISIBLE
                            }
                        }, { item, position, adapter ->

                        })
                        binder?.rvTransactionStatus?.adapter = transactionAdapter
                    }
                }, { item, position, adapter ->
        }, false)
        rvOrderItems?.adapter = adapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: ConfirmOrderFragment.FailedTransactions) {
        transactionList = data.transactions
        setOrderItemsAdapter(data.transactions)
        removeStickyEvent(data)
        e("Failed Data",transactionList.toString())
    }

    fun onBackPress(){
        if (arguments?.getBoolean(ISFROMTRANSACTIONMODE)==true){
            onBack(2)
            postSticky(Event.ISFROMTRANSACTIONSUCCESS)
        }else{
            if (isFromPaymentMode==true){
                onBack(3)
            }else {
                onBack(2)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPress()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
