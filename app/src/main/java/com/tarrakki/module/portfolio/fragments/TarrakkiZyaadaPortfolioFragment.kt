package com.tarrakki.module.portfolio.fragments


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.TableLayout
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.model.FolioData
import com.tarrakki.api.model.SIPDetails
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.api.model.printRequest
import com.tarrakki.databinding.FragmentTarrakkiZyaadaPortfolioBinding
import com.tarrakki.databinding.RowTarrakkiZyaadaInvestmentListItemBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.portfolio.PortfolioVM
import com.tarrakki.module.redeem.RedeemConfirmFragment
import kotlinx.android.synthetic.main.fragment_tarrakki_zyaada_portfolio.*
import org.supportcompact.CoreParentFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
import org.supportcompact.utilise.EqualSpacingItemDecoration
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [TarrakkiZyaadaPortfolioFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TarrakkiZyaadaPortfolioFragment : CoreParentFragment<PortfolioVM, FragmentTarrakkiZyaadaPortfolioBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_tarrakki_zyaada_portfolio
    }

    override fun createViewModel(): Class<out PortfolioVM> {
        return PortfolioVM::class.java
    }

    override fun setVM(binding: FragmentTarrakkiZyaadaPortfolioBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvDInvests?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_item)))

        val vm = getViewModel()

        vm.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
            }
        })

        vm.portfolioData.observe(this, Observer {
            if (!it?.data?.tarrakkiZyaadaInvestment.isNullOrEmpty()) {
                getViewModel().isDirectEmpty.set(false)
                rvDInvests?.setUpRecyclerView(R.layout.row_tarrakki_zyaada_investment_list_item, it?.data?.tarrakkiZyaadaInvestment as ArrayList<UserPortfolioResponse.Data.TarrakkiZyaadaInvestment>) { item: UserPortfolioResponse.Data.TarrakkiZyaadaInvestment, binder: RowTarrakkiZyaadaInvestmentListItemBinding, position ->
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

                        // binder.tlfolio.setBackgroundResource(R.drawable.shape_border)
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
                                context?.simpleAlert(alertStopPortfolio(folio, date)) {
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

        mRefresh?.setOnRefreshListener {
            vm.getUserPortfolio(true)
        }

        tvHowReturns?.setOnClickListener {
            context?.portfolioIntro(getPortfolioCalculatedIntro(), 0)
        }

        tvWhen?.setOnClickListener {
            context?.portfolioIntro(getPortfolioCalculatedIntro(), 1)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment TarrakkiZyaadaPortfolioFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = TarrakkiZyaadaPortfolioFragment().apply { arguments = basket }
    }
}
