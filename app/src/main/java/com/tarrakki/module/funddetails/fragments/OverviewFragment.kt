package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tarrakki.R
import com.tarrakki.addToCart
import com.tarrakki.api.model.TopTenHolding
import com.tarrakki.databinding.FragmentOverviewBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.databinding.RowTopTenHoldingsListItemBinding
import com.tarrakki.investDialog
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import kotlinx.android.synthetic.main.fragment_overview.*
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.convertTo
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.toDate

/**
 * A simple [Fragment] subclass.
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment : Fragment() {

    var fundVM: FundDetailsVM? = null
    var binder: FragmentOverviewBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
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
                fundDetailsResponse?.let { fundDetails ->
                    fundDetails.topTenHoldings?.let { topTenHoldings ->
                        rvHolding?.setUpRecyclerView(R.layout.row_top_ten_holdings_list_item, topTenHoldings) { item: TopTenHolding, binder: RowTopTenHoldingsListItemBinding, position ->
                            binder.topFund = item
                            binder.executePendingBindings()
                        }
                    }
                    vm.fund?.let { f ->
                        fundDetails.fundsDetails?.fscbiIndianRiskLevel = f.getRiskLevelName("${fundDetails.fundsDetails?.riskLevelId}")
                    }
                    binder?.fund = fundDetails.fundsDetails
                    binder?.executePendingBindings()
                    binder?.root?.visibility = View.VISIBLE
                    val keysInfo = arrayListOf<KeyInfo>()
                    keysInfo.add(KeyInfo("AMC Name", fundDetails.fundsDetails?.amcName))
                    keysInfo.add(KeyInfo("Fund Type", fundDetails.fundsDetails?.fscbiLegalStructure))
                    keysInfo.add(KeyInfo("Investment Plan", fundDetails.fundsDetails?.schemePlan))
                    keysInfo.add(KeyInfo("Launch Date", fundDetails.fundsDetails?.inceptionDate?.toDate()?.convertTo()
                            ?: "NA"))
                    keysInfo.add(KeyInfo("Benchmark", fundDetails.fundsDetails?.benchmark))
                    keysInfo.add(KeyInfo("Assets Size", fundDetails.fundsDetails?.netAssets))
                    keysInfo.add(KeyInfo("Asset Date", fundDetails.fundsDetails?.assetsDate))
                    keysInfo.add(KeyInfo("Minimum Investment SIP", fundDetails.fundsDetails?.minSIPAmount))
                    keysInfo.add(KeyInfo("Minimum Investment Lump sum", fundDetails.fundsDetails?.lumpsumAmount))
                    keysInfo.add(KeyInfo("Fund Manger", fundDetails.fundsDetails?.fundManagers))
                    keysInfo.add(KeyInfo("Exit Load", fundDetails.fundsDetails?.exitLoad))
                    keysInfo.add(KeyInfo("Volatility (VOL)", fundDetails.fundsDetails?.vol))
                    rvKeyInfo?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, keysInfo) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
                        binder.keyInfo = item
                        binder.executePendingBindings()
                    }

                    btn_invest_now?.setOnClickListener { it1 ->
                        val fund_id = fundDetailsResponse.fundsDetails?.id
                        val minSIPAmount = fundDetailsResponse.fundsDetails?.validminSIPAmount
                        val minLumpSumAmount = fundDetailsResponse.fundsDetails?.validminlumpsumAmount

                        if (fund_id != null && minSIPAmount != null && minLumpSumAmount != null) {
                            context?.investDialog(fund_id, minSIPAmount, minLumpSumAmount) { amountLumpsum, amountSIP, fundId ->
                                addToCart(fundId, amountSIP, amountLumpsum).observe(this,
                                        android.arch.lifecycle.Observer { response ->
                                            startFragment(CartFragment.newInstance(), R.id.frmContainer)
                                        })
                            }
                        }
                    }
                }
            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment OverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = OverviewFragment().apply { arguments = basket }
    }
}
