package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.TopTenHolding
import com.tarrakki.databinding.FragmentOverviewBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.databinding.RowTopTenHoldingsListItemBinding
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import kotlinx.android.synthetic.main.fragment_overview.*
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.convertTo
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
        fundVM?.let {
            it.fundDetailsResponse.observe(this, Observer { fundDetailsResponse ->
                fundDetailsResponse?.let { it ->
                    rvHolding?.setUpRecyclerView(R.layout.row_top_ten_holdings_list_item, it.topTenHoldings) { item: TopTenHolding, binder: RowTopTenHoldingsListItemBinding, position ->
                        binder.topFund = item
                        binder.executePendingBindings()
                    }
                    binder?.fund = it.fundsDetails
                    binder?.executePendingBindings()
                    val keysInfo = arrayListOf<KeyInfo>()
                    keysInfo.add(KeyInfo("AMC Name", it.fundsDetails?.fscbiProviderCompanyName))
                    keysInfo.add(KeyInfo("Fund Type", it.fundsDetails?.fscbiLegalStructure))
                    keysInfo.add(KeyInfo("Investment Plan", it.fundsDetails?.fscbiDistributionStatus))
                    keysInfo.add(KeyInfo("Launch Date", it.fundsDetails?.inceptionDate?.toDate()?.convertTo() ?: "NA"))
                    keysInfo.add(KeyInfo("Benchmark", it.fundsDetails?.fscbiLegalStructure))
                    keysInfo.add(KeyInfo("Assets Size (\u20B9cr)", App.INSTANCE.getString(R.string.rs_symbol).plus("80.09 cr(31 Mar, 2018)")))
                    keysInfo.add(KeyInfo("Asset Date", "Mar 31, 2018"))
                    keysInfo.add(KeyInfo("Minimum Investment SIP", App.INSTANCE.getString(R.string.rs_symbol).plus("2000")))
                    keysInfo.add(KeyInfo("Minimum Investment Lump sum", App.INSTANCE.getString(R.string.rs_symbol).plus("5000")))
                    keysInfo.add(KeyInfo("Fund Manger", "Shreyash Develkar"))
                    keysInfo.add(KeyInfo("Exit Load", "1.0%"))
                    keysInfo.add(KeyInfo("Volatility (VOL)", "12.05%"))
                    rvKeyInfo?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, keysInfo) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
                        binder.keyInfo = item
                        binder.executePendingBindings()
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
