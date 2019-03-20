package com.tarrakki.module.redeem


import android.arch.lifecycle.Observer
import android.os.Bundle
import com.tarrakki.R
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.api.model.toEncrypt
import com.tarrakki.databinding.FragmentRedeemConfirmBinding
import com.tarrakki.redeemPortfolio
import kotlinx.android.synthetic.main.fragment_redeem_confirm.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment


class RedeemConfirmFragment : CoreFragment<RedeemConfirmVM, FragmentRedeemConfirmBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.redemption_confim)

    override fun getLayout(): Int {
        return R.layout.fragment_redeem_confirm
    }

    override fun createViewModel(): Class<out RedeemConfirmVM> {
        return RedeemConfirmVM::class.java
    }

    override fun setVM(binding: FragmentRedeemConfirmBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().directRedeemFund.observe(this, Observer {
            it?.let { fund ->
                tvName?.text = fund.fundName
                tvUnits?.text = fund.redeemUnits
                tvExit?.text = fund.exitLoad
            }
        })
        getViewModel().goalBasedRedeemFund.observe(this, Observer {
            it?.let { fund ->
                tvName?.text = fund.fundName
                tvUnits?.text = fund.redeemUnits
                tvExit?.text = fund.exitLoad
            }
        })
        btnProceed?.setOnClickListener {
            getViewModel().directRedeemFund.value?.let { fund ->
                fund.redeemRequest?.let { json ->
                    val data = json.toString().toEncrypt()
                    redeemPortfolio(data).observe(this, Observer {
                        startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
                        postSticky(fund)
                    })
                }
            }
            getViewModel().goalBasedRedeemFund.value?.let { fund ->
                fund.redeemRequest?.let { json ->
                    val data = json.toString().toEncrypt()
                    redeemPortfolio(data).observe(this, Observer {
                        startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
                        postSticky(fund)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment RedeemConfirmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RedeemConfirmFragment().apply { arguments = basket }
    }
}
