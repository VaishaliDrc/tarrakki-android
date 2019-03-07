package com.tarrakki.module.portfolio


import android.arch.lifecycle.Observer
import android.os.Bundle
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.model.FolioData
import com.tarrakki.api.model.SIPDetails
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.api.model.toEncrypt
import com.tarrakki.databinding.FragmentPortfolioDetailsBinding
import com.tarrakki.databinding.RowGoalBasedInvestmentDetailsListItemBinding
import com.tarrakki.module.cart.CartFragment
import kotlinx.android.synthetic.main.fragment_portfolio_details.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import java.util.*

class PortfolioDetailsFragment : CoreFragment<PortfolioDetailsVM, FragmentPortfolioDetailsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.portfolio)

    override fun getLayout(): Int {
        return R.layout.fragment_portfolio_details
    }

    override fun createViewModel(): Class<out PortfolioDetailsVM> {
        return PortfolioDetailsVM::class.java
    }

    override fun setVM(binding: FragmentPortfolioDetailsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        getViewModel().portfolioData.observe(this, Observer { response ->
            if (response?.data?.goalBasedInvestment?.isNotEmpty() == true) {
                val goldbasedInvestment =
                        response.data.goalBasedInvestment.find { it.goalId == getViewModel().goalInvestment.get()?.goalId }

                if (goldbasedInvestment != null) {
                    getViewModel().goalBasedInvestment.value = goldbasedInvestment
                    getViewModel().goalInvestment.set(goldbasedInvestment)
                }
            }

        })

        getViewModel().goalBasedInvestment.observe(this, Observer {
            rvPortfolioFunds?.setUpRecyclerView(R.layout.row_goal_based_investment_details_list_item,
                    it?.funds as ArrayList<UserPortfolioResponse.Data.GoalBasedInvestment.Fund>)
            { item: UserPortfolioResponse.Data.GoalBasedInvestment.Fund, binder: RowGoalBasedInvestmentDetailsListItemBinding, position ->
                binder.investment = item
                binder.executePendingBindings()

                binder.tvAddPortfolio.setOnClickListener {
                    val folios: MutableList<FolioData> = mutableListOf()
                    for (folio in item.folioList) {
                        folios.add(FolioData(folio.currentValue, folio.amount, folio.folioNo))
                    }
                    context?.addFundPortfolioDialog(folios, item.validminlumpsumAmount,
                            item.validminSIPAmount) { portfolio, amountLumpsum, amountSIP ->
                        addToCartGoalPortfolio(item.fundId, amountSIP.toString(),
                                amountLumpsum.toString(), portfolio,getViewModel().goalInvestment.get()?.goalId).observe(this,
                                android.arch.lifecycle.Observer { response ->
                                    context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                        startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                    }
                                })
                    }
                }

                binder.tvRedeem.setOnClickListener {
                    val folios: MutableList<FolioData> = mutableListOf()
                    for (folio in item.folioList) {
                        folios.add(FolioData(folio.currentValue, folio.amount, folio.folioNo))
                    }
                    context?.redeemFundPortfolioDialog(folios) { portfolioNo, totalAmount, allRedeem, amount ->
                            val json = JsonObject()
                            json.addProperty("user_id", App.INSTANCE.getUserId())
                            json.addProperty("fund_id", item.fundId)
                            json.addProperty("all_redeem", allRedeem)
                            json.addProperty("amount", totalAmount)
                            json.addProperty("folio_number", portfolioNo)
                            json.addProperty("goal_id", getViewModel().goalInvestment.get()?.goalId)
                            val data = json.toString().toEncrypt()
                            redeemPortfolio(data).observe(this, Observer {
                                context?.simpleAlert("Your redemption of amount $amount is successful.") {
                                    getViewModel().getUserPortfolio()
                                }
                            })
                        }
                }

                binder.tvStopPortfolio.setOnClickListener {
                    val folios: MutableList<FolioData> = mutableListOf()
                    for (folio in item.folioList) {
                        val sipDetailsList: MutableList<SIPDetails> = mutableListOf()
                        for (sipDetail in folio.sipDetails) {
                            sipDetailsList.add(SIPDetails(sipDetail.amount, sipDetail.startDate, sipDetail.transId))
                        }
                        folios.add(FolioData(folio.currentValue, folio.amount, folio.folioNo, sipDetailsList))
                    }

                    context?.stopFundPortfolioDialog(folios) { transactionId, folio, date ->
                        stopPortfolio(transactionId).observe(this, Observer {
                            context?.simpleAlert("Your SIP having folio no. $folio and start date $date has been stopped successfully.") {
                                getViewModel().getUserPortfolio()
                            }
                        })
                    }
                }
            }
        })
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onReceive(data: UserPortfolioResponse.Data.GoalBasedInvestment) {
        getViewModel().goalBasedInvestment.value = data
        getViewModel().goalInvestment.set(data)
        EventBus.getDefault().removeStickyEvent(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PortfolioDetailsFragment().apply { arguments = basket }
    }
}
