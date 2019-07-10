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
import com.tarrakki.module.portfolio.StopSIP
import com.tarrakki.module.redeem.RedeemStopConfirmationFragment
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
                                        ?: 0.0).toDouble().toDecimalCurrencyWithoutRoundOff()))
                                tableRow?.addView(context?.tableRowContent("${folioList.currentValue?.toDecimalCurrencyWithoutRoundOff()}"))
                                tableRow?.addView(context?.tableRowContent(folioList.units
                                        ?: ""/*(folioList.units?.toDoubleOrNull()?: 0.0).decimalFormat())*/))
                                tableRow?.addView(context?.tableRowContent(folioList.xiRR))
                                binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
                            }

                            /**Footer View**/
                            val tableRow = context?.tableRow()
                            tableRow?.addView(context?.tableRowContent("Total", context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent("${item.totalInvestment?.toDecimalCurrencyWithoutRoundOff()}", context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent("${item.currentValue?.toDecimalCurrencyWithoutRoundOff()}", context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent("${item.totalUnits
                                    ?: ""}", context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent(item.xiRR + " (XIRR)", context?.color(R.color.black)))
                            binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

                            // binder.tlfolio.setBackgroundResource(R.drawable.shape_border)
                        }

                        binder.tvAddPortfolio.setOnClickListener {
                            val folios: MutableList<FolioData> = mutableListOf()
                            for (folio in item.folioList) {
                                val fData = FolioData(folio.folioId, folio.currentValue, folio.units, folio.folioNo).apply {
                                    additionalSIPMinAmt = item.additionalSIPAmount
                                    additionalLumpsumMinAmt = item.additionalMinLumpsum
                                }
                                folios.add(fData)
                            }
                            context?.addFundPortfolioDialog(folios, item.validminlumpsumAmount, item.validminSIPAmount, item.bseData) { portfolio, amountLumpsum, amountSIP ->
                                addToCartPortfolio(item.fundId, amountSIP.toString(), amountLumpsum.toString(), portfolio).observe(
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
                                        vm.getUserPortfolio()
                                    }
                                })*/
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
            context?.portfolioIntro(getPortfolioCalculatedIntro(), 0)
        }

        tvWhen?.setOnClickListener {
            context?.portfolioIntro(getPortfolioCalculatedIntro(), 1)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DirectInvestmentFragment().apply { arguments = basket }
    }

    data class InvestmentPortfolioIntro(val title: String, val description: String)
}
