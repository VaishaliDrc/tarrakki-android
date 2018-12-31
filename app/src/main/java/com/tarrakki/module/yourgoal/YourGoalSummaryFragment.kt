package com.tarrakki.module.yourgoal


import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.Goal
import com.tarrakki.api.model.PMTResponse
import com.tarrakki.databinding.FragmentYourGoalSummaryBinding
import com.tarrakki.module.recommended.RecommendedFragment
import com.tarrakki.toYearWord
import com.xiaofeng.flowlayoutmanager.Alignment
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.fragment_your_goal_summary.*
import org.greenrobot.eventbus.EventBus
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
                        tvPMT.text = pmtResponse.pmt.toCurrency()
                        setGoalSummary(pmtResponse.futureValue.toCurrencyWithSpace(), durations, pmtResponse.pmt, goal.getPVAmount())
                        goalSummarys = goal.goalSummary()
                        rvGoalSummary?.setUpMultiViewRecyclerAdapter(goalSummarys) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                            val goalSummary = item as GoalSummary
                            binder.setVariable(BR.onAction, TextView.OnEditorActionListener { v, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    goalSummarys.forEach { item ->
                                        val summary = item as GoalSummary
                                        when (summary.txt) {
                                            "#cv" -> {
                                                goal.setCVAmount(summary.value)
                                                investAmount = summary.value
                                            }
                                            "#fv" -> {
                                                //goalSummary.value = "${pmtResponse.futureValue}"
                                            }
                                            "#pv" -> {
                                                goal.setPVAmount(summary.value)
                                            }
                                            "\$n" -> {
                                                goalSummary.value = durations
                                            }
                                            "\$fv" -> {
                                                goalSummary.value = pmtResponse.futureValue.toCurrencyWithSpace()
                                            }
                                            "#i" -> {
                                                //goalSummary.value = "${goal.inflation}"
                                                if (goalSummary.txt == "#i")
                                                    goal.inflation = v.text.toString().toDoubleOrNull()
                                            }
                                            "#dp" -> {
                                                //goalSummary.value = "${goal.getDPAmount() ?: "0"}"
                                                goal.setDPAmount(summary.value)
                                            }
                                            "#n" -> {
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
                                "#cv" -> {
                                    goalSummary.value = "${goal.getCVAmount() ?: "0"}"
                                }
                                "#fv" -> {
                                    goalSummary.value = "${pmtResponse.futureValue}"
                                }
                                "#pv" -> {
                                    goalSummary.value = "${goal.getPVAmount() ?: "0"}"
                                }
                                "\$n" -> {
                                    goalSummary.value = durations
                                }
                                "\$fv" -> {
                                    goalSummary.value = pmtResponse.futureValue.toCurrencyWithSpace()
                                }
                                "#i" -> {
                                    goalSummary.value = "${goal.inflation ?: "0"}"
                                }
                                "#dp" -> {
                                    goalSummary.value = "${goal.getDPAmount() ?: "0"}"
                                }
                                "#n" -> {
                                    goalSummary.value = durations
                                }
                            }
                            binder.setVariable(BR.goalSummary, item)
                            binder.executePendingBindings()
                        }
                    }
                }
                btnAddThisGoal?.setOnClickListener {
                    getViewModel().addGoal(goal).observe(this, Observer { apiResponse ->
                        startFragment(RecommendedFragment.newInstance(), R.id.frmContainer)
                    })
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
            val cv = goal.getCV()
            val n = goal.getN()
            return if (cv != null && n != null) {
                val amount = "${cv.ans}".replace(",", "")
                return if (TextUtils.isEmpty(amount) || amount.toInt() < cv.minValue) {
                    var msg = "Please enter a valid number above".plus(" ".plus(cv.minValue))
                    context?.simpleAlert(msg)
                    false
                } else if (TextUtils.isEmpty(n.ans) || n.ans.toInt() !in n.minValue..n.maxValue.toDouble()) {
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


    private fun setGoalSummary(amount: String, durations: String, pmt: Double, lumpsum: String? = null) {
        /*getBinding().goal?.investmentAmount = amount
        getBinding().goal?.investmentDuration = durations*/
        val ssb = SpannableStringBuilder("To achieve your goal of saving ")
        ssb.append(SpannableString(amount/*getString(R.string.rs_symbol).plus(" ").plus(amount)*/).apply {
            setSpan(RelativeSizeSpan(1.1f), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.WHITE), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        ssb.append(" in ")
        ssb.append(SpannableString(durations).apply {
            setSpan(RelativeSizeSpan(1.1f), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.WHITE), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        ssb.append(" ${durations.toYearWord()}, you'll have to invest ")
        ssb.append(SpannableString(pmt.toCurrencyWithSpace()/*getString(R.string.rs_symbol).plus(" 1,583")*/).apply {
            setSpan(RelativeSizeSpan(1.2f), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.WHITE), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        ssb.append(" every month.")
        getViewModel().gSummary.set(ssb)

        val ssb1 = SpannableStringBuilder("every month, for the next ")
        ssb1.append(SpannableString(durations).apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#00CB00")), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(UnderlineSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        ssb1.append(" ".plus(durations.toYearWord())/*" years"*/)
        getViewModel().tmpFor.set(ssb1)

        val ssb2 = SpannableStringBuilder("A lumpsum of ")
        ssb2.append(SpannableString(if (TextUtils.isEmpty(lumpsum)) "0" else lumpsum).apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#00CB00")), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(UnderlineSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        ssb2.append("  upfront, to achieve your goal")
        getViewModel().lumpsumpFor.set(ssb2)
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(goal: com.tarrakki.api.model.Goal.Data.GoalData) {
        if (getViewModel().goalVM.value == null) {
            getViewModel().goalVM.value = goal
        }
        EventBus.getDefault().removeStickyEvent(goal)
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
