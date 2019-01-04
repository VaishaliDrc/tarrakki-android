package com.tarrakki.module.recommended


import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.tarrakki.R
import com.tarrakki.api.model.Fund
import com.tarrakki.api.model.RecommendedFunds
import com.tarrakki.databinding.FragmentRecommendedBinding
import com.tarrakki.databinding.RowRecommendedFundsListItemBinding
import kotlinx.android.synthetic.main.fragment_recommended.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.getColor
import org.supportcompact.ktx.toCurrency


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
        rvAMCList?.isFocusable = false
        rvAMCList?.isNestedScrollingEnabled = false
        getViewModel().funds.observe(this, Observer { funds ->
            funds?.let {
                rvAMCList?.setUpRecyclerView(R.layout.row_recommended_funds_list_item, funds.data) { item: Fund, binder: RowRecommendedFundsListItemBinding, position ->
                    binder.fund = item
                    binder.executePendingBindings()
                }
                setPieChartData(funds)
            }
        })
        getViewModel().goalVM.observe(this, Observer { goal ->
            getBinding().goal = goal
            getBinding().executePendingBindings()
            /***
             * Your goal is to reach about (strFVAmt). This means investing (strInvestmentAmt) over the next (strNoOfYears) (strYearText), including a one-time lumpsum of (strLumpsumAmt) up front.
             *
             *  Your goal is to reach about (strFVAmt). This means investing (strInvestmentAmt) over the next (strNoOfYears) (strYearText).
             *
             * */
            try {
                val ssb2 = SpannableStringBuilder("Your goal is to reach about ")
                ssb2.append(SpannableString("${goal?.futureValue?.toCurrency()}.").apply {
                    setSpan(ForegroundColorSpan(Color.parseColor("#00CB00")), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(UnderlineSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                })
                ssb2.append(" This means investing ")
                ssb2.append(SpannableString("${goal?.getInvestmentAmount()?.replace(",", "")?.toDoubleOrNull()?.toCurrency()}").apply {
                    setSpan(ForegroundColorSpan(Color.parseColor("#00CB00")), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(UnderlineSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                })
                ssb2.append("  over the next ")
                ssb2.append(SpannableString("${goal?.getNDuration()}").apply {
                    setSpan(ForegroundColorSpan(Color.parseColor("#00CB00")), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(UnderlineSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                })
                if (!TextUtils.isEmpty(goal?.getPVAmount())) {
                    ssb2.append(" ".plus("${goal?.getNDurationInWord()}, including a one-time lumpsum of "))
                    ssb2.append(SpannableString(if (TextUtils.isEmpty("${goal?.getPVAmount()?.replace(",", "")?.toDoubleOrNull()?.toCurrency()}")) "0" else "${goal?.getPVAmount()}").apply {
                        setSpan(ForegroundColorSpan(Color.parseColor("#00CB00")), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(UnderlineSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    ssb2.append(" up front.")
                } else {
                    ssb2.append(" ${goal?.getNDurationInWord()}.")
                }
                getViewModel().lumpsumpFor.set(ssb2)
                tvRecommendedInfo?.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun setPieChartData(funds: RecommendedFunds) {

        val fundsData = funds.data.groupBy { f -> f.schemeType }
        val entries: MutableList<PieEntry> = ArrayList()
        val colors = arrayListOf<Int>()
        fundsData.forEach { action ->
            var sum = 0.0f
            action.value.forEach { f ->
                sum += f.weightage
            }
            when {
                "EQUITY".equals(action.key, true) -> {
                    entries.add(PieEntry(sum, "EQUITY"))
                    getColor(R.color.equity_fund_color)?.let { colors.add(it) }
                }
                "DEBT".equals(action.key, true) -> {
                    entries.add(PieEntry(sum, "DEBT"))
                    getColor(R.color.debt_fund_color)?.let { colors.add(it) }
                }
                else -> {
                    entries.add(PieEntry(sum, "BALANCED"))
                    getColor(R.color.balanced_fund_color)?.let { colors.add(it) }
                }
            }
        }


        val set = PieDataSet(entries, "")
        set.colors = colors//arrayListOf(getColor(R.color.equity_fund_color), getColor(R.color.debt_fund_color), getColor(R.color.balanced_fund_color))
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(goal: com.tarrakki.api.model.Goal.Data.GoalData) {
        if (getViewModel().goalVM.value == null) {
            getViewModel().goalVM.value = goal
        }
        EventBus.getDefault().removeStickyEvent(goal)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceiveFunds(fund: RecommendedFunds) {
        if (getViewModel().funds.value == null) {
            getViewModel().funds.value = fund
        }
        EventBus.getDefault().removeStickyEvent(fund)
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