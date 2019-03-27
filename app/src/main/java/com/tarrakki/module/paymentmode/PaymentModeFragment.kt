package com.tarrakki.module.paymentmode

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.model.*
import com.tarrakki.databinding.FragmentPaymentModeBinding
import com.tarrakki.databinding.RowListPaymentFundsItemBinding
import com.tarrakki.getDefaultBank
import com.tarrakki.module.bankmandate.AddBankMandateFragment
import com.tarrakki.module.netbanking.NET_BANKING_PAGE
import com.tarrakki.module.netbanking.NetBankingFragment
import com.tarrakki.module.transactionConfirm.TransactionConfirmFragment
import com.tarrakki.module.webviewActivity.WebviewActivity
import kotlinx.android.synthetic.main.fragment_payment_mode.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONObject
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.utilise.DividerItemDecoration
import java.math.BigDecimal

const val BANKACCOUNTNUMBER = "bankaccountnumber"
const val ISFROMPAYMENTMODE = "isFromPaymentMode"
const val SUCCESSTRANSACTION = "successtransactions"
const val ISFROMTRANSACTIONMODE = "isFromTransactionMode"

class PaymentModeFragment : CoreFragment<PaymentModeVM, FragmentPaymentModeBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.payment_mode)

    var isFromTransaction: Boolean? = false

    override fun getLayout(): Int {
        return R.layout.fragment_payment_mode
    }

    var confirmOrderAdapter: KSelectionAdapter<ConfirmTransactionResponse.Data.Order, RowListPaymentFundsItemBinding>? = null

    override fun createReference() {
        context?.let { DividerItemDecoration(it) }?.let { rvPaymentOrderItems?.addItemDecoration(it) }
        setHasOptionsMenu(true)

        getBinding().root.isFocusableInTouchMode = true
        getBinding().root.requestFocus()
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPress()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        getViewModel().isNetBanking.observe {
            if (it) {
                tvIntro.text = "Your transaction will now be processed by Bombay Stock Exchange - Star platform."
            }
        }
        getViewModel().isNEFTRTGS.observe {
            if (it) {
                val spannableString = SpannableString("Please follow the instructions listed in this link to initiate an NEFT/RTGS transfer and generate the UTR number. Then enter the UTR number below to proceed.")
                val end = 30
                val ssText = SpannableString(spannableString)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        startActivity<WebviewActivity>()

                        //startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
                        postSticky(Event.NEFTRTGS)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = getColor(R.color.colorAccent)!!
                        ds.isUnderlineText = false
                    }
                }
                ssText.setSpan(clickableSpan, 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvIntro.text = ssText
                tvIntro.movementMethod = LinkMovementMethod.getInstance()
                tvIntro.highlightColor = Color.TRANSPARENT
                tvIntro.isEnabled = true
            }
        }

        getViewModel().confirmOrder.observe(this, Observer { response ->
            getViewModel().totalOrder.set((response?.data?.totalPayableAmount))
            if (response?.data?.orders?.isNotEmpty() == true) {
                setOrderItemsAdapter(response.data.orders)
            }
        })

        btnPayNow?.setOnClickListener {
            val items = confirmOrderAdapter?.getAllItems()
            if (items?.isNotEmpty() == true) {
                if (!getViewModel().accountNumber.get().isNullOrEmpty()) {

                    val transaction = arrayListOf<Int>()
                    for (funds in items) {
                        if (funds.lumpsumTransactionId != 0) {
                            transaction.add(funds.lumpsumTransactionId)
                        }
                        if (funds.sipTransactionId != 0) {
                            transaction.add(funds.sipTransactionId)
                        }
                    }
                    val response = getViewModel().confirmOrder.value
                    val json = JSONObject()
                    json.put("user_id", context?.getUserId())
                    json.put("total_payable_amount", response?.data?.totalPayableAmount.toString())
                    json.put("account_number", "${getViewModel().accountNumber.get()}")
                    json.put("transaction_ids", JSONArray(transaction))
                    if (getViewModel().isNetBanking.get() == true) {
                        json.put("payment_mode", "DIRECT")
                        val authData = AES.encrypt(json.toString())
                        getViewModel().paymentOrder(authData).observe(this, Observer {
                            it?.let { response ->
                                val jsonObject = JSONObject("${response.data?.toDecrypt()}")
                                startFragment(NetBankingFragment.newInstance(Bundle().apply {
                                    putSerializable(NET_BANKING_PAGE, jsonObject.optString("data"))
                                    putString(SUCCESSTRANSACTION, transaction.toString())
                                    isFromTransaction?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
                                }), R.id.frmContainer)
                            }
                            /*it?.printResponse()
                            val bundle = Bundle().apply {
                                putString(SUCCESSTRANSACTION, transaction.toString())
                                isFromTransaction?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
                            }
                            startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)*/
                        })
                    } else {
                        if (!TextUtils.isEmpty(getViewModel().utrNumber.get())) {
                            json.put("payment_mode", "NEFT/RTGS")
                            json.put("utr_number", getViewModel().utrNumber.get())
                            val authData = AES.encrypt(json.toString())
                            getViewModel().paymentOrder(authData).observe(this, Observer {
                                it?.printResponse()
                                val bundle = Bundle().apply {
                                    putString(SUCCESSTRANSACTION, transaction.toString())
                                    isFromTransaction?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
                                }
                                startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)
                            })
                        } else {
                            context?.simpleAlert(getString(R.string.alert_req_utr))
                        }
                    }
                    e("Plain Data=>", json.toString())
                } else {
                    context?.simpleAlert(getString(R.string.alert_req_bank))
                }
            }
        }

        tvChangeBank?.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean(ISFROMPAYMENTMODE, true)
                putString(BANKACCOUNTNUMBER, getViewModel().accountNumber.get())
            }
            startFragment(AddBankMandateFragment.newInstance(bundle), R.id.frmContainer)
        }

        tvSelectBank?.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean(ISFROMPAYMENTMODE, true)
                putString(BANKACCOUNTNUMBER, getViewModel().accountNumber.get())
            }
            startFragment(AddBankMandateFragment.newInstance(bundle), R.id.frmContainer)
        }

        rb_netbanking?.isChecked = true

        getDefaultBank().observe(this, Observer {
            it?.data?.let { bank ->
                getViewModel().accountNumber.set(bank.accountNumber)
                getViewModel().branchName.set(bank.branchName)
            }
        })
    }

    override fun createViewModel(): Class<out PaymentModeVM> {
        return PaymentModeVM::class.java
    }

    override fun setVM(binding: FragmentPaymentModeBinding) {
        getBinding().vm = getViewModel()
        getBinding().executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PaymentModeFragment().apply { arguments = basket }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: ConfirmTransactionResponse) {
        getViewModel().confirmOrder.value = data
        getViewModel().accountNumber.set(data.data.accountNumber)
        getViewModel().branchName.set(data.data.bankName)
        removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: BankDetail) {
        getViewModel().accountNumber.set(data.accountNumber)
        getViewModel().branchName.set(data.branchBankIdBankName)
        removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: TransactionApiResponse.Transaction) {
        isFromTransaction = true

        val orderList: MutableList<ConfirmTransactionResponse.Data.Order> = mutableListOf()
        val failedTransactions = arrayListOf<TransactionStatus>()

        val sipAmount: String = if (data.type == "SIP") {
            data.amount.toString()
        } else {
            ""
        }
        val lumpsumAmount: String = if (data.type == "Lumpsum") {
            data.amount.toString()
        } else {
            ""
        }
        val sipTransactionId: Int = if (data.type == "SIP") {
            data.id
        } else {
            0
        }
        val lumpsumTransactionId: Int = if (data.type == "Lumpsum") {
            data.id
        } else {
            0
        }

        val transaction = ConfirmTransactionResponse.Data.Order(
                sipTransactionId, data.name, lumpsumTransactionId
                , lumpsumAmount, sipAmount
        )
        orderList.add(transaction)

        val ConfirmResponseData = data.amount?.let { BigDecimal.valueOf(it).toBigInteger() }?.let {
            ConfirmTransactionResponse.Data(0,
                    "", "", orderList, it, failedTransactions)
        }

        val confirmTransactionResponse = ConfirmResponseData?.let { ConfirmTransactionResponse(it) }
        getViewModel().confirmOrder.value = confirmTransactionResponse
        getViewModel().accountNumber.set("")
        getViewModel().branchName.set("")
        removeStickyEvent(data)
    }

    private fun setOrderItemsAdapter(list: List<ConfirmTransactionResponse.Data.Order>) {
        confirmOrderAdapter = setUpAdapter(list as MutableList<ConfirmTransactionResponse.Data.Order>,
                ChoiceMode.NONE,
                R.layout.row_list_payment_funds_item,
                { item, binder: RowListPaymentFundsItemBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()

                }, { item, position, adapter ->

        }, false)
        rvPaymentOrderItems?.adapter = confirmOrderAdapter
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

    fun onBackPress() {
        if (isFromTransaction == true) {
            onBack(1)
        } else {
            onBack(2)
        }
    }
}
