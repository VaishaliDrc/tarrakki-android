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
import com.tarrakki.api.model.PMTResponse
import com.tarrakki.databinding.FragmentYourGoalSummaryBinding
import com.tarrakki.module.recommended.RecommendedFragment
import com.xiaofeng.flowlayoutmanager.Alignment
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.fragment_your_goal_summary.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.toCurrency
import org.supportcompact.ktx.toCurrencyWithSpace


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
        /*getViewModel().goalVM.observe(this, Observer {
            getBinding().goal = it
            getBinding().executePendingBindings()
            getViewModel().goalSummary.forEach { item ->
                when (item.type) {
                    "investment" -> {
                        item.txt = it?.investmentAmount!!
                    }
                    "durations" -> {
                        item.txt = it?.investmentDuration!!
                    }
                }
            }
            var investAmount = ""
            var durations = ""
            var txtDuration: TextView? = null
            getViewModel().goalSummary.forEach { item ->
                val lParam: FlexboxLayout.LayoutParams
                when (item.widgetType) {
                    LABEL -> {
                        lParam = FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        val label = DataBindingUtil.inflate<SummaryLabelBinding>(layoutInflater, R.layout.summary_label, mFlax, true)
                        label.goalSummary = item
                        label.executePendingBindings()
                        addSpace(item, label, lParam)
                        if ("durations" == item.type) {
                            txtDuration = label.edtDurations
                        }
                    }
                    TXT -> {
                        lParam = FlexboxLayout.LayoutParams(56f.convertToPx().toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
                        val txt = DataBindingUtil.inflate<SummaryTxtBinding>(layoutInflater, R.layout.summary_txt, mFlax, true)
                        txt.goalSummary = item
                        txt.executePendingBindings()
                        addSpace(item, txt, lParam)
                        if ("durations" == item.type) {
                            durations = item.txt
                            txt.edtDurations.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(p0: Editable?) {
                                    durations = p0.toString()
                                    setGoalSummary(amount = investAmount, durations = durations)
                                    txtDuration?.text = durations
                                }

                                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                }

                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                }
                            })
                        }
                    }
                    TXT_CURRENCY -> {
                        lParam = FlexboxLayout.LayoutParams(120f.convertToPx().toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
                        val txtCurrency = DataBindingUtil.inflate<SummaryTxtCurrencyBinding>(layoutInflater, R.layout.summary_txt_currency, mFlax, true)
                        txtCurrency.goalSummary = item
                        txtCurrency.executePendingBindings()
                        addSpace(item, txtCurrency, lParam)
                        if ("investment" == item.type) {
                            investAmount = item.txt
                            txtCurrency.edtInvestAmount.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(p0: Editable?) {

                                }

                                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                                }

                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                    investAmount = p0.toString()
                                    setGoalSummary(amount = investAmount, durations = durations)
                                }
                            })
                        }
                    }
                    TXT_PERCENTAGE -> {
                        lParam = FlexboxLayout.LayoutParams(56f.convertToPx().toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
                        val tatPercentage = DataBindingUtil.inflate<SummaryTxtPercetageBinding>(layoutInflater, R.layout.summary_txt_percetage, mFlax, true)
                        tatPercentage.goalSummary = item
                        tatPercentage.executePendingBindings()
                        addSpace(item, tatPercentage, lParam)
                    }
                }
            }
            setGoalSummary(amount = investAmount, durations = durations)
        })*/

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
                        tvPMT.text = pmtResponse.pmt.toCurrency()
                        setGoalSummary(investAmount, durations, pmtResponse.pmt, goal.getPVAmount())
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
                                                    goal.inflation = v.text.toString().toIntOrNull()
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
                                    v.dismissKeyboard()
                                    v.clearFocus()
                                    getViewModel().calculatePMT(goal).observe(this, pmt)
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
                getViewModel().calculatePMT(goal).observe(this, pmt)
            }
        })

        tvWhyInflationMatter?.setOnClickListener { v ->
            getViewModel().whyInflationMatter.get()?.let {
                getViewModel().whyInflationMatter.set(!it)
            }
        }

        btnAddThisGoal?.setOnClickListener {
            startFragment(RecommendedFragment.newInstance(), R.id.frmContainer)
        }
    }

    private fun setGoalSummary(amount: String, durations: String, pmt: Double, lumpsum: String? = null) {
        /*getBinding().goal?.investmentAmount = amount
        getBinding().goal?.investmentDuration = durations*/
        val ssb = SpannableStringBuilder("To achieve your goal of saving ")
        ssb.append(SpannableString(getString(R.string.rs_symbol).plus(" ").plus(amount)).apply {
            setSpan(RelativeSizeSpan(1.1f), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.WHITE), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        ssb.append(" in ")
        ssb.append(SpannableString(durations).apply {
            setSpan(RelativeSizeSpan(1.1f), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.WHITE), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        ssb.append(" years, you'll have to invest ")
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
        ssb1.append(" years")
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
