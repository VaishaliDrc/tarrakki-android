package com.tarrakki.module.funddetails.fragments


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.tarrakki.R
import com.tarrakki.databinding.*
import com.tarrakki.module.funddetails.FundDetailsVM
import com.tarrakki.module.funddetails.KeyInfo
import com.tarrakki.module.funddetails.TopHolding
import com.tarrakki.module.invest.FundType
import kotlinx.android.synthetic.main.fragment_performance.*
import org.supportcompact.adapters.setUpRecyclerView
import com.tarrakki.chartformaters.MyYAxisValueFormatter




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
        fundVM?.let { itVM ->
            binder?.fund = itVM.fund
            binder?.executePendingBindings()
            rvReturns?.isFocusable = false
            rvReturns?.isNestedScrollingEnabled = false
            rvReturns?.setUpRecyclerView(R.layout.row_fund_key_info_list_item, itVM.returns) { item: KeyInfo, binder: RowFundKeyInfoListItemBinding, position ->
                binder.keyInfo = item
                binder.executePendingBindings()
            }
            rvEarned?.isFocusable = false
            rvEarned?.isNestedScrollingEnabled = false
            rvEarned?.setUpRecyclerView(R.layout.row_earning_base_returns_list_item, itVM.earningBase) { item: TopHolding, binder: RowEarningBaseReturnsListItemBinding, position ->
                binder.topFund = item
                binder.executePendingBindings()
            }

            var selectedAt = 3
            rvDurations?.setUpRecyclerView(R.layout.row_duration_list_item, itVM.durations) { item: FundType, binder: RowDurationListItemBinding, position ->
                binder.fundType = item
                binder.executePendingBindings()
                binder.tvFundType.setOnClickListener {
                    if (selectedAt != -1) {
                        itVM.durations[selectedAt].isSelected = false
                    }
                    item.isSelected = !item.isSelected
                    selectedAt = position
                    // add data
                    setUpChart(15, 40f)
                    mChart.invalidate()
                }
            }

        }
        val adapter = ArrayAdapter.createFromResource(
                activity,
                R.array.durations,
                R.layout.simple_spinner_item_gray
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spnDuration.adapter = adapter
        setUpChart()
    }

    private fun setUpChart() {

        mChart.setBackgroundColor(Color.WHITE)
        mChart.setDrawBorders(false)

        // no description text
        mChart.description.isEnabled = false


        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true)

        val l = mChart.legend
        l.isEnabled = false

        val xAxis = mChart.xAxis
        xAxis.isEnabled = true
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = 5
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
            val index = value.toInt()
            when (index) {
                0 -> "Apr 18"
                3 -> "May 18"
                6 -> "Jun 18"
                9 -> "Jul 18"
                12 -> "Aug 18"
                else -> ""
            }
        }

        val leftAxis = mChart.axisRight
        leftAxis.labelCount = 4
        leftAxis.axisMaximum = 40f
        leftAxis.axisMinimum = 10f
        leftAxis.setDrawAxisLine(true)
        leftAxis.setDrawZeroLine(true)
        leftAxis.setDrawGridLines(true)
        mChart.axisLeft.isEnabled = false
        leftAxis.valueFormatter = MyYAxisValueFormatter()

        // add data
        setUpChart(15, 40f)

        mChart.invalidate()
    }

    private fun setUpChart(count: Int, range: Float) {

        val yVals1 = ArrayList<Entry>()
        val x = 0.0
        for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat() + 40// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(Entry(i.toFloat(), `val`))
        }

        val set1: LineDataSet

        if (mChart.data != null && mChart.data.dataSetCount > 0) {
            set1 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = yVals1
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(yVals1, "DataSet 1")

            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.color = Color.parseColor("#4EB95D")
            set1.setDrawCircles(false)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.fillAlpha = 255
            set1.setDrawFilled(true)
            val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_line_chart_bg) }
            set1.fillDrawable = drawable
            //set1.fillColor = Color.WHITE
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.setDrawCircleHole(false)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> mChart.axisLeft.axisMinimum }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets

            // create a data object with the datasets
            val data = LineData(dataSets)
            data.setDrawValues(false)

            // set data
            mChart.data = data
        }
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
