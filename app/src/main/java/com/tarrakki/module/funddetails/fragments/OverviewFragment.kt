package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tarrakki.R
import com.tarrakki.databinding.FragmentOverviewBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.databinding.RowTopTenHoldingsListItemBinding
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import com.tarrakki.module.funddetails.TopHolding
import kotlinx.android.synthetic.main.fragment_overview.*
import org.supportcompact.adapters.setUpRecyclerView

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
            binder = FragmentOverviewBinding.inflate(inflater, container, false)
        }
        return binder?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.let {
            fundVM = ViewModelProviders.of(it).get(FundDetailsVM::class.java)
        }
        fundVM?.let {
            binder?.fund = it.fund
            binder?.executePendingBindings()
            rvKeyInfo?.isFocusable = false
            rvKeyInfo?.isNestedScrollingEnabled = false
            rvKeyInfo?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, it.keysInfo) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
                binder.keyInfo = item
                binder.executePendingBindings()
            }
            rvHolding?.isFocusable = false
            rvHolding?.isNestedScrollingEnabled = false
            rvHolding?.setUpRecyclerView(R.layout.row_top_ten_holdings_list_item, it.topsHolding) { item: TopHolding, binder: RowTopTenHoldingsListItemBinding, position ->
                binder.topFund = item
                binder.executePendingBindings()
            }
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
