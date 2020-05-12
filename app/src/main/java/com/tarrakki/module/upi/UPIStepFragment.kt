package com.tarrakki.module.upi

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.Observer
import com.tarrakki.R
import com.tarrakki.databinding.FragmentUpiStepBinding
import com.tarrakki.module.netbanking.NET_BANKING_PAGE
import com.tarrakki.module.paymentmode.ISFROMTRANSACTIONMODE
import com.tarrakki.module.paymentmode.SUCCESSTRANSACTION
import com.tarrakki.module.paymentmode.SUCCESS_ORDERS
import com.tarrakki.module.paymentmode.TRANSACTION_IDS
import com.tarrakki.module.transactionConfirm.TransactionConfirmFragment
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

const val UPI_PAGE = "upi_page"

class UPIStepFragment : CoreFragment<UPIStepVM, FragmentUpiStepBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.upi_payment)

    lateinit var countdown_timer: CountDownTimer


    override fun getLayout(): Int {
        return R.layout.fragment_upi_step
    }

    override fun createReference() {
        countdown_timer = object : CountDownTimer(300000, 5000) {
            override fun onTick(millisUntilFinished: Long) {
                checkstatusApi()
            }

            override fun onFinish() {
                context?.simpleAlert(getString(R.string.something_went_wrong_please_try_again)) {
                    countdown_timer.cancel()
                    //                    redirectTo()
                }
            }
        }.start()
        coreActivityVM?.footerVisibility?.set(View.GONE)
        getViewModel().transaction_ids = arguments?.getString(TRANSACTION_IDS)!!

    }

    private fun redirectTo() {
        val bundle = Bundle().apply {
            arguments?.getString(SUCCESS_ORDERS)?.let { it1 -> putString(SUCCESS_ORDERS, it1) }
            arguments?.getString(SUCCESSTRANSACTION)?.let { it1 -> putString(SUCCESSTRANSACTION, it1) }
            arguments?.getBoolean(ISFROMTRANSACTIONMODE)?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
            putBoolean(NET_BANKING_PAGE, true)
        }
        startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)
    }

    fun checkstatusApi() {
        getViewModel().checkPaymentStatus().observe(this, Observer {
            it?.data?.let {
                if (it.payment) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    countdown_timer.cancel()
                    redirectTo()
                }
            }
        })

    }


    override fun createViewModel(): Class<out UPIStepVM> {
        return UPIStepVM::class.java
    }

    override fun setVM(binding: FragmentUpiStepBinding) {
        getBinding().vm = getViewModel()
        getBinding().executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UPIStepFragment().apply { arguments = basket }
    }


}
