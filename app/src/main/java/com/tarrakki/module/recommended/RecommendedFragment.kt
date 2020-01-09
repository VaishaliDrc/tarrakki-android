package com.tarrakki.module.recommended


import androidx.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.TextUtils
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
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.funddetails.FundDetailsFragment
import com.tarrakki.module.funddetails.ITEM_ID
import com.tarrakki.toYearWord
import kotlinx.android.synthetic.main.fragment_recommended.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*


/**
 * A simple [Fragment] subclass.
 * Use the [RecommendedFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
const val KEY_FUND_DIST_EQUITY = "EQUITY"
const val KEY_FUND_DIST_DEBT = "DEBT"
const val KEY_FUND_DIST_BALANCED = "BALANCED"
const val KEY_FUND_DIST_BOND = "Bond"
const val KEY_FUND_DIST_ELSS = "ELSS"
const val KEY_FUND_DIST_FOF = "FOF"
const val KEY_FUND_DIST_GUILT = "Guilt"
const val KEY_FUND_DIST_HYBRID = "Hybrid"
const val KEY_FUND_DIST_LIQUID = "Liquid"
const val KEY_FUND_DIST_MIP = "MIP"
const val KEY_FUND_DIST_STP = "STP"

const val ISFROMGOALRECOMMEDED = "is_from_goal_recommended"

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
                getViewModel().userGoalId = funds.userGoalId.toString()
                rvAMCList?.setUpRecyclerView(R.layout.row_recommended_funds_list_item, funds.data) { item: Fund, binder: RowRecommendedFundsListItemBinding, position ->
                    binder.fund = item
                    binder.executePendingBindings()

                    binder.root.setOnClickListener {
                        startFragment(FundDetailsFragment.newInstance(Bundle().apply { putString(ITEM_ID, "${item.id}") }), R.id.frmContainer)
                    }
                }
                setPieChartData(funds)
            }
        })
        getViewModel().goalVM.observe(this, Observer { it ->
            it?.let { goal ->
                getBinding().goal = goal
                getBinding().executePendingBindings()
                try {
                    var recommendationSummary = "${goal.recommendationSummary}"
                    if (recommendationSummary.contains("\$n")) {
                        recommendationSummary = recommendationSummary.replace("\$n", "${goal.getNDuration()?.toColorAndUnderlineFromHTML()}")
                    }
                    if (recommendationSummary.contains("\$fv")) {
                        recommendationSummary = recommendationSummary.replace("\$fv", "${goal.futureValue?.toCurrency()?.toColorAndUnderlineFromHTML()}")
                    }
                    if (recommendationSummary.contains("\$pmt")) {
                        recommendationSummary = recommendationSummary.replace("\$pmt", "${goal.pmt?.toCurrency()?.toColorAndUnderlineFromHTML()}")
                    }
                    if (recommendationSummary.contains("\$pv")) {
                        recommendationSummary = recommendationSummary.replace("\$pv", "${if (TextUtils.isEmpty(goal.getPVAmount())) "0" else goal.getPVAmount()}".toColorAndUnderlineFromHTML())
                    }
                    recommendationSummary = if (recommendationSummary.contentEquals("year")) recommendationSummary.replace("year", "${goal.getNDuration()?.toYearWord()}") else recommendationSummary.replace("years", "${goal.getNDuration()?.toYearWord()}")
                    getViewModel().lumpsumpFor.set(recommendationSummary.toHTMl())
                    tvRecommendedInfo?.visibility = View.VISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })


        btnGetThisGoal?.setOnClickListener {
            getViewModel().addGoalToCart(getViewModel().userGoalId).observe(this, Observer { apiResponce ->
                context?.simpleAlert(getString(R.string.cart_goal_added)) {
                    val bundle = Bundle().apply {
                        putBoolean(ISFROMGOALRECOMMEDED, true)
                    }
                    startFragment(CartFragment.newInstance(bundle), R.id.frmContainer)
                }
            })
        }
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
                KEY_FUND_DIST_EQUITY.equals(action.key, true) ||
                        KEY_FUND_DIST_ELSS.equals(action.key, true) ||
                        KEY_FUND_DIST_HYBRID.equals(action.key, true) ||
                        "${action.key}".contains(KEY_FUND_DIST_EQUITY, true) -> {
                    val equity = entries.find { it.label == "EQUITY" }
                    if (equity == null) {
                        entries.add(PieEntry(sum, "EQUITY"))
                        getColor(R.color.equity_fund_color)?.let { colors.add(it) }
                    } else {
                        val update = PieEntry(equity.value + sum, "EQUITY")
                        entries.remove(equity)
                        entries.add(update)
                    }
                }
                KEY_FUND_DIST_BALANCED.equals(action.key, true) -> {
                    val balanced = entries.find { it.label == "BALANCED" }
                    if (balanced == null) {
                        entries.add(PieEntry(sum, "BALANCED"))
                        getColor(R.color.balanced_fund_color)?.let { colors.add(it) }
                    } else {
                        val update = PieEntry(balanced.value + sum, "BALANCED")
                        entries.remove(balanced)
                        entries.add(update)
                    }
                }
                KEY_FUND_DIST_MIP.equals(action.key, true) ||
                        KEY_FUND_DIST_BOND.equals(action.key, true) ||
                        KEY_FUND_DIST_GUILT.equals(action.key, true) ||
                        KEY_FUND_DIST_LIQUID.equals(action.key, true) ||
                        KEY_FUND_DIST_STP.equals(action.key, true) ||
                        KEY_FUND_DIST_DEBT.equals(action.key, true) -> {
                    val debt = entries.find { it.label == "DEBT" }
                    if (debt == null) {
                        entries.add(PieEntry(sum, "DEBT"))
                        getColor(R.color.debt_fund_color)?.let { colors.add(it) }
                    } else {
                        val update = PieEntry(debt.value + sum, "DEBT")
                        entries.remove(debt)
                        entries.add(update)
                    }
                }
                else -> {
                    val FOF = entries.find { it.label == "FOF" }
                    if (FOF == null) {
                        entries.add(PieEntry(sum, "FOF"))
                        getColor(R.color.fof_fund_color)?.let { colors.add(it) }
                    } else {
                        val update = PieEntry(FOF.value + sum, "FOF")
                        entries.remove(FOF)
                        entries.add(update)
                    }
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