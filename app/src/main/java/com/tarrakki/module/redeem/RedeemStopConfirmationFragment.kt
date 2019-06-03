package com.tarrakki.module.redeem


import android.arch.lifecycle.Observer
import android.databinding.Observable
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.alertStopPortfolio
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.databinding.FragmentRedeemStopConfirmationBinding
import com.tarrakki.module.portfolio.StopSIP
import com.tarrakki.stopPortfolio
import kotlinx.android.synthetic.main.fragment_redeem_stop_confirmation.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [RedeemStopConfirmationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

const val IS_REDEEM_REQ = "isRedeemReq"

class RedeemStopConfirmationFragment : CoreFragment<RedeemConfirmVM, FragmentRedeemStopConfirmationBinding>() {

    override val isBackEnabled: Boolean
        get() = true

    override val title: String
        get() = getString(R.string.redeem)

    override fun getLayout(): Int {
        return R.layout.fragment_redeem_stop_confirmation
    }

    override fun createViewModel(): Class<out RedeemConfirmVM> {
        return RedeemConfirmVM::class.java
    }

    override fun setVM(binding: FragmentRedeemStopConfirmationBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().isRedeemReq.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                coreActivityVM?.title?.set(App.INSTANCE.getString(if (getViewModel().isRedeemReq.get() == true) R.string.redeem else R.string.stop_sip))
            }
        })
        arguments?.let {
            val isRedeemReq = it.getBoolean(IS_REDEEM_REQ)
            getViewModel().isRedeemReq.set(isRedeemReq)
        }
        btnNo?.setOnClickListener {
            onBack()
        }
        btnYes?.setOnClickListener {

            if (getViewModel().isRedeemReq.get() == true) {
                //proceed for redeem request
                val item: Any? = when {
                    getViewModel().goalBasedRedeemFund.value != null -> getViewModel().goalBasedRedeemFund.value
                    getViewModel().directRedeemFund.value != null -> getViewModel().directRedeemFund.value
                    else -> getViewModel().tarrakkiZyaadaRedeemFund.value
                }
                item?.let {
                    startFragment(RedeemConfirmFragment.newInstance(), R.id.frmContainer)
                    repostSticky(it)
                }
            } else {
                //proceed for stop SIP
                getViewModel().stopSIP?.let { stopSIP ->
                    stopPortfolio(stopSIP.transactionId).observe(this, Observer {
                        context?.simpleAlert(alertStopPortfolio(stopSIP.folioNo, stopSIP.date)) {
                            if (getViewModel().goalBasedRedeemFund.value == null)
                                onBack()
                            else
                                onBack(2)
                        }
                    })
                }
            }
        }
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.DirectInvestment) {
        if (getViewModel().directRedeemFund.value == null) {
            getViewModel().directRedeemFund.value = item
        }
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.GoalBasedInvestment.Fund) {
        if (getViewModel().goalBasedRedeemFund.value == null) {
            getViewModel().goalBasedRedeemFund.value = item
        }
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.TarrakkiZyaadaInvestment) {
        if (getViewModel().tarrakkiZyaadaRedeemFund.value == null) {
            getViewModel().tarrakkiZyaadaRedeemFund.value = item
        }
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: StopSIP) {
        if (getViewModel().stopSIP == null) {
            getViewModel().stopSIP = item
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param basket As Bundle.
         * @return A new instance of fragment RedeemStopConfirmationFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle) = RedeemStopConfirmationFragment().apply { arguments = basket }

        @JvmStatic
        fun newInstance(isRedeemReq: Boolean) = RedeemStopConfirmationFragment().apply {
            arguments = Bundle().apply { putBoolean(IS_REDEEM_REQ, isRedeemReq) }
        }
    }
}
