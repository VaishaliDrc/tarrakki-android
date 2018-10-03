package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tarrakki.R
import com.tarrakki.databinding.FragmentPerformanceBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.databinding.RowTopTenHoldingsListItemBinding
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import com.tarrakki.module.funddetails.TopHolding
import kotlinx.android.synthetic.main.fragment_performance.*
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [PerformanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PerformanceFragment : Fragment() {

    var fundVM: FundDetailsVM? = null
    var binder: FragmentPerformanceBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (binder == null) {
            binder = DataBindingUtil.inflate(inflater, R.layout.fragment_performance, container, false)
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
            rvReturns?.isFocusable = false
            rvReturns?.isNestedScrollingEnabled = false
            rvReturns?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, it.returns) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
                binder.keyInfo = item
                binder.executePendingBindings()
            }
            rvEarned?.isFocusable = false
            rvEarned?.isNestedScrollingEnabled = false
            rvEarned?.setUpRecyclerView(R.layout.row_top_ten_holdings_list_item, it.topsHolding) { item: TopHolding, binder: RowTopTenHoldingsListItemBinding, position ->
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
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerformanceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PerformanceFragment().apply { arguments = basket }
    }
}
