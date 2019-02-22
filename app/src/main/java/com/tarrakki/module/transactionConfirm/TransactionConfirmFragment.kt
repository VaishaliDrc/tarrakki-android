package com.tarrakki.module.transactionConfirm


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.google.gson.JsonObject
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.model.TransactionStatus
import com.tarrakki.api.model.printRequest
import com.tarrakki.databinding.FragmentTransactionConfirmBinding
import com.tarrakki.databinding.RowTransactionConfirmBinding
import com.tarrakki.databinding.RowTransactionStatusItemBinding
import com.tarrakki.module.paymentmode.SUCCESSTRANSACTION
import kotlinx.android.synthetic.main.fragment_transaction_confirm.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.ktx.getUserId
import org.json.JSONObject



class TransactionConfirmFragment : CoreFragment<TransactionConfirmVM, FragmentTransactionConfirmBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.transaction_confirm)

    override fun getLayout(): Int {
        return R.layout.fragment_transaction_confirm
    }

    var transactionList: List<TransactionStatus> = arrayListOf()

    override fun createReference() {
        // setOrderItemsAdapter(getViewModel().list)

        val success_transactions = arguments?.getString(SUCCESSTRANSACTION, "")
        if (!success_transactions.isNullOrEmpty()) {
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
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Mutual Fund Payment", "via Net Banking", funds.payment))
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Order Placed with AMC", "", funds.orderPlaced))
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Investment Confirmation", "", funds.investmentConfirmation))
                        statuslist.add(TransactionConfirmVM.TranscationStatuss("Units Alloted", "", funds.unitsAlloted))
                        transactionStatus.add(TransactionStatus("", funds.amount, 0, funds.orderType, funds.schemeName, true, statuslist as MutableList<TransactionConfirmVM.TranscationStatuss>))
                    }
                    if (transactionList.isNotEmpty()) {
                        transactionStatus.addAll(transactionList)
                    }
                    setOrderItemsAdapter(transactionStatus)
                }
            })
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
                        val statusAdapter = setUpAdapter(item.status as MutableList<TransactionConfirmVM.TranscationStatuss>,
                                ChoiceMode.NONE,
                                R.layout.row_transaction_list_item,
                                { item1, binder1: RowTransactionStatusItemBinding?, position1, adapter ->
                                    binder1?.widget = item1
                                    binder1?.executePendingBindings()
                                    if (position1 == item.status.size - 1) {
                                        binder1?.verticalDivider?.visibility = View.GONE
                                    } else {
                                        binder1?.verticalDivider?.visibility = View.VISIBLE
                                    }

                                }, { item1, position1, adapter1 ->

                        }, false)
                        binder?.rvTransactionStatus?.adapter = statusAdapter
                    }
                }, { item, position, adapter ->
        }, false)
        rvOrderItems?.adapter = adapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: List<TransactionStatus>) {
        transactionList = data
        setOrderItemsAdapter(data)
        removeStickyEvent(data)
    }

}
