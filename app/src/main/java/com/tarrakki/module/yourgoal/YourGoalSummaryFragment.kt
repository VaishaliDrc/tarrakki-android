package com.tarrakki.module.yourgoal


import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.tarrakki.R
import com.tarrakki.databinding.*
import com.tarrakki.module.goal.Goal
import com.tarrakki.module.yourgoal.SummaryWidget.*
import com.tarrakki.module.yourgoal.WidgetSpace.*
import kotlinx.android.synthetic.main.fragment_your_goal_summary.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.convertToPx


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

    override fun createReference() {
        getViewModel().goalVM.value = arguments?.getSerializable(KEY_GOAL) as Goal
        getViewModel().goalVM.observe(this, Observer {
            getBinding().goal = it
            getBinding().executePendingBindings()
        })

        tvWhyInflationMatter?.setOnClickListener { _ ->
            getViewModel().whyInflationMatter.get()?.let {
                getViewModel().whyInflationMatter.set(!it)
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
    }

    private fun setGoalSummary(amount: String, durations: String) {
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
        ssb.append(SpannableString(getString(R.string.rs_symbol).plus(" 1,583")).apply {
            setSpan(RelativeSizeSpan(1.2f), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.WHITE), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        ssb.append(" every month.")
        getViewModel().gSummary.set(ssb)
    }

    private fun addSpace(item: GoalSummary, binder: ViewDataBinding, lParam: FlexboxLayout.LayoutParams) {
        when (item.widgetSpace) {
            RIGHT_SPACE -> {
                lParam.setMargins(0, 16f.convertToPx().toInt(), 8f.convertToPx().toInt(), 0)
                binder.root.layoutParams = lParam
            }
            LEFT_SPACE -> {
                lParam.setMargins(8.0f.convertToPx().toInt(), 16f.convertToPx().toInt(), 0, 0)
                binder.root.layoutParams = lParam
            }
            BOTH_SIDE_SPACE -> {
                lParam.setMargins(8.0f.convertToPx().toInt(), 16f.convertToPx().toInt(), 8.0f.convertToPx().toInt(), 0)
                binder.root.layoutParams = lParam
            }
            else -> {
                lParam.setMargins(0, 16f.convertToPx().toInt(), 0, 0)
                binder.root.layoutParams = lParam
            }
        }
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
