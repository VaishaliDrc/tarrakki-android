package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tarrakki.*
import com.tarrakki.api.model.FolioData
import com.tarrakki.api.model.TopTenHolding
import com.tarrakki.databinding.FragmentOverviewBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.databinding.RowTopTenHoldingsListItemBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import kotlinx.android.synthetic.main.fragment_overview.*
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*


class OverviewFragment : Fragment() {

    var fundVM: FundDetailsVM? = null
    var binder: FragmentOverviewBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binder == null) {
            binder = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)
        }
        return binder?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvHolding?.isFocusable = false
        rvHolding?.isNestedScrollingEnabled = false
        rvKeyInfo?.isFocusable = false
        rvKeyInfo?.isNestedScrollingEnabled = false
        parentFragment?.let {
            fundVM = ViewModelProviders.of(it).get(FundDetailsVM::class.java)
        }
        fundVM?.let { vm ->
            vm.fundDetailsResponse.observe(this, Observer { fundDetailsResponse ->

                txt_fund_offer?.setOnClickListener {
                    context?.openUrl(fundDetailsResponse?.fundsDetails?.amcLink.toString())
                }

                fundDetailsResponse?.let { fundDetails ->
                    fundDetails.topTenHoldings?.let { topTenHoldings ->
                        if (topTenHoldings.isEmpty()) {
                            tvDataNotFountTop10?.visibility = View.VISIBLE
                        }
                        rvHolding?.setUpRecyclerView(R.layout.row_top_ten_holdings_list_item, topTenHoldings) { item: TopTenHolding, binder: RowTopTenHoldingsListItemBinding, position ->
                            binder.topFund = item
                            binder.executePendingBindings()
                        }
                    }
                    fundDetails.fundsDetails?.fscbiIndianRiskLevel = fundDetails.fundsDetails?.riskLevelId
                    binder?.fund = fundDetails.fundsDetails
                    binder?.executePendingBindings()
                    binder?.root?.visibility = View.VISIBLE

                    val keysInfo = arrayListOf<KeyInfo>()
                    keysInfo.add(KeyInfo("AMC Name", fundDetails.fundsDetails?.amcName))
                    keysInfo.add(KeyInfo("Fund Type", fundDetails.fundsDetails?.fscbiLegalStructure))
                    keysInfo.add(KeyInfo("Investment Plan", fundDetails.fundsDetails?.schemePlan))
                    keysInfo.add(KeyInfo("Launch Date", fundDetails.fundsDetails?.inceptionDate?.toDate()?.convertTo()
                            ?: "N/A"))
                    keysInfo.add(KeyInfo("Benchmark", fundDetails.fundsDetails?.benchmark))
                    keysInfo.add(KeyInfo("Assets Size (\u20B9cr)", fundDetails.fundsDetails?.netAssets))
                    keysInfo.add(KeyInfo("Asset Date", fundDetails.fundsDetails?.assetsDate))
                    keysInfo.add(KeyInfo("Minimum Investment SIP", fundDetails.fundsDetails?.minSIPAmount))
                    keysInfo.add(KeyInfo("Minimum Investment Lump sum", fundDetails.fundsDetails?.lumpsumAmount))
                    keysInfo.add(KeyInfo("Expense Ratio", fundDetails.fundsDetails?.expenseRatio))
                    keysInfo.add(KeyInfo("Fund Manger", fundDetails.fundsDetails?.fundManagers))
                    keysInfo.add(KeyInfo("Exit Load", fundDetails.fundsDetails?.exitLoad))
                    keysInfo.add(KeyInfo("Standard Deviation(5yr)", fundDetails.fundsDetails?.vol))
                    rvKeyInfo?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, keysInfo) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
                        binder.keyInfo = item
                        binder.executePendingBindings()
                    }

                    btn_invest_now?.setOnClickListener { it1 ->
                        val fund_id = fundDetailsResponse.fundsDetails?.id
                        val minSIPAmount = fundDetailsResponse.fundsDetails?.validminSIPAmount
                        val minLumpSumAmount = fundDetailsResponse.fundsDetails?.validminlumpsumAmount
                        val foliosList = fundDetailsResponse.folios
                        if (fund_id != null && minSIPAmount != null && minLumpSumAmount != null) {
                            if (foliosList?.isNotEmpty() == true) {
                                val folios: MutableList<FolioData> = mutableListOf()
                                for (folioNo in foliosList) {
                                    folios.add(FolioData(null, null, null, folioNo))
                                }
                                context?.addFundPortfolioDialog(folios, minLumpSumAmount, minSIPAmount, fundDetailsResponse.bseData) { folioNo, amountLumpsum, amountSIP ->
                                    if (vm.tarrakkiZyaadaId.isNullOrBlank()) {
                                        addToCart(fund_id, amountSIP.toString(), amountLumpsum.toString(), folioNo).observe(this,
                                                Observer { response ->
                                                    context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                                        startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                                    }
                                                })
                                    } else {
                                        addToCartTarrakkiZyaada("${vm.tarrakkiZyaadaId}", amountSIP.toString(), amountLumpsum.toString(), folioNo).observe(this,
                                                Observer { response ->
                                                    context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                                        startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                                    }
                                                })
                                    }
                                }
                            } else {
                                context?.investDialog(fund_id, minSIPAmount, minLumpSumAmount, fundDetailsResponse.bseData) { amountLumpsum, amountSIP, fundId ->
                                    if (vm.tarrakkiZyaadaId.isNullOrBlank()) {
                                        addToCart(fundId, amountSIP, amountLumpsum).observe(this,
                                                Observer { response ->
                                                    context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                                        startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                                    }
                                                })
                                    } else {
                                        addToCartTarrakkiZyaada("${vm.tarrakkiZyaadaId}", amountSIP, amountLumpsum).observe(this,
                                                Observer { response ->
                                                    context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                                        startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                                    }
                                                })
                                    }

                                }
                            }
                        }
                    }
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = OverviewFragment().apply { arguments = basket }
    }
}
