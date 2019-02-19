package com.tarrakki.module.confirmorder


import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.ConfirmOrderResponse
import com.tarrakki.databinding.FragmentConfirmOrderBinding
import com.tarrakki.databinding.RowConfirmOrderBinding
import com.tarrakki.module.bankaccount.SingleButton
import com.tarrakki.module.bankmandate.BankMandateFragment
import com.tarrakki.module.paymentmode.PaymentModeFragment
import kotlinx.android.synthetic.main.fragment_confirm_order.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [ConfirmOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ConfirmOrderFragment : CoreFragment<ConfirmOrderVM, FragmentConfirmOrderBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.order_confirm)

    override fun getLayout(): Int {
        return R.layout.fragment_confirm_order
    }

    override fun createViewModel(): Class<out ConfirmOrderVM> {
        return ConfirmOrderVM::class.java
    }

    override fun setVM(binding: FragmentConfirmOrderBinding) {
        getBinding().vm = getViewModel()
        getBinding().executePendingBindings()
    }

    override fun createReference() {
        getViewModel().getConfirmOrder().observe(this, Observer {
            it.let { confirmOrderResponse ->
                val orderTotal = OrderTotal()
                val orders = arrayListOf<WidgetsViewModel>()
                confirmOrderResponse?.data?.orderLines?.let { orders.addAll(it) }
                orderTotal.total = ((confirmOrderResponse?.data?.totalLumpsum
                        ?: 0.0) + (confirmOrderResponse?.data?.totalSip ?: 0.0))
                orderTotal.bank = if (confirmOrderResponse?.data?.bankName?.isEmpty() == false) "${confirmOrderResponse.data.bankName}" else "Add Bank"
                orders.add(orderTotal)
                orders.add(SingleButton(R.string.place_order))
                rvOrders?.setUpMultiViewRecyclerAdapter(orders) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                    binder.setVariable(BR.widget, item)
                    binder.setVariable(BR.onAdd, View.OnClickListener {
                        it1 ->
                        /***
                         * On place order click
                         * */
                        /*if (confirmOrderResponse?.data?.mandateId == null) {
                            context?.simpleAlert("Please select Mandate bank to continue")
                            return@OnClickListener
                        }else{*/
                            startFragment(PaymentModeFragment.newInstance(), R.id.frmContainer)
                            it?.let { it2 -> postSticky(it2) }
                       // }
                    })

                    binder.setVariable(BR.onCheckedChange, View.OnClickListener {
                        if (item is ConfirmOrderResponse.Data.OrderLine) {
                            if (binder is RowConfirmOrderBinding) {
                                binder.cbSIP.isChecked = item.isFirstInstallmentSIP
                            }
                            val isFirstSIP = !item.isFirstInstallmentSIP
                            getViewModel().updateFirstSIPFlag(item, isFirstSIP).observe(this, Observer {
                                item.isFirstInstallmentSIP = isFirstSIP
                            })
                        }
                    })

                    binder.setVariable(BR.onBankMandateChange, View.OnClickListener {
                        startFragment(BankMandateFragment.newInstance(), R.id.frmContainer)
                    })
                    binder.executePendingBindings()
                }
            }
        })
    }


    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ConfirmOrderFragment().apply { arguments = basket }
    }
}
