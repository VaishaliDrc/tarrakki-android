package com.tarrakki.module.portfolio.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.model.FolioData
import com.tarrakki.api.model.SIPDetails
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.api.model.printRequest
import com.tarrakki.databinding.FragmentAllInvestmnetBinding
import com.tarrakki.databinding.RowAllInvestmentListItemBinding
import com.tarrakki.module.account.AccountActivity
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.portfolio.ImportPortfolioFragment
import com.tarrakki.module.portfolio.PortfolioVM
import com.tarrakki.module.portfolio.StopSIP
import com.tarrakki.module.redeem.RedeemStopConfirmationFragment
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_all_investmnet.*
import kotlinx.android.synthetic.main.fragment_all_investmnet.mRefresh
import kotlinx.android.synthetic.main.fragment_all_investmnet.tvHowReturns
import kotlinx.android.synthetic.main.fragment_all_investmnet.tvNote
import kotlinx.android.synthetic.main.fragment_all_investmnet.tvWhen
import kotlinx.android.synthetic.main.fragment_tarrakki_zyaada_portfolio.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.customefloatingmenu.OneMoreFabMenu
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.utilise.EqualSpacingItemDecoration
import java.util.*


class AllInvestmnetFragment : CoreFragment<PortfolioVM, FragmentAllInvestmnetBinding>(), OneMoreFabMenu.OptionsClick, OneMoreFabMenu.OnExpandOrCollapse {


    var vm: PortfolioVM? = null
    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.portfolio)

    override fun getLayout(): Int {
        return R.layout.fragment_all_investmnet
    }

    override fun createViewModel(): Class<out PortfolioVM> {
        return PortfolioVM::class.java
    }

    override fun setVM(binding: FragmentAllInvestmnetBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    private fun makeLinks(textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>) {
        val spannableString = SpannableString(textView.text)
        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]
            val startIndexOfLink = textView.text.toString().indexOf(link)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    override fun createReference() {


        if (extFab.isExtended) {
            extFab.shrink()
        }
        extFab.setOnClickListener {
            if (extFab.isExtended) {
                extFab.visibility = View.GONE
                fab.visibility = View.VISIBLE
                fab.expand()
            } else {
                extFab.extend()
            }
        }

        val webClickLink = object : ClickableSpan() {

            override fun onClick(widget: View) {
                widget.context?.browse("https://www.nipponindiamf.com/")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                context?.color(R.color.colorAccent)?.let { ds.color = it }
            }
        }

        tvNote?.let {
            makeLinks(
                    it,
                    arrayOf("Nippon AMC's Website"),
                    arrayOf(webClickLink)
            )
        }

        fab.setOptionsClick(this)
        fab.setExpandListner(this)
        ivAddRound.setOnClickListener {
            ivAddRound.visibility = View.GONE
            ivAddPortfolio.visibility = View.VISIBLE
        }

        ivAddPortfolio.setOnClickListener {
            ivAddPortfolio.visibility = View.GONE
            fab.visibility = View.VISIBLE
            fab.expand()
        }

        rvAllInvest?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_item)))

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
                    rvAllInvest?.setUpRecyclerView(R.layout.row_all_investment_list_item, it?.data?.directInvestment as ArrayList<UserPortfolioResponse.Data.DirectInvestment>) { item: UserPortfolioResponse.Data.DirectInvestment, binder: RowAllInvestmentListItemBinding, position ->
                        binder.investment = item
                        binder.executePendingBindings()
                        if (item.folioList.size > 1) {
                            binder.tlfolio.removeAllViews()

                            /**Header View**/
                            /**Header View**/
                            val tableRowHeader = context?.tableRow()
                            tableRowHeader?.setBackgroundResource(R.color.bg_img_color)
                            tableRowHeader?.addView(context?.tableRowContent("Folio\nNumber", context?.color(R.color.black)))
                            tableRowHeader?.addView(context?.tableRowContent("Total\nInvestment", context?.color(R.color.black)))
                            tableRowHeader?.addView(context?.tableRowContent("Current\nValue", context?.color(R.color.black)))
                            tableRowHeader?.addView(context?.tableRowContent("Units", context?.color(R.color.black)))
                            tableRowHeader?.addView(context?.tableRowContent("Returns\n(XIRR)", context?.color(R.color.black)))
                            tableRowHeader?.addView(context?.tableRowContent("Returns\n(Absolute)", context?.color(R.color.black)))
                            binder.tlfolio.addView(tableRowHeader, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

                            /**Body View**/

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
                                tableRow?.addView(context?.tableRowContent(folioList.absolute))
                                binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
                            }

                            /**Footer View**/
                            /**Footer View**/
                            val tableRow = context?.tableRow()
                            tableRow?.addView(context?.tableRowContent("Total", context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent("${item.totalInvestment?.toDecimalCurrencyWithoutRoundOff()}", context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent("${item.currentValue?.toDecimalCurrencyWithoutRoundOff()}", context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent("${
                            item.totalUnits
                                    ?: ""
                            }", context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent(item.xiRR, context?.color(R.color.black)))
                            tableRow?.addView(context?.tableRowContent(item.absolute, context?.color(R.color.black)))
                            binder.tlfolio.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

                            // binder.tlfolio.setBackgroundResource(R.drawable.shape_border)
                        } else {

                        }
                        binder.tvYDRLabel.setOnClickListener {
                            if (binder.tvYDRLabel.contentDescription == "xirr") {
                                binder.tvYDRLabel.text = resources.getString(R.string.return_absolute)
                                binder.tvYDRLabel.contentDescription = "absolute"
                                binder.tvYDR.text = item.absolute
                            } else {
                                binder.tvYDRLabel.text = resources.getString(R.string.return_xirr)
                                binder.tvYDRLabel.contentDescription = "xirr"
                                binder.tvYDR.text = item.xiRR
                            }
                        }

                        binder.tvAddPortfolio.setOnClickListener {
                            App.piMinimumInitialMultiple = toBigInt(item.piMinimumInitialMultiple)
                            App.piMinimumSubsequentMultiple = toBigInt(item.piMinimumSubsequentMultiple)
                            App.additionalSIPMultiplier = item.additionalSIPMultiplier
                            val folios: MutableList<FolioData> = mutableListOf()
                            for (folio in item.folioList) {
                                val fData = FolioData(folio.folioId, folio.currentValue, folio.units, folio.folioNo).apply {
                                    additionalSIPMinAmt = item.validminSIPAmount
                                    additionalLumpsumMinAmt = item.additionalMinLumpsum
                                }
                                folios.add(fData)
                            }
                            context?.addFundPortfolioDialog(folios, item.validminlumpsumAmount, item.validminSIPAmount, item.bseData) { portfolio, amountLumpsum, amountSIP ->
                                addToCartPortfolio(item.fundId, amountSIP.toString(), amountLumpsum.toString(), portfolio, if (item.tzId!!) "1" else null).observe(
                                        this,
                                        Observer { response ->
                                            context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                                startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                            }
                                        })
                            }
                        }

                        binder.tvRedeem.setOnClickListener {

                            if (context?.isCompletedRegistration()!!) {
                                val folios: MutableList<FolioData> = mutableListOf()
                                for (folio in item.folioList) {
                                    folios.add(FolioData(folio.folioId, folio.currentValue, folio.units, folio.folioNo))
                                }
                                val onRedeem: ((portfolioNo: String, folioId: String, allRedeem: String, units: String, amount: String) -> Unit)? = { portfolioNo, folioId, allRedeem, units, amount ->
                                    val json = JsonObject()
                                    json.addProperty("user_id", App.INSTANCE.getUserId())
                                    json.addProperty("fund_id", item.fundId)
                                    json.addProperty("all_redeem", allRedeem)
                                    if (units.toCurrencyBigDecimal().toString() == "0") {
                                        json.addProperty("qty", "")
                                        item.redeemUnits = ""
                                    } else {
                                        json.addProperty("qty", units.toCurrencyBigDecimal().toString())
                                        item.redeemUnits = units
                                    }
                                    json.addProperty("folio_number", portfolioNo)
                                    json.addProperty("folio_id", folioId)
                                    if (amount.toCurrencyBigDecimal().toString() == "0") {
                                        json.addProperty("amount", "")
                                        item.redeemAmount = ""
                                    } else {
                                        json.addProperty("amount", amount.toCurrencyBigDecimal().toString())
                                        item.redeemAmount = amount
                                    }

                                    if (item.tzId!!) {
                                        json.addProperty("tz_id", "1")
                                    }

                                    item.redeemRequest = json
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
                                if (item.tzId == true) {
                                    redeemFundTarrakkiZyaadaDialog(folios, onRedeem) { portfolioNo: String, folioId: String, amount: String, allRedeem: String ->
                                        val json = JsonObject()
                                        json.addProperty("folio_number", portfolioNo)
                                        json.addProperty("amount", amount.toCurrencyBigDecimal().toString())
                                        json.addProperty("redemption_flag", allRedeem)
                                        json.addProperty("folio_id", folioId)
                                        if (item.tzId) {
                                            json.addProperty("tz_id", "1")
                                        }
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
                            } else {
                                context?.confirmationDialog(getString(R.string.alert_req_place_order_registration),
                                        btnPositiveClick = {
                                            startActivity<AccountActivity>()
                                        }
                                )
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
            context?.portfolioIntro(getPortfolioCalculatedIntro(), 0)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AllInvestmnetFragment().apply { arguments = basket }
    }

    data class InvestmentPortfolioIntro(val title: String, val description: String)


    override fun onOptionClick(optionId: Int?) {
        fab.collapse()

        Handler().postDelayed({
            when (optionId) {
                //   R.id.main_option ->
                R.id.option1 -> {
                    startFragment(ImportPortfolioFragment.newInstance(), R.id.frmContainer)
                }
                R.id.option2 -> {
                    startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
                    postSticky(Event.CAMS_WEBSITE)
                }

            }
        }, 300)


    }

    override fun OnExpandOrCollapse(isExpand: Boolean) {
        blurView.visibility = if (isExpand) View.VISIBLE else View.GONE
    }

}