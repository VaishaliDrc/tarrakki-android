package com.tarrakki.module.yourgoal


import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
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
        getViewModel().goalSummary.forEach { item ->
            when (item.widgetType) {
                LABEL -> {
                    val label = DataBindingUtil.inflate<SummaryLabelBinding>(layoutInflater, R.layout.summary_label, mFlax, true)
                    label.goalSummary = item
                    label.executePendingBindings()
                    addSpace(item, label.root)
                }
                TXT -> {
                    val txt = DataBindingUtil.inflate<SummaryTxtBinding>(layoutInflater, R.layout.summary_txt, mFlax, true)
                    txt.goalSummary = item
                    txt.executePendingBindings()
                    addSpace(item, txt.root)
                }
                TXT_CURRENCY -> {
                    val txtCurrency = DataBindingUtil.inflate<SummaryTxtCurrencyBinding>(layoutInflater, R.layout.summary_txt_currency, mFlax, true)
                    txtCurrency.goalSummary = item
                    txtCurrency.executePendingBindings()
                    addSpace(item, txtCurrency.root)
                }
                TXT_PERCENTAGE -> {
                    val tatPercentage = DataBindingUtil.inflate<SummaryTxtPercetageBinding>(layoutInflater, R.layout.summary_txt_percetage, mFlax, true)
                    tatPercentage.goalSummary = item
                    tatPercentage.executePendingBindings()
                    addSpace(item, tatPercentage.root)
                }
            }
        }
    }

    private fun addSpace(item: GoalSummary, view: View) {
        val lParam = FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        when (item.widgetSpace) {
            RIGHT_SPACE -> {
                lParam.setMargins(0, 0, 8.0f.convertToPx().toInt(), 0)
                view.layoutParams = lParam
            }
            LEFT_SPACE -> {
                lParam.setMargins(8.0f.convertToPx().toInt(), 0, 0, 0)
                view.layoutParams = lParam
            }
            BOTH_SIDE_SPACE -> {
                lParam.setMargins(8.0f.convertToPx().toInt(), 0, 8.0f.convertToPx().toInt(), 0)
                view.layoutParams = lParam
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
