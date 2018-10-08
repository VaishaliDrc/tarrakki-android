package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.tarrakki.R
import com.tarrakki.databinding.FragmentPerformanceBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.databinding.RowTopTenHoldingsListItemBinding
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import com.tarrakki.module.funddetails.TopHolding
import kotlinx.android.synthetic.main.fragment_invest.*
import kotlinx.android.synthetic.main.fragment_performance.*
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.convertToPx


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
            rvEarned?.setUpRecyclerView(R.layout.row_top_ten_holdings_list_item, it.earningBase) { item: TopHolding, binder: RowTopTenHoldingsListItemBinding, position ->
                binder.topFund = item
                binder.executePendingBindings()
            }
        }
        val adapter = ArrayAdapter.createFromResource(
                activity,
                R.array.durations,
                R.layout.simple_spinner_item_gray
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spnDuration.adapter = adapter

        setChartData()
    }

    private fun setChartData() {
        val series = LineGraphSeries(
                arrayOf(
                        DataPoint(0.0, 1.0),
                        DataPoint(1.0, 5.0),
                        DataPoint(2.0, 3.0),
                        DataPoint(3.0, 2.0),
                        DataPoint(4.0, 6.0)
                )
        )
        series.backgroundColor = Color.parseColor("#A9DCB0")
        series.isDrawBackground = true
        series.setAnimated(true)
        series.isDrawDataPoints = true


        val staticLabelsFormatter = StaticLabelsFormatter(graph)
        staticLabelsFormatter.setHorizontalLabels(arrayOf("Apr 18", "May 18", "Jun 18", "Jul 18", "Aug 18"))
        staticLabelsFormatter.setVerticalLabels(arrayOf("10.00", "20.00", "30.00", "40.00"))
        graph.gridLabelRenderer.labelFormatter = staticLabelsFormatter
        graph.addSeries(series)
        graph.gridLabelRenderer.textSize = 10f.convertToPx()
        graph.gridLabelRenderer.reloadStyles()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket param as Bundle.
         * @return A new instance of fragment PerformanceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PerformanceFragment().apply { arguments = basket }
    }
}
