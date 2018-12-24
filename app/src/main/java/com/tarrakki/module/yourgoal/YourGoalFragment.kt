package com.tarrakki.module.yourgoal


import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.Goal
import com.tarrakki.databinding.FragmentYourGoalBinding
import com.tarrakki.databinding.PagetYourGoalStepBinding
import com.tarrakki.databinding.QuestionBooleanBinding
import kotlinx.android.synthetic.main.fragment_your_goal.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setPageAdapter
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
const val KEY_GOAL = "key_goal"

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
                }
                val dataList = goal.questions.chunked(2)
                mPageGoal?.setPageAdapter(R.layout.paget_your_goal_step, dataList as ArrayList<List<Goal.Data.GoalData.Question>>) { binder: PagetYourGoalStepBinding, item: List<Goal.Data.GoalData.Question> ->
                    binder.yourGoal = getViewModel().yourGoalSteps[mPageGoal.currentItem]
                    binder.ivStep2.visibility = if (dataList.size == 3) View.VISIBLE else View.INVISIBLE
                    binder.btnNext.setOnClickListener { onNext() }
                    //binder.btnNext.alpha = if (mPageGoal.currentItem == (dataList.size - 1)) 0.6f else 1f
                    //binder.btnNext.isEnabled = mPageGoal.currentItem != (dataList.size - 1)
                    binder.btnPrevious.alpha = if (mPageGoal.currentItem == 0) 0.6f else 1f
                    binder.btnPrevious.isEnabled = mPageGoal.currentItem != 0
                    binder.btnPrevious.setOnClickListener { onPrevious() }
                    binder.goal = goal
                    binder.llContainer.removeAllViews()
                    var mViewBoolean: QuestionBooleanBinding? = null
                    item.forEach { question ->
                        val mView: ViewDataBinding = DataBindingUtil.bind(mPageGoal.inflate(question.layoutId()))!!
                        mView.setVariable(BR.question, question)
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


    private fun onNext() {
        val index = mPageGoal.currentItem
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
                            context?.simpleAlert("All the questions mentioned above are mandatory so please answers them first to continue!")
                        } else if (item1.ansBoolean && !TextUtils.isEmpty(item2.ans)) {
                            isValidate = isValid(item2)
                        } else {
                            isValidate = true
                        }
                    } else {
                        if (TextUtils.isEmpty(item1.ans) && TextUtils.isEmpty(item2.ans)) {
                            context?.simpleAlert("All the questions mentioned above are mandatory so please answers them first to continue!")
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
            if (isValidate && (dataList.size - 1) == mPageGoal.currentItem) {
                startFragment(YourGoalSummaryFragment.newInstance(), R.id.frmContainer)
                postSticky(goal)
            } else if (isValidate) {
                mPageGoal.setCurrentItem(mPageGoal.currentItem + 1, true)
                mPageGoal?.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun isValid(question: Goal.Data.GoalData.Question): Boolean {
        try {
            return when ("${question.parameter}") {
                "cv", "pv" -> {
                    val amount = "${question.ans}".replace(",", "")
                    if (TextUtils.isEmpty(amount) || amount.toInt() < question.minValue) {
                        var msg = "Please enter a valid number above".plus(" ".plus(question.minValue))
                        context?.simpleAlert(msg)
                        false
                    } else
                        true
                }
                "n" -> {
                    if (TextUtils.isEmpty(question.ans) || question.ans.toInt() !in question.minValue..question.maxValue.toInt()) {
                        var msg = "Please enter a valid number of years between"
                                .plus(" ".plus(question.minValue))
                                .plus(" to ".plus(question.maxValue.toIntOrNull()))
                        context?.simpleAlert(msg)
                        false
                    } else
                        true
                }
                "dp" -> {
                    if (TextUtils.isEmpty(question.ans) || question.ans.toInt() !in question.minValue..question.maxValue.toInt()) {
                        var msg = "Please enter a valid percentage between"
                                .plus(" ".plus(question.minValue))
                                .plus(" to ".plus(question.maxValue.toInt()))
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

    private fun onPrevious() {
        mPageGoal.setCurrentItem(mPageGoal.currentItem - 1, true)
        mPageGoal?.adapter?.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(goal: Goal.Data.GoalData) {
        if (getViewModel().goalVM.value == null) {
            getViewModel().goalVM.value = goal
        }
        //EventBus.getDefault().removeStickyEvent(goal)
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
