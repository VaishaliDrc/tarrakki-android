package com.tarrakki.module.yourgoal


import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.tarrakki.*
import com.tarrakki.api.model.Goal
import com.tarrakki.api.model.GoalSavedResponse
import com.tarrakki.api.model.PMTResponse
import com.tarrakki.databinding.FragmentYourGoalSummaryBinding
import com.tarrakki.module.recommended.RecommendedFragment
import com.xiaofeng.flowlayoutmanager.Alignment
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.fragment_your_goal_summary.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.*


/**
 * A simple [Fragment] subclass.
 * Use the [YourGoalSummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class YourGoalSummaryFragment : CoreFragment<YourGoalVM, FragmentYourGoalSummaryBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.your_goal)

    override fun getLayout(): Int {
        return R.layout.fragment_your_goal_summary
    }

    override fun createViewModel(): Class<out YourGoalVM> {
        return YourGoalVM::class.java
    }

    override fun setVM(binding: FragmentYourGoalSummaryBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    lateinit var pmt: Observer<PMTResponse>

    override fun createReference() {

        getViewModel().goalVM.observe(this, Observer { it ->
            it?.let { goal ->
                var investAmount = "${goal.getCVAmount()}"
                var durations = "${goal.getNDuration()}"
                getBinding().goal = goal
                getBinding().executePendingBindings()

                val flowLayoutManager = FlowLayoutManager()
                flowLayoutManager.isAutoMeasureEnabled = true
                flowLayoutManager.setAlignment(Alignment.LEFT)
                rvGoalSummary?.layoutManager = flowLayoutManager
                var goalSummarys: ArrayList<WidgetsViewModel>
                pmt = Observer {
                    it?.let { pmtResponse ->
                        goal.pmt = pmtResponse.pmt
                        goal.futureValue = pmtResponse.futureValue
                        tvPMT.text = pmtResponse.pmt.toCurrency()
                        setGoalSummary(pmtResponse.futureValue.toCurrency(), durations, pmtResponse.pmt, goal)
                        goalSummarys = goal.goalSummary()
                        rvGoalSummary?.setUpMultiViewRecyclerAdapter(goalSummarys) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                            val goalSummary = item as GoalSummary
                            binder.setVariable(BR.onAction, TextView.OnEditorActionListener { v, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    goalSummarys.forEach { item ->
                                        val summary = item as GoalSummary
                                        when (summary.txt) {
                                            "#cv", "#cv." -> {
                                                goal.setCVAmount(summary.value)
                                                investAmount = summary.value
                                            }
                                            "#fv", "#fv." -> {
                                                //goalSummary.value = "${pmtResponse.futureValue}"
                                            }
                                            "#pv", "#pv." -> {
                                                goal.setPVAmount(summary.value)
                                            }
                                            "\$n", "\$n." -> {
                                                goalSummary.value = durations
                                            }
                                            "\$fv", "\$fv." -> {
                                                goalSummary.value = pmtResponse.futureValue.toCurrencyWithSpace()
                                            }
                                            "#i", "#i." -> {
                                                //goalSummary.value = "${goal.inflation}"
                                                if (goalSummary.txt == "#i")
                                                    goal.inflation = v.text.toString().replace(",", "").toDoubleOrNull()
                                            }
                                            "#pmt", "#pmt." -> {
                                                if (goalSummary.txt.contains("#pmt")) {
                                                    if (goal.isCustomInvestment())
                                                        goal.customPMT = v.text.toString().replace(",", "").toDoubleOrNull()
                                                    else {
                                                        goal.setPMT(summary.value)
                                                        investAmount = summary.value
                                                    }
                                                }
                                            }
                                            "#dp", "#dp." -> {
                                                //goalSummary.value = "${goal.getDPAmount() ?: "0"}"
                                                goal.setDPAmount(summary.value)
                                            }
                                            "#n", "#n." -> {
                                                //goalSummary.value = durations
                                                goal.setNDuration(summary.value)
                                                durations = summary.value
                                            }
                                        }
                                    }
                                    if (isValid(goal)) {
                                        v.dismissKeyboard()
                                        v.clearFocus()
                                        getViewModel().calculatePMT(goal).observe(this, pmt)
                                    }
                                    return@OnEditorActionListener true
                                }
                                false
                            })
                            when (goalSummary.txt) {
                                "#cv", "#cv." -> {
                                    goalSummary.value = goal.getCVAmount() ?: "0"
                                }
                                "#fv", "#fv." -> {
                                    goalSummary.value = "${pmtResponse.futureValue}"
                                }
                                "#pv", "#pv." -> {
                                    goalSummary.value = goal.getPVAmount() ?: "0"
                                }
                                "#pmt", "#pmt." -> {
                                    goalSummary.value = goal.pmt?.format() ?: "0"
                                }
                                "\$n", "\$n." -> {
                                    goalSummary.value = durations
                                }
                                "\$fv.", "\$fv" -> {
                                    goalSummary.value = pmtResponse.futureValue.toCurrencyWithSpace()
                                }
                                "#i", "#i." -> {
                                    goalSummary.value = "${goal.inflation ?: "0"}"
                                }
                                "#dp", "#dp." -> {
                                    goalSummary.value = goal.getDPAmount() ?: "0"
                                }
                                "#n", "#n." -> {
                                    goalSummary.value = durations
                                }
                            }
                            binder.setVariable(BR.goalSummary, item)
                            binder.executePendingBindings()
                        }
                    }
                }

                getBinding().addThisGoal = View.OnClickListener {
                    getViewModel().addGoal(goal).observe(this, Observer { apiResponse ->
                        apiResponse?.let { funds ->
                            startFragment(RecommendedFragment.newInstance(), R.id.frmContainer)
                            postSticky(funds)
                            postSticky(goal)
                        }
                    })
                }

                tvSetCustom?.setOnClickListener { v ->
                    /*if (goal.customPMT == null && goal.isCustomInvestment()) {
                        goal.customPMT = goal.pmt
                    }*/
                    v.context.investGoalDialog(goal = goal) { amountLumpsum: String, amountSIP: String, duration: String ->
                        if (goal.isCustomInvestment()) {
                            if (goal.pmt != amountSIP.replace(",", "").toDoubleOrNull())
                                goal.customPMT = amountSIP.replace(",", "").toDoubleOrNull()
                        } else {
                            goal.setPMT(amountSIP)
                        }
                        goal.setPVAmount(amountLumpsum)
                        goal.setNDuration(duration)
                        durations = duration
                        getViewModel().calculatePMT(goal).observe(this, pmt)
                    }
                }
                getViewModel().calculatePMT(goal).observe(this, pmt)
            }
        })

        tvWhyInflationMatter?.setOnClickListener { v ->
            getViewModel().whyInflationMatter.get()?.let {
                getViewModel().whyInflationMatter.set(!it)
            }
        }
    }

    private fun isValid(goal: Goal.Data.GoalData): Boolean {
        try {
            val cv: Goal.Data.GoalData.Question? = if (goal.isCustomInvestment()) goal.getCV() else goal.getPMT()
            val n = goal.getN()
            return if (cv != null && n != null) {
                val amount = "${cv.ans}".replace(",", "")
                return if (TextUtils.isEmpty(amount) || amount.toDouble() < cv.minValue) {
                    var msg = "Please enter a valid number above".plus(" ".plus(cv.minValue))
                    context?.simpleAlert(msg)
                    false
                } else if (TextUtils.isEmpty(n.ans) || n.ans.toDouble() !in n.minValue..n.maxValue.toDouble()) {
                    var msg = "Please enter a valid number of years between"
                            .plus(" ".plus(n.minValue))
                            .plus(" to ".plus(n.maxValue.toIntOrNull()))
                    context?.simpleAlert(msg)
                    false
                } else if (goal.inflation == null || "${goal.inflation}" == "0") {
                    context?.simpleAlert("Please enter inflation")
                    false
                } else
                    true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun setGoalSummary(amount: String, durations: String, pmt: Double, goal: Goal.Data.GoalData) {

        var futureValueSummary = "${goal.futureValueSummary}"
        if (futureValueSummary.contains("\$n")) {
            futureValueSummary = futureValueSummary.replace("\$n", durations.toColorFromHTML())
        }
        if (futureValueSummary.contains("\$fv")) {
            futureValueSummary = futureValueSummary.replace("\$fv", amount.toColorFromHTML())
        }
        if (futureValueSummary.contains("\$pmt")) {
            futureValueSummary = futureValueSummary.replace("\$pmt", pmt.toCurrency().toColorFromHTML())
        }
        futureValueSummary = if (futureValueSummary.contentEquals("year")) futureValueSummary.replace("year", durations.toYearWord()) else futureValueSummary.replace("years", durations.toYearWord())
        getViewModel().gSummary.set(futureValueSummary.toHTMl())


        var yearSummary = "${goal.yearSummary}".replace("\$n", durations.toColorAndUnderlineFromHTML())
        yearSummary = if (yearSummary.contentEquals("year")) yearSummary.replace("year", durations.toYearWord()) else yearSummary.replace("years", durations.toYearWord())
        getViewModel().tmpFor.set(yearSummary.toHTMl())


        val lumpsumSummary = "${goal.lumpsumSummary}".replace("\$pv", "${if (TextUtils.isEmpty(goal.getPVAmount())) "0" else goal.getPVAmount()}".toColorAndUnderlineFromHTML())
        getViewModel().lumpsumpFor.set(lumpsumSummary.toHTMl())
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(goal: com.tarrakki.api.model.Goal.Data.GoalData) {
        if (getViewModel().goalVM.value == null) {
            goal.customPMT = null
            getViewModel().goalVM.value = goal
        }
        //EventBus.getDefault().removeStickyEvent(goal)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(goal: GoalSavedResponse.Data) {
        if (getViewModel().goalVM.value == null) {
            getViewModel().goalVM.value = goal.getGoal()
        }
        removeStickyEvent(goal)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle argument.
         * @return A new instance of fragment YourGoalSummaryFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = YourGoalSummaryFragment().apply { arguments = basket }
    }
}
