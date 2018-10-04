package com.tarrakki.module.learn


import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.tarrakki.R
import com.tarrakki.databinding.FragmentLearnBinding
import kotlinx.android.synthetic.main.fragment_performance.*
import org.supportcompact.CoreFragment
import java.util.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_fund_details.*


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
        setChartData()
    }


    private fun setChartData() {
        val values = ArrayList<Entry>()

        for (i in 0 until 50) {

            val value = (Math.random() * 50).toFloat() + 3
            values.add(Entry(i.toFloat(), value))
        }

        var set1: LineDataSet
        set1 = LineDataSet(values, "DataSet 1")
        set1.setDrawIcons(false)
        set1.setDrawCircleHole(false)
        set1.valueTextSize = 9f
        set1.setDrawFilled(true)
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1) // add the datasets
        // create a data object with the datasets
        val data = LineData(dataSets)
        // set data
        mChart.data = data
        mChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mChart.axisLeft.isEnabled = false
        mChart.xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
            val index = value.toInt()
            "Jan 18"
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
