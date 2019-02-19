package com.tarrakki.module.paymentmode

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.text.SpannableString
import com.tarrakki.R
import com.tarrakki.api.model.ConfirmOrderResponse
import com.tarrakki.databinding.FragmentPaymentModeBinding
import com.tarrakki.databinding.RowListPaymentFundsItemBinding
import com.tarrakki.module.transactionConfirm.TransactionConfirmFragment
import kotlinx.android.synthetic.main.fragment_payment_mode.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.ktx.observe
import org.supportcompact.ktx.startFragment
import org.supportcompact.utilise.DividerItemDecoration
import android.text.method.LinkMovementMethod
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.text.Spanned
import android.R.color
import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.tarrakki.module.webview.WebViewFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.getColor


class PaymentModeFragment : CoreFragment<PaymentModeVM, FragmentPaymentModeBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.payment_mode)

    override fun getLayout(): Int {
        return R.layout.fragment_payment_mode
    }

    var confirmOrderAdapter: KSelectionAdapter<ConfirmOrderResponse.Data.OrderLine, RowListPaymentFundsItemBinding>? = null

    override fun createReference() {
        getViewModel().isNetBanking.observe {
            if (it){
                tvIntro.text = "Your transaction will now be processed by Bombay Stock Exchange - Star platform."
            }
        }

        getViewModel().isNEFTRTGS.observe {
            if (it){
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

        getViewModel().confirmOrder.observe(this, Observer {
            response ->
            getViewModel().totalOrder.set(((response?.data?.totalLumpsum
                    ?: 0.0) + (response?.data?.totalSip ?: 0.0)))
            if (response?.data?.orderLines?.isNotEmpty()==true){
                setOrderItemsAdapter(response.data.orderLines)
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
    fun onReceive(data: ConfirmOrderResponse) {
        getViewModel().confirmOrder.value = data
    }

    private fun setOrderItemsAdapter(list : List<ConfirmOrderResponse.Data.OrderLine>){
        confirmOrderAdapter = setUpAdapter(list as MutableList<ConfirmOrderResponse.Data.OrderLine>,
                    ChoiceMode.NONE,
                    R.layout.row_list_payment_funds_item,
                    { item, binder: RowListPaymentFundsItemBinding?, position, adapter ->
                        binder?.widget = item
                        binder?.executePendingBindings()

                    }, { item, position, adapter ->

            },false)
            rvPaymentOrderItems?.adapter = confirmOrderAdapter

    }

}
