package com.tarrakki.module.portfolio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.TableLayout
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.model.FolioData
import com.tarrakki.api.model.SIPDetails
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.api.model.printRequest
import com.tarrakki.databinding.FragmentDirectInvestmentBinding
import com.tarrakki.databinding.RowDirectInvestmentListItemBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.portfolio.PortfolioVM
import com.tarrakki.module.redeem.RedeemConfirmFragment
import kotlinx.android.synthetic.main.fragment_direct_investment.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
import org.supportcompact.utilise.EqualSpacingItemDecoration
import java.util.*


class DirectInvestmentFragment : CoreFragment<PortfolioVM, FragmentDirectInvestmentBinding>() {

    var vm: PortfolioVM? = null

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.portfolio)

    override fun getLayout(): Int {
        return R.layout.fragment_direct_investment
    }

    override fun createViewModel(): Class<out PortfolioVM> {
        return PortfolioVM::class.java
    }

    override fun setVM(binding: FragmentDirectInvestmentBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        rvDInvests?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_item)))

        parentFragment?.let {
            vm = ViewModelProviders.of(it).get(PortfolioVM::class.java)
        }

        vm?.isRefreshing?.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
            }
        })

        vm?.let { vm ->
            vm.portfolioData.observe(this, Observer {
                if (!it?.data?.directInvestment.isNullOrEmpty()) {
                    getViewModel().isDirectEmpty.set(false)
                    rvDInvests?.setUpRecyclerView(R.layout.row_direct_investment_list_item, it?.data?.directInvestment as ArrayList<UserPortfolioResponse.Data.DirectInvestment>) { item: UserPortfolioResponse.Data.DirectInvestment, binder: RowDirectInvestmentListItemBinding, position ->
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
                                addToCartPortfolio(item.fundId, amountSIP.toString(),
                                        amountLumpsum.toString(), portfolio).observe(this,
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
                                val data = json.toString().toEncrypt()
                                redeemPortfolio(data).observe(this, Observer {
                                    context?.simpleAlert("Your redemption of amount ${amount.toCurrencyBigInt().toCurrency()} is successful.") {
                                        vm.getUserPortfolio()
                                    }
                                })*/
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
                                        vm.getUserPortfolio()
                                    }
                                })
                            }
                        }
                    }
                } else {
                    getViewModel().isDirectEmpty.set(true)
                }
            })
        }

        mRefresh?.setOnRefreshListener {
            vm?.getUserPortfolio(true)
        }

        tvHowReturns?.setOnClickListener {
            val options: MutableList<InvestmentPortfolioIntro> = mutableListOf()
            options.add(InvestmentPortfolioIntro("How are returns calculated?", "Investments less than one year reflect absolute returns.\n" +
                    "Investments over one year reflect XIRR returns(Annualised returns)"))
            options.add(InvestmentPortfolioIntro("When does my portfolio update here?", "Updates to your portfolio that you make today, will only be visible after 9:30 am tomorrow, in your Portfolio Screen here. Please check the transaction screen to know the status of your recent transactions."))
            context?.portfolioIntro(options, 0)
        }

        tvWhen?.setOnClickListener {
            val options: MutableList<InvestmentPortfolioIntro> = mutableListOf()
            options.add(InvestmentPortfolioIntro("How are returns calculated?", "Investments less than one year reflect absolute returns.\n" +
                    "Investments over one year reflect XIRR returns(Annualised returns)"))
            options.add(InvestmentPortfolioIntro("When does my portfolio update here?", "Updates to your portfolio that you make today, will only be visible after 9:30 am tomorrow, in your Portfolio Screen here. Please check the transaction screen to know the status of your recent transactions."))
            context?.portfolioIntro(options, 1)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DirectInvestmentFragment().apply { arguments = basket }
    }

    data class InvestmentPortfolioIntro(val title: String, val description: String)
}
