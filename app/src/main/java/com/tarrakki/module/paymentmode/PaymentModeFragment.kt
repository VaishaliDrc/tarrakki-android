package com.tarrakki.module.paymentmode

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.tarrakki.R
import com.tarrakki.api.model.ConfirmTransactionResponse
import com.tarrakki.databinding.FragmentPaymentModeBinding
import com.tarrakki.databinding.RowListPaymentFundsItemBinding
import com.tarrakki.module.transactionConfirm.TransactionConfirmFragment
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_payment_mode.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.getColor
import org.supportcompact.ktx.observe
import org.supportcompact.ktx.startFragment
import org.supportcompact.utilise.DividerItemDecoration


class PaymentModeFragment : CoreFragment<PaymentModeVM, FragmentPaymentModeBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.payment_mode)

    override fun getLayout(): Int {
        return R.layout.fragment_payment_mode
    }

    var confirmOrderAdapter: KSelectionAdapter<ConfirmTransactionResponse.Data.Order, RowListPaymentFundsItemBinding>? = null

    override fun createReference() {
        getBinding().root.isFocusableInTouchMode = true
        getBinding().root.requestFocus()
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBack(2)
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
                        startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
                        postSticky(Event.NEFTRTGS)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = getColor(R.color.colorAccent)!!
                    }
                }
                ssText.setSpan(clickableSpan, 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvIntro.text = ssText
                tvIntro.movementMethod = LinkMovementMethod.getInstance()
                tvIntro.highlightColor = Color.TRANSPARENT
                tvIntro.isEnabled = true
            }
        }

        rb_netbanking?.isChecked = true

        context?.let { DividerItemDecoration(it) }?.let { rvPaymentOrderItems?.addItemDecoration(it) }
        getViewModel().confirmOrder.observe(this, Observer { response ->
            getViewModel().totalOrder.set((response?.data?.totalPayableAmount?.toDouble()))
            if (response?.data?.orders?.isNotEmpty() == true) {
                setOrderItemsAdapter(response.data.orders)
            }

        })

        btnPayNow?.setOnClickListener {
            startFragment(TransactionConfirmFragment.newInstance(), R.id.frmContainer)
        }

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

        getViewModel().accountNumber.set("A/C :" + data.data.accountNumber)
        getViewModel().branchName.set(data.data.bankName)
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
                onBack(2)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
