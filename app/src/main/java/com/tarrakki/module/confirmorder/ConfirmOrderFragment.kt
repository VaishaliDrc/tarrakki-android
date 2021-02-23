package com.tarrakki.module.confirmorder

import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.ConfirmOrderResponse
import com.tarrakki.api.model.TransactionStatus
import com.tarrakki.api.model.UserBankMandateResponse
import com.tarrakki.api.model.toJson
import com.tarrakki.databinding.FragmentConfirmOrderBinding
import com.tarrakki.databinding.RowConfirmOrderBinding
import com.tarrakki.databinding.RowOrderTotalBinding
import com.tarrakki.investmentRecommendation
import com.tarrakki.module.bankaccount.SingleButton
import com.tarrakki.module.bankmandate.BankMandateFragment
import com.tarrakki.module.bankmandate.ISFROMCONFIRMORDER
import com.tarrakki.module.bankmandate.MANDATEID
import com.tarrakki.module.paymentmode.PaymentModeFragment
import com.tarrakki.module.paymentmode.SUCCESSTRANSACTION
import com.tarrakki.module.paymentmode.SUCCESS_ORDERS
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import com.tarrakki.module.tarrakkipro.TarrakkiProBenefitsFragment
import com.tarrakki.module.transactionConfirm.TransactionConfirmFragment
import kotlinx.android.synthetic.main.fragment_confirm_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.*
import java.math.BigInteger

const val IS_FROM_CONFIRM_ORDER = "is_from_confirm_order"

class ConfirmOrderFragment : CoreFragment<ConfirmOrderVM, FragmentConfirmOrderBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.order_confirm)
    val orders = arrayListOf<WidgetsViewModel>()
    private var orderId: Int? = -1
    var textChnage: ObservableField<Int> = ObservableField(0)

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
    }

    fun getData() {
        getViewModel().getConfirmOrder().observe(this, confirmOrderObserve)
    }

    val confirmOrderObserve: Observer<ConfirmOrderResponse> = Observer {
        it.let { confirmOrderResponse ->
            orderId = confirmOrderResponse?.data?.id
            orders.clear()
            val orderTotal = OrderTotal()
            orderTotal.isBankMandateVisible = confirmOrderResponse?.data?.isSIP
            orderTotal.isTarrakkiPro = confirmOrderResponse?.data?.isTarrakkiPro
            orderTotal.recommendationLeft = "(You have ${intNumberToStringNumber(confirmOrderResponse?.data?.userTrials!!)} free recommendations left)"
            textChnage.set(confirmOrderResponse?.data?.userTrials)
            confirmOrderResponse?.data?.orderLines?.let { orders.addAll(it) }
            orderTotal.total = ((confirmOrderResponse?.data?.totalLumpsum
                    ?: 0.0) + (confirmOrderResponse?.data?.totalSip ?: 0.0))
            orderTotal.bank = if (!TextUtils.isEmpty(confirmOrderResponse?.data?.bank)) "${confirmOrderResponse?.data?.bankName}" else "Choose Bank"
            orders.add(orderTotal)
            orders.add(SingleButton(R.string.place_order))
            rvOrders?.setUpMultiViewRecyclerAdapter(orders) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.widget, item)
                /*if (item is ConfirmOrderResponse.Data.OrderLine){
                    val binding = binder as RowConfirmOrderBinding
                    binding.cbSIP.isEnabled = confirmOrderResponse?.data?.isApproveBank==true
                }*/

                textChnage.observe {
                    if (binder is RowOrderTotalBinding && it >= 0) {
                        binder.tvRecommendationLeft.text = "(You have ${intNumberToStringNumber(it)} free recommendations left)"
                    }
                }

                confirmOrderResponse?.data?.proPrice?.let {
                    if (binder is RowOrderTotalBinding) {
                        binder.tvGetTPro.text = getString(R.string.get_t_pro,it)
                    }

                }

                if (binder is RowConfirmOrderBinding) {

                    if (item is ConfirmOrderResponse.Data.OrderLine) {
                        val rating = item?.primeRatingData?.primeRating?.toFloatOrNull()
                        if(rating == null){
                            binder.tvRatingEmpty.visibility = View.VISIBLE
                            binder.ratingBarCO.visibility = View.GONE
                            binder.tvRatingEmpty.text = item?.primeRatingData?.primeReview
                        }
                        else{
                            if(rating == 0.0f){
                                binder.tvRatingEmpty.visibility = View.VISIBLE
                                binder.ratingBarCO.visibility = View.GONE
                                binder.tvRatingEmpty.text = item?.primeRatingData?.primeReview
                            }
                            else{
                                binder.tvRatingEmpty.visibility = View.GONE
                                binder.ratingBarCO.visibility = View.VISIBLE
                                binder.ratingBarCO.rating = rating
                            }
                        }
                    }

                    if (confirmOrderResponse?.data?.isTarrakkiPro) {
                        binder.groupLocks.visibility = View.GONE
                        binder.llcRating.visibility = View.VISIBLE
                        binder.tvPrimeRecommned.visibility = View.VISIBLE
                    } else {
                        binder.groupLocks.visibility = View.VISIBLE
                        binder.llcRating.visibility = View.GONE
                        binder.tvPrimeRecommned.visibility = View.GONE
                    }
                }



                binder.setVariable(BR.onAdd, View.OnClickListener { it1 ->
                    if (confirmOrderResponse?.data?.isSIP == true) {
                        if (TextUtils.isEmpty(confirmOrderResponse.data.mandateId)) {
                            context?.simpleAlert(getString(R.string.alert_req_bank_mandate))
                            return@OnClickListener
                        } else {
                            var isSell = false
                            for (i in confirmOrderResponse!!.data.orderLines) {
                                if (i.primeRatingData.primeReview.get(0).equals('S', true)) {
                                    isSell = true
                                }
                            }
                            if (confirmOrderResponse?.data?.isTarrakkiPro && isSell) {

                                context?.confirmationDialog(getString(R.string.app_name), getString(R.string.recommendation_sell_alert),
                                        btnPositive = getString(R.string.yes),
                                        btnNegative = getString(R.string.no),
                                        btnPositiveClick = {
                                            getViewModel().checkoutConfirmOrder().observe(this, Observer {
                                                App.INSTANCE.cartCount.value = it?.data?.cartCount
                                                if (!it?.data?.orders.isNullOrEmpty() && it?.data?.totalPayableAmount ?: BigInteger.ZERO > BigInteger.ZERO) {
                                                    startFragment(PaymentModeFragment.newInstance(), R.id.frmContainer)
                                                    it?.let { it2 -> postSticky(it2) }
                                                    if (it?.data?.failedTransactions?.isNotEmpty() == true) {
                                                        val failed = FailedTransactions(it.data.failedTransactions)
                                                        postSticky(failed)
                                                    }
                                                } else if (it?.data?.orders != null && it.data.orders.isNotEmpty() && it.data.totalPayableAmount == BigInteger.ZERO) {
                                                    val transaction = arrayListOf<Int>()
                                                    for (funds in it.data.orders) {
                                                        if (funds.lumpsumTransactionId != 0) {
                                                            transaction.add(funds.lumpsumTransactionId)
                                                        }
                                                        if (funds.sipTransactionId != 0) {
                                                            transaction.add(funds.sipTransactionId)
                                                        }
                                                    }
                                                    val bundle = Bundle().apply {
                                                        putString(SUCCESSTRANSACTION, transaction.toString())
                                                        putString(SUCCESS_ORDERS, it.data.orders.toJson())
                                                        putBoolean(IS_FROM_CONFIRM_ORDER, true)
                                                    }
                                                    startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)
                                                    it.data.failedTransactions?.let { list ->
                                                        val failed = FailedTransactions(list)
                                                        postSticky(failed)
                                                    }
                                                } else {
                                                    startFragment(TransactionConfirmFragment.newInstance(), R.id.frmContainer)
                                                    it?.data?.failedTransactions?.let { list ->
                                                        val failed = FailedTransactions(list)
                                                        postSticky(failed)
                                                    }
                                                }
                                            })
                                        }
                                )
                            } else {
                                getViewModel().checkoutConfirmOrder().observe(this, Observer {
                                    App.INSTANCE.cartCount.value = it?.data?.cartCount
                                    if (!it?.data?.orders.isNullOrEmpty() && it?.data?.totalPayableAmount ?: BigInteger.ZERO > BigInteger.ZERO) {
                                        startFragment(PaymentModeFragment.newInstance(), R.id.frmContainer)
                                        it?.let { it2 -> postSticky(it2) }
                                        if (it?.data?.failedTransactions?.isNotEmpty() == true) {
                                            val failed = FailedTransactions(it.data.failedTransactions)
                                            postSticky(failed)
                                        }
                                    } else if (it?.data?.orders != null && it.data.orders.isNotEmpty() && it.data.totalPayableAmount == BigInteger.ZERO) {
                                        val transaction = arrayListOf<Int>()
                                        for (funds in it.data.orders) {
                                            if (funds.lumpsumTransactionId != 0) {
                                                transaction.add(funds.lumpsumTransactionId)
                                            }
                                            if (funds.sipTransactionId != 0) {
                                                transaction.add(funds.sipTransactionId)
                                            }
                                        }
                                        val bundle = Bundle().apply {
                                            putString(SUCCESSTRANSACTION, transaction.toString())
                                            putString(SUCCESS_ORDERS, it.data.orders.toJson())
                                            putBoolean(IS_FROM_CONFIRM_ORDER, true)
                                        }
                                        startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)
                                        it.data.failedTransactions?.let { list ->
                                            val failed = FailedTransactions(list)
                                            postSticky(failed)
                                        }
                                    } else {
                                        startFragment(TransactionConfirmFragment.newInstance(), R.id.frmContainer)
                                        it?.data?.failedTransactions?.let { list ->
                                            val failed = FailedTransactions(list)
                                            postSticky(failed)
                                        }
                                    }
                                })
                            }

                        }
                    } else {
                        var isSell = false
                        for (i in confirmOrderResponse!!.data.orderLines) {
                            if (i.primeRatingData.primeReview.get(0).equals('S', true)) {
                                isSell = true
                            }
                        }
                        if (confirmOrderResponse?.data?.isTarrakkiPro && isSell) {
                            context?.confirmationDialog(getString(R.string.app_name), getString(R.string.recommendation_sell_alert),
                                    btnPositive = getString(R.string.yes),
                                    btnNegative = getString(R.string.no),
                                    btnPositiveClick = {
                                        getViewModel().checkoutConfirmOrder().observe(this, Observer {
                                            App.INSTANCE.cartCount.value = it?.data?.cartCount
                                            if (!it?.data?.orders.isNullOrEmpty() && it?.data?.totalPayableAmount ?: BigInteger.ZERO > BigInteger.ZERO) {
                                                startFragment(PaymentModeFragment.newInstance(), R.id.frmContainer)
                                                it?.let { it2 -> postSticky(it2) }
                                                if (it?.data?.failedTransactions?.isNotEmpty() == true) {
                                                    val failed = FailedTransactions(it.data.failedTransactions)
                                                    postSticky(failed)
                                                }
                                            } else if (it?.data?.orders != null && it.data.orders.isNotEmpty() && it.data.totalPayableAmount == BigInteger.ZERO) {
                                                val transaction = arrayListOf<Int>()
                                                for (funds in it.data.orders) {
                                                    if (funds.lumpsumTransactionId != 0) {
                                                        transaction.add(funds.lumpsumTransactionId)
                                                    }
                                                    if (funds.sipTransactionId != 0) {
                                                        transaction.add(funds.sipTransactionId)
                                                    }
                                                }
                                                val bundle = Bundle().apply {
                                                    putString(SUCCESS_ORDERS, it.data.orders.toJson())
                                                    putString(SUCCESSTRANSACTION, transaction.toString())
                                                    putBoolean(IS_FROM_CONFIRM_ORDER, true)
                                                }
                                                startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)
                                                it.data.failedTransactions?.let { list ->
                                                    val failed = FailedTransactions(list)
                                                    postSticky(failed)
                                                }
                                            } else {
                                                startFragment(TransactionConfirmFragment.newInstance(), R.id.frmContainer)
                                                it?.data?.failedTransactions?.let { list ->
                                                    val failed = FailedTransactions(list)
                                                    postSticky(failed)
                                                }
                                            }
                                        })
                                    }
                            )
                        } else {
                            getViewModel().checkoutConfirmOrder().observe(this, Observer {
                                App.INSTANCE.cartCount.value = it?.data?.cartCount
                                if (!it?.data?.orders.isNullOrEmpty() && it?.data?.totalPayableAmount ?: BigInteger.ZERO > BigInteger.ZERO) {
                                    startFragment(PaymentModeFragment.newInstance(), R.id.frmContainer)
                                    it?.let { it2 -> postSticky(it2) }
                                    if (it?.data?.failedTransactions?.isNotEmpty() == true) {
                                        val failed = FailedTransactions(it.data.failedTransactions)
                                        postSticky(failed)
                                    }
                                } else if (it?.data?.orders != null && it.data.orders.isNotEmpty() && it.data.totalPayableAmount == BigInteger.ZERO) {
                                    val transaction = arrayListOf<Int>()
                                    for (funds in it.data.orders) {
                                        if (funds.lumpsumTransactionId != 0) {
                                            transaction.add(funds.lumpsumTransactionId)
                                        }
                                        if (funds.sipTransactionId != 0) {
                                            transaction.add(funds.sipTransactionId)
                                        }
                                    }
                                    val bundle = Bundle().apply {
                                        putString(SUCCESS_ORDERS, it.data.orders.toJson())
                                        putString(SUCCESSTRANSACTION, transaction.toString())
                                        putBoolean(IS_FROM_CONFIRM_ORDER, true)
                                    }
                                    startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)
                                    it.data.failedTransactions?.let { list ->
                                        val failed = FailedTransactions(list)
                                        postSticky(failed)
                                    }
                                } else {
                                    startFragment(TransactionConfirmFragment.newInstance(), R.id.frmContainer)
                                    it?.data?.failedTransactions?.let { list ->
                                        val failed = FailedTransactions(list)
                                        postSticky(failed)
                                    }
                                }
                            })
                        }


                    }
                })

                binder.setVariable(BR.onLockClick, View.OnClickListener {
                    if (binder is RowConfirmOrderBinding) {
                        if (textChnage?.get()!! <= 0) {
                            context?.simpleAlert(resources.getString(R.string.no_free_trial_left))
                        } else {
                            context?.confirmationDialog(getString(R.string.app_name), getString(R.string.free_recommendation_use_alert, intNumberToStringNumber(textChnage.get()!!)),
                                    btnPositive = getString(R.string.yes),
                                    btnNegative = getString(R.string.no),
                                    btnPositiveClick = {
                                        if (item is ConfirmOrderResponse.Data.OrderLine) {
                                            getViewModel().getFundsReview(item.fundIdId.toString()).observe(this, Observer {
                                                it?.let { response ->
                                                    binder.groupLocks.visibility = View.GONE
                                                    binder.llcRating.visibility = View.VISIBLE
                                                    binder.tvPrimeRecommned.visibility = View.VISIBLE
                                                    textChnage.set(textChnage?.get()?.dec())
                                                }
                                            })
                                        }
                                    })

                        }
                    }

                })


                binder.setVariable(BR.onTarrakkiProClick, View.OnClickListener {
                    startFragment(TarrakkiProBenefitsFragment.newInstance(), R.id.frmContainer)
                })

                binder.setVariable(BR.onCheckedChange, View.OnClickListener {
                    if (item is ConfirmOrderResponse.Data.OrderLine) {
                        if (confirmOrderResponse?.data?.isApproveBank == true) {
                            if (binder is RowConfirmOrderBinding) {
                                binder.cbSIP.isChecked = item.isFirstInstallmentSIP
                            }
                            val isFirstSIP = !item.isFirstInstallmentSIP
                            getViewModel().updateFirstSIPFlag(item, isFirstSIP).observe(this, Observer {
                                item.isFirstInstallmentSIP = isFirstSIP
                                getData()
                            })
                        } else {
                            if (binder is RowConfirmOrderBinding) {
                                binder.cbSIP.isChecked = !binder.cbSIP.isChecked
                            }
                            context?.simpleAlert(getString(R.string.alert_warn_uncheck_mandate))
                        }
                    }
                })


                binder.setVariable(BR.onBankMandateChange, View.OnClickListener {
                    if (confirmOrderResponse?.data?.isSIP == true) {
                        val bundle = Bundle().apply {
                            putBoolean(ISFROMCONFIRMORDER, true)
                            confirmOrderResponse.data.mandateId?.let { it1 -> putString(MANDATEID, it1) }
                        }
                        startFragment(BankMandateFragment.newInstance(bundle), R.id.frmContainer)
                    }
                })

                binder.executePendingBindings()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: UserBankMandateResponse.Data) {
        getViewModel().mandateIdConfirmOrder(data.id, orderId).observe(this, confirmOrderObserve)
        removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: ConfirmOrderResponse) {
        getViewModel().apiResponse.value = data
        confirmOrderObserve.onChanged(data)
        removeStickyEvent(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ConfirmOrderFragment().apply { arguments = basket }
    }

    class FailedTransactions(val transactions: List<TransactionStatus>?)

}
