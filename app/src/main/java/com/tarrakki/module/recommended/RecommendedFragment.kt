package com.tarrakki.module.recommended


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.tarrakki.R
import com.tarrakki.databinding.FragmentRecommendedBinding
import com.tarrakki.databinding.RowAmcListItemBinding
import kotlinx.android.synthetic.main.fragment_recommended.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.getColor


/**
 * A simple [Fragment] subclass.
 * Use the [RecommendedFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RecommendedFragment : CoreFragment<RecommendedVM, FragmentRecommendedBinding>() {


    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.our_recommended)

    override fun getLayout(): Int {
        return R.layout.fragment_recommended
    }

    override fun createViewModel(): Class<out RecommendedVM> {
        return RecommendedVM::class.java
    }

    override fun setVM(binding: FragmentRecommendedBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setPieChartData()
        rvAMCList?.isFocusable = false
        rvAMCList?.isNestedScrollingEnabled = false
        rvAMCList?.setUpRecyclerView(R.layout.row_amc_list_item, getViewModel().AMCList) { item: AMC, binder: RowAmcListItemBinding, position ->
            binder.amc = item
            binder.executePendingBindings()
        }
    }

    private fun setPieChartData() {
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry(25.0f, "EQUITY"))
        entries.add(PieEntry(14.0f, "DEBT"))
        entries.add(PieEntry(61.0f, "BALANCED"))
        val set = PieDataSet(entries, "")
        set.colors = arrayListOf(getColor(R.color.equity_fund_color), getColor(R.color.debt_fund_color), getColor(R.color.balanced_fund_color))
        set.sliceSpace = 0f
        val data = PieData(set)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(8f)


        data.setValueTextColor(Color.WHITE)
        mPieChart.data = data
        mPieChart.setEntryLabelTextSize(9f)

        mPieChart.setTouchEnabled(false)
        mPieChart.isDrawHoleEnabled = false // To file entire
        mPieChart.description.isEnabled = false // To remove description
        mPieChart.legend.isEnabled = false // To remove legend
        mPieChart.setExtraOffsets(-5f, -5f, -5f, -5f)
        mPieChart.rotation = 0f
        mPieChart.invalidate() // refresh
        //for rotating anti-clockwise
        mPieChart.animateY(500)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket basket as Bundle.
         * @return A new instance of fragment RecommendedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RecommendedFragment().apply { arguments = basket }
    }
}