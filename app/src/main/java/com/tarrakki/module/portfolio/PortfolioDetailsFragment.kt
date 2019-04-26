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
import com.tarrakki.module.redeem.RedeemConfirmFragment
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
                    var totalInvesment = 0.0
                    var totalCurrent = 0.0
                    var totalUnites = 0.0
                    var totalReturns = 0.0

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

                        totalInvesment += folioList.amount.toDoubleOrNull() ?: 0.0
                        totalCurrent += folioList.currentValue
                        totalUnites += (folioList.currentValue / item.nav).decimalFormat().toCurrencyBigDecimal().toDouble()
                        totalReturns += folioList.xirr.toDoubleOrNull() ?: 0.0

                        val tableRow = context?.tableRow()
                        tableRow?.addView(context?.tableRowContent(folioList.folioNo))
                        tableRow?.addView(context?.tableRowContent(folioList.amount.toDouble().toCurrency()))
                        tableRow?.addView(context?.tableRowContent(folioList.currentValue.toCurrency()))
                        tableRow?.addView(context?.tableRowContent((folioList.currentValue / item.nav).decimalFormat()))
                        tableRow?.addView(context?.tableRowContent(folioList.xiRR))
                        binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
                    }

                    /**Footer View**/
                    val tableRow = context?.tableRow()

                    tableRow?.addView(context?.tableRowContent("Total", context?.color(R.color.black)))


                    tableRow?.addView(context?.tableRowContent(totalInvesment.toCurrency(), context?.color(R.color.black)))

                    tableRow?.addView(context?.tableRowContent(totalCurrent.toCurrency(), context?.color(R.color.black)))

                    tableRow?.addView(context?.tableRowContent(totalUnites.decimalFormat(), context?.color(R.color.black)))

                    tableRow?.addView(context?.tableRowContent(totalReturns.toReturnAsPercentage(), context?.color(R.color.black)))

                    binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

                }

                binder.tvAddPortfolio.setOnClickListener {
                    val folios: MutableList<FolioData> = mutableListOf()
                    for (folio in item.folioList) {
                        folios.add(FolioData(folio.currentValue, folio.amount, folio.folioNo))
                    }
                    context?.addFundPortfolioDialog(folios, item.validminlumpsumAmount,
                            item.validminSIPAmount) { portfolio, amountLumpsum, amountSIP ->
                        addToCartGoalPortfolio(item.fundId, amountSIP.toString(),
                                amountLumpsum.toString(), portfolio, getViewModel().goalInvestment.get()?.goalId).observe(this,
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
                    if (item.instaRedeem == true) {
                        redeemFundTarrakkiZyaadaDialog(item.nav, folios) { portfolioNo: String, totalUnits: String, allRedeem: String, units: String ->
                            val json = JsonObject()
                            json.addProperty("user_id", App.INSTANCE.getUserId())
                            json.addProperty("fund_id", item.fundId)
                            json.addProperty("all_redeem", allRedeem)
                            json.addProperty("amount", units)
                            json.addProperty("folio_number", portfolioNo)
                            item.redeemRequest = json
                            item.redeemUnits = units
                            getDefaultBank().observe(this, Observer {
                                it?.let { bank ->
                                    json.printRequest()
                                    item.bank = bank.data
                                    startFragment(RedeemConfirmFragment.newInstance(), R.id.frmContainer)
                                    postSticky(item)
                                }
                            })
                        }
                    } else {
                        context?.redeemFundPortfolioDialog(item.nav, folios) { portfolioNo, totalAmount, allRedeem, amount ->
                            val json = JsonObject()
                            json.addProperty("user_id", App.INSTANCE.getUserId())
                            json.addProperty("fund_id", item.fundId)
                            json.addProperty("all_redeem", allRedeem)
                            json.addProperty("amount", totalAmount)
                            json.addProperty("folio_number", portfolioNo)
                            item.redeemRequest = json
                            item.redeemUnits = amount
                            getDefaultBank().observe(this, Observer {
                                it?.let { bank ->
                                    json.printRequest()
                                    item.bank = bank.data
                                    startFragment(RedeemConfirmFragment.newInstance(), R.id.frmContainer)
                                    postSticky(item)
                                }
                            })
                            /*val json = JsonObject()
                            json.addProperty("user_id", App.INSTANCE.getUserId())
                            json.addProperty("fund_id", item.fundId)
                            json.addProperty("all_redeem", allRedeem)
                            json.addProperty("amount", totalAmount)
                            json.addProperty("folio_number", portfolioNo)
                            json.addProperty("goal_id", getViewModel().goalInvestment.get()?.goalId)
                            val data = json.toString().toEncrypt()
                            redeemPortfolio(data).observe(this, Observer {
                                context?.simpleAlert("Your redemption of amount ${amount.toCurrencyBigInt().toCurrency()} is successful.") {
                                    getViewModel().getUserPortfolio()
                                }
                            })*/

                        }
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
                            context?.simpleAlert(alertStopPortfolio(folio, date)) {
                                getViewModel().getUserPortfolio()
                            }
                        })
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
