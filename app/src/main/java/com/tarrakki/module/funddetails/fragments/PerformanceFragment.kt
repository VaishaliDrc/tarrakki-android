package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.tarrakki.R
import com.tarrakki.databinding.FragmentPerformanceBinding
import com.tarrakki.databinding.RowFundKeyInfoListItemBinding
import com.tarrakki.databinding.RowTopTenHoldingsListItemBinding
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import com.tarrakki.module.funddetails.TopHolding
import kotlinx.android.synthetic.main.fragment_performance.*
import org.supportcompact.adapters.setUpRecyclerView
import com.anychart.AnyChartView
import android.R.attr.data
import android.graphics.Color
import android.graphics.DashPathEffect
import android.support.v4.content.ContextCompat
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.AnyChart
import com.anychart.charts.Pie
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import java.util.ArrayList


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
        setChartData()
    }

    private fun setChartData() {
        val values = ArrayList<Entry>()

        for (i in 0 until 50) {

            val value = (Math.random() * 50).toFloat() + 3
            values.add(Entry(i.toFloat(), value))
        }

        var set1: LineDataSet

        // create a dataset and give it a type
        set1 = LineDataSet(values, "DataSet 1")

        set1.setDrawIcons(false)

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f)
        set1.enableDashedHighlightLine(10f, 5f, 0f)
        /*set1.color = Color.BLACK
        set1.setCircleColor(Color.BLACK)
        set1.lineWidth = 1f
        set1.circleRadius = 3f*/
        set1.setDrawCircleHole(false)
        set1.valueTextSize = 9f
        set1.setDrawFilled(true)
        /*set1.formLineWidth = 1f
        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1.formSize = 15f*/

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1) // add the datasets
        // create a data object with the datasets
        val data = LineData(dataSets)
        // set data
        mChart.data = data
        mChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mChart.axisLeft.isEnabled = false
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
