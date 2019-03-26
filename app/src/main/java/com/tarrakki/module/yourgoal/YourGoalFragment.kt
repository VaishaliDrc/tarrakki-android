package com.tarrakki.module.yourgoal


import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.tarrakki.R
import com.tarrakki.api.model.Goal
import com.tarrakki.databinding.FragmentYourGoalBinding
import com.tarrakki.databinding.PagetYourGoalStepBinding
import com.tarrakki.databinding.QuestionAmountBinding
import com.tarrakki.databinding.QuestionBooleanBinding
import kotlinx.android.synthetic.main.fragment_your_goal.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.BR
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setPageAdapter
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.inflate
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [YourGoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
const val KEY_GOAL_ID = "key_goal_id"

class YourGoalFragment : CoreFragment<YourGoalVM, FragmentYourGoalBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.your_goal)

    override fun getLayout(): Int {
        return R.layout.fragment_your_goal
    }

    override fun createViewModel(): Class<out YourGoalVM> {
        return YourGoalVM::class.java
    }

    override fun setVM(binding: FragmentYourGoalBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().goalVM.observe(this, Observer {
            it?.let { goal ->
                goal.questions.forEach { q ->
                    q.ansBoolean = true
                    q.ans = ""
                }
                val dataList = goal.questions.chunked(2)
                mPager.setPageAdapter(R.layout.paget_your_goal_step, dataList as ArrayList<List<Goal.Data.GoalData.Question>>) { binder: PagetYourGoalStepBinding, item: List<Goal.Data.GoalData.Question> ->
                    binder.yourGoal = getViewModel().yourGoalSteps[dataList.indexOf(item)]
                    binder.ivStep2.visibility = if (dataList.size == 3) View.VISIBLE else View.INVISIBLE
                    if (dataList.size == 1) {
                        binder.mStepLine.visibility = View.INVISIBLE
                        binder.ivStep1.visibility = View.INVISIBLE
                        binder.ivStep2.visibility = View.INVISIBLE
                        binder.ivStep3.visibility = View.INVISIBLE
                    }
                    binder.btnNext.setOnClickListener { onNext(dataList.indexOf(item)) }
                    binder.btnPrevious.alpha = if (dataList.indexOf(item) == 0) 0.6f else 1f
                    binder.btnPrevious.isEnabled = dataList.indexOf(item) != 0
                    binder.btnPrevious.setOnClickListener { onPrevious(dataList.indexOf(item)) }
                    binder.goal = goal
                    binder.llContainer.removeAllViews()
                    var mViewBoolean: QuestionBooleanBinding? = null
                    item.forEach { question ->
                        val mView: ViewDataBinding = DataBindingUtil.bind(mPager.inflate(question.layoutId()))!!
                        mView.setVariable(BR.question, question)
                        if (item.size == 2 && (item.indexOf(question) + 1) % 2 == 0)
                            mView.setVariable(BR.onAction, TextView.OnEditorActionListener { v, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    v.dismissKeyboard()
                                    v.clearFocus()
                                    return@OnEditorActionListener true
                                }
                                return@OnEditorActionListener false
                            })
                        if (item.size == 2 && question.questionType != "boolean" && (item.indexOf(question) + 1) % 2 == 0) {
                            try {
                                if (mView is QuestionAmountBinding) {
                                    mView.edtInvestAmount.imeOptions = EditorInfo.IME_ACTION_DONE
                                    /*val max = goal.getCVAmount()?.replace(",", "")?.toIntOrNull()
                                    max?.let { maxValue ->
                                        mView.edtInvestAmount.setMinMax(1, maxValue - 1)
                                    }*/
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        } else if (item.size == 1 && question.questionType != "boolean") {
                            try {
                                if (mView is QuestionAmountBinding) {
                                    mView.edtInvestAmount.imeOptions = EditorInfo.IME_ACTION_DONE
                                    /*val max = goal.getCVAmount()?.replace(",", "")?.toIntOrNull()
                                    max?.let { maxValue ->
                                        mView.edtInvestAmount.setMinMax(1, maxValue - 1)
                                    }*/
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        mView.executePendingBindings()
                        when {
                            mView is QuestionBooleanBinding -> {
                                if (mViewBoolean == null)
                                    mViewBoolean = mView
                                binder.llContainer.addView(mView.root)
                            }
                            mViewBoolean != null -> {
                                mViewBoolean?.expandableLayout?.addView(mView.root)
                            }
                            else -> binder.llContainer.addView(mView.root)
                        }
                    }
                    binder.executePendingBindings()
                }
            }
        })

    }

    private fun onNext(position: Int) {
        val index = position
        getViewModel().goalVM.value?.let { goal ->
            val dataList = goal.questions.chunked(2)
            val questions = dataList[index]
            var isValidate = false
            val isBoolean: Boolean
            when (questions.size) {
                2 -> {
                    val item1 = questions[0]
                    val item2 = questions[1]
                    isBoolean = item1.questionType == "boolean"
                    if (isBoolean) {
                        if (item1.ansBoolean && TextUtils.isEmpty(item2.ans)) {
                            context?.simpleAlert(getString(R.string.alert_req_answers))
                        } else if (item1.ansBoolean && !TextUtils.isEmpty(item2.ans)) {
                            isValidate = isValid(item2)
                        } else {
                            item2.ans = ""
                            isValidate = true
                        }
                    } else {
                        if (TextUtils.isEmpty(item1.ans) && TextUtils.isEmpty(item2.ans)) {
                            context?.simpleAlert(getString(R.string.alert_req_answers))
                        } else if (isValid(item1) && isValid(item2)) {
                            isValidate = true
                        }
                    }
                }
                else -> {
                    val item1 = questions[0]
                    isBoolean = item1.questionType == "boolean"
                    isValidate = if (isBoolean) {
                        true
                    } else {
                        isValid(item1)
                    }
                }
            }
            if (isValidate && (dataList.size - 1) == position) {
                startFragment(YourGoalSummaryFragment.newInstance(), R.id.frmContainer)
                postSticky(goal)
            } else if (isValidate) {
                mPager.setCurrentItem(position + 1, true)
            }
        }
    }

    private fun isValid(question: Goal.Data.GoalData.Question): Boolean {
        try {
            return when ("${question.parameter}") {
                "cv" -> {
                    val amount = "${question.ans}".replace(",", "")
                    if (TextUtils.isEmpty(amount) || amount.toDouble() < question.minValue) {
                        var msg = getString(R.string.alert_valid_above).plus(" ".plus(question.minValue))
                        context?.simpleAlert(msg)
                        false
                    } else
                        true
                }
                "tax_pmt" -> {
                    val amount = "${question.ans}".replace(",", "")
                    if (TextUtils.isEmpty(amount) || amount.toDouble() < question.minValue) {
                        var msg = getString(R.string.alert_valid_above).plus(" ".plus(question.minValue))
                        context?.simpleAlert(msg)
                        false
                    } else
                        true
                }
                "pv" -> {
                    var amount = ""
                    getViewModel().goalVM.value?.let { goal ->
                        amount = "${goal.getInvestmentAmount()}".replace(",", "")
                    }
                    val pvAmount = "${question.ans}".replace(",", "")
                    //amount = "${question.ans}".replace(",", "")
                    if (!TextUtils.isEmpty(amount) && !TextUtils.isEmpty(pvAmount) && pvAmount.toDouble() > amount.toDouble()) {
                        context?.simpleAlert(getString(R.string.alert_valid_goal_lumpsum))
                        false
                    } else
                        true
                }
                "n" -> {
                    if (TextUtils.isEmpty(question.ans) || question.ans.toDouble() !in question.minValue..question.maxValue.toDouble()) {
                        var msg = getString(R.string.alert_valid_years)
                                .plus(" ".plus(question.minValue))
                                .plus(" to ".plus(question.maxValue))
                        context?.simpleAlert(msg)
                        false
                    } else
                        true
                }
                "dp" -> {
                    if (TextUtils.isEmpty(question.ans) || question.ans.toDouble() !in question.minValue..question.maxValue.toDouble()) {
                        var msg = getString(R.string.alert_valid_percentage)
                                .plus(" ".plus(question.minValue))
                                .plus(" to ".plus(question.maxValue))
                        context?.simpleAlert(msg)
                        false
                    } else
                        true
                }
                else -> true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun onPrevious(position: Int) {
        mPager.setCurrentItem(position - 1, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(goal: Goal.Data.GoalData) {
        if (getViewModel().goalVM.value == null) {
            getViewModel().goalVM.value = goal
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment YourGoalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) = YourGoalFragment().apply { arguments = bundle }
    }
}
