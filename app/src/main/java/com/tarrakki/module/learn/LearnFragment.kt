package com.tarrakki.module.learn


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.tarrakki.R
import com.tarrakki.databinding.FragmentLearnBinding
import kotlinx.android.synthetic.main.fragment_learn.*
import org.supportcompact.CoreFragment


/**
 * A simple [Fragment] subclass.
 * Use the [LearnFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LearnFragment : CoreFragment<LearnVM, FragmentLearnBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.learn)

    override fun getLayout(): Int {
        return R.layout.fragment_learn
    }

    override fun createViewModel(): Class<out LearnVM> {
        return LearnVM::class.java
    }

    override fun setVM(binding: FragmentLearnBinding) {
        binding.vm = getViewModel()
    }

    override fun createReference() {
        setUpChart()
    }

    private fun setUpChart() {

        mLineChart.setBackgroundColor(Color.WHITE)
        mLineChart.setDrawBorders(false)

        // no description text
        mLineChart.description = Description()

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(true)

        val l = mLineChart.legend
        l.isEnabled = false

        val xAxis = mLineChart.xAxis
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
        };

        val leftAxis = mLineChart.axisRight
        leftAxis.labelCount = 4
        leftAxis.axisMaximum = 40f
        leftAxis.axisMinimum = 10f
        leftAxis.setDrawAxisLine(true)
        leftAxis.setDrawZeroLine(true)
        leftAxis.setDrawGridLines(true)

        mLineChart.axisLeft.isEnabled = false

        // add data
        setUpChart(15, 40f)

        mLineChart.invalidate()
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

        if (mLineChart.data != null && mLineChart.data.dataSetCount > 0) {
            set1 = mLineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = yVals1
            mLineChart.data.notifyDataChanged()
            mLineChart.notifyDataSetChanged()
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
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> mLineChart.axisLeft.axisMinimum }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets

            // create a data object with the datasets
            val data = LineData(dataSets)
            data.setDrawValues(false)

            // set data
            mLineChart.data = data
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param basket As Bundle.
         * @return A new instance of fragment LearnFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = LearnFragment().apply { arguments = basket }
    }
}
