package com.tarrakki.module.portfolio


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.widget.TableLayout
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.model.FolioData
import com.tarrakki.api.model.SIPDetails
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.api.model.printRequest
import com.tarrakki.databinding.FragmentPortfolioDetailsBinding
import com.tarrakki.databinding.RowGoalBasedInvestmentDetailsListItemBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.redeem.RedeemStopConfirmationFragment
import kotlinx.android.synthetic.main.fragment_portfolio_details.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
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
                } else {
                    onBack()
                }
            } else {
                onBack()
            }

        })

        getViewModel().goalBasedInvestment.observe(this, Observer {
            rvPortfolioFunds?.setUpRecyclerView(R.layout.row_goal_based_investment_details_list_item,
                    it?.funds as ArrayList<UserPortfolioResponse.Data.GoalBasedInvestment.Fund>)
            { item: UserPortfolioResponse.Data.GoalBasedInvestment.Fund, binder: RowGoalBasedInvestmentDetailsListItemBinding, position ->
                binder.investment = item
                binder.executePendingBindings()

                if (item.folioList.size > 1) {
                    binder.tlfolio.removeAllViews()

                    /**Header View**/
                    val tableRowHeader = context?.tableRow()
                    tableRowHeader?.setBackgroundResource(R.color.bg_img_color)
                    tableRowHeader?.addView(context?.tableRowContent("Folio\nNumber", context?.color(R.color.black)))
                    tableRowHeader?.addView(context?.tableRowContent("Total\nInvestment", context?.color(R.color.black)))
                    tableRowHeader?.addView(context?.tableRowContent("Current\nValue", context?.color(R.color.black)))
                    tableRowHeader?.addView(context?.tableRowContent("Units", context?.color(R.color.black)))
                    tableRowHeader?.addView(context?.tableRowContent("%\nReturns", context?.color(R.color.black)))
                    binder.tlfolio.addView(tableRowHeader, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

                    /**Body View**/
                    for (folioList in item.folioList) {
                        val tableRow = context?.tableRow()
                        tableRow?.addView(context?.tableRowContent(folioList.folioNo))
                        tableRow?.addView(context?.tableRowContent((folioList.totalInvestment
                                ?: 0.0).toDouble().toCurrency()))
                        tableRow?.addView(context?.tableRowContent("${folioList.currentValue?.toCurrency()}"))
                        tableRow?.addView(context?.tableRowContent((folioList.units?.toDoubleOrNull()
                                ?: 0.0).decimalFormat()))
                        tableRow?.addView(context?.tableRowContent(folioList.xiRR))
                        binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
                    }

                    /**Footer View**/
                    val tableRow = context?.tableRow()
                    tableRow?.addView(context?.tableRowContent("Total", context?.color(R.color.black)))
                    tableRow?.addView(context?.tableRowContent("${item.totalInvestment?.toCurrency()}", context?.color(R.color.black)))
                    tableRow?.addView(context?.tableRowContent("${item.currentValue?.toCurrency()}", context?.color(R.color.black)))
                    tableRow?.addView(context?.tableRowContent("${item.totalUnits?.decimalFormat()}", context?.color(R.color.black)))
                    tableRow?.addView(context?.tableRowContent(item.xiRR + " (XIRR)", context?.color(R.color.black)))
                    binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
                }

                binder.tvAddPortfolio.setOnClickListener {
                    val folios: MutableList<FolioData> = mutableListOf()
                    for (folio in item.folioList) {
                        val fData = FolioData(folio.folioId, folio.currentValue, folio.amount, folio.folioNo).apply {
                            additionalSIPMinAmt = item.additionalSIPAmount
                            additionalLumpsumMinAmt = item.additionalMinLumpsum
                        }
                        folios.add(fData)
                    }
                    context?.addFundPortfolioDialog(folios, item.validminlumpsumAmount, item.validminSIPAmount, item.bseData) { portfolio, amountLumpsum, amountSIP ->
                        addToCartGoalPortfolio(item.fundId, amountSIP.toString(), amountLumpsum.toString(), portfolio, getViewModel().goalInvestment.get()?.goalId).observe(
                                this,
                                Observer { response ->
                                    context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                        startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                    }
                                })
                    }
                }

                binder.tvRedeem.setOnClickListener {
                    val folios: MutableList<FolioData> = mutableListOf()
                    for (folio in item.folioList) {
                        folios.add(FolioData(folio.folioId, folio.currentValue, folio.units, folio.folioNo))
                    }
                    val onRedeem: ((portfolioNo: String, folioId: String, allRedeem: String, units: String) -> Unit)? = { portfolioNo, folioId, allRedeem, units ->
                        val json = JsonObject()
                        json.addProperty("user_id", App.INSTANCE.getUserId())
                        json.addProperty("fund_id", item.fundId)
                        json.addProperty("all_redeem", allRedeem)
                        json.addProperty("qty", units.toCurrencyBigDecimal().toString())
                        json.addProperty("folio_number", portfolioNo)
                        json.addProperty("folio_id", folioId)
                        json.addProperty("goal_id", getViewModel().goalInvestment.get()?.goalId)
                        item.redeemRequest = json
                        item.redeemUnits = units
                        item.isInstaRedeem = false
                        getDefaultBank().observe(this, Observer {
                            it?.let { bank ->
                                json.printRequest()
                                item.bank = bank.data
                                startFragment(RedeemStopConfirmationFragment.newInstance(isRedeemReq = true), R.id.frmContainer)
                                repostSticky(item)
                            }
                        })
                    }
                    if (item.instaRedeem == true) {
                        redeemFundTarrakkiZyaadaDialog(folios, onRedeem) { portfolioNo: String, folioId: String, amount: String, allRedeem: String ->
                            val json = JsonObject()
                            json.addProperty("folio_number", portfolioNo)
                            json.addProperty("amount", amount.toCurrencyBigDecimal().toString())
                            json.addProperty("redemption_flag", allRedeem)
                            json.addProperty("folio_id", folioId)
                            json.addProperty("goal_id", getViewModel().goalInvestment.get()?.goalId)
                            item.redeemRequest = json
                            item.redeemUnits = amount.toCurrency().toDecimalCurrency()
                            item.isInstaRedeem = true
                            getDefaultBank().observe(this, Observer {
                                it?.let { bank ->
                                    json.addProperty("bank", bank.data?.bankName)
                                    json.printRequest()
                                    item.bank = bank.data
                                    startFragment(RedeemStopConfirmationFragment.newInstance(isRedeemReq = true), R.id.frmContainer)
                                    repostSticky(item)
                                }
                            })
                        }
                    } else {
                        context?.redeemFundPortfolioDialog(folios, onRedeem)
                    }
                }

                binder.tvStopPortfolio.setOnClickListener {
                    val folios: MutableList<FolioData> = mutableListOf()
                    for (folio in item.folioList) {
                        val sipDetailsList: MutableList<SIPDetails> = mutableListOf()
                        for (sipDetail in folio.sipDetails) {
                            sipDetailsList.add(SIPDetails(sipDetail.amount, sipDetail.startDate, sipDetail.transId).apply {
                                sipDay = sipDetail.sipDay
                            })
                        }
                        folios.add(FolioData(folio.folioId, folio.currentValue, folio.amount, folio.folioNo, sipDetailsList))
                    }

                    context?.stopFundPortfolioDialog(folios) { transactionId, folio, date ->
                        val data = StopSIP(transactionId, folio, date)
                        startFragment(RedeemStopConfirmationFragment.newInstance(isRedeemReq = false), R.id.frmContainer)
                        repostSticky(data)
                        /*stopPortfolio(transactionId).observe(this, Observer {
                            context?.simpleAlert(alertStopPortfolio(folio, date)) {
                                getViewModel().getUserPortfolio()
                            }
                        })*/
                    }
                }
            }
        })

        tvHowReturns?.setOnClickListener {
            context?.portfolioIntro(getPortfolioCalculatedIntro(), 0)
        }

        tvWhen?.setOnClickListener {
            context?.portfolioIntro(getPortfolioCalculatedIntro(), 1)
        }
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
