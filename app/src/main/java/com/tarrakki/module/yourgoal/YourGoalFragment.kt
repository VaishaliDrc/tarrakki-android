package com.tarrakki.module.yourgoal


import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.databinding.FragmentYourGoalBinding
import com.tarrakki.module.goal.Goal
import kotlinx.android.synthetic.main.fragment_your_goal.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setMultiViewPageAdapter
import org.supportcompact.ktx.simpleAlert

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
        getViewModel().goalVM.value = arguments?.getSerializable(KEY_GOAL) as Goal
        getViewModel().goalVM.observe(this, Observer {
            getBinding().goal = it
            getBinding().executePendingBindings()
        })
        mPageGoal?.setMultiViewPageAdapter(getViewModel().yourGoalSteps) { binder: ViewDataBinding, item: YourGoalSteps ->
            item.onNext = View.OnClickListener {
                onNext()
            }
            item.onPrevious = View.OnClickListener {
                onPrevious()
            }
            binder.setVariable(BR.goal, getViewModel().goalVM.value)
            binder.setVariable(BR.yourGoal, item)
            binder.executePendingBindings()
        }
    }

    private fun onNext() {
        val index = mPageGoal.currentItem
        val item = getViewModel().yourGoalSteps[index]
        when (index) {
            0 -> {
                if (TextUtils.isEmpty(item.answered)) {
                    context?.simpleAlert("Please enter amount")
                } else if (TextUtils.isEmpty(item.answered2)) {
                    context?.simpleAlert("Please enter years")
                } else {
                    mPageGoal.setCurrentItem(mPageGoal.currentItem + 1, true)
                }
            }
            1 -> {
                if (item.isSelected && TextUtils.isEmpty(item.answered2)) {
                    context?.simpleAlert("Please enter a valid percentage between 1 and 99")
                } else {
                    mPageGoal.setCurrentItem(mPageGoal.currentItem + 1, true)
                }
            }
            2 -> {
                if (TextUtils.isEmpty(item.answered2)) {
                    context?.simpleAlert("Please enter amount")
                }
            }
        }
    }

    private fun onPrevious() {
        if (mPageGoal.currentItem <= getViewModel().yourGoalSteps.size - 1) {
            mPageGoal.setCurrentItem(mPageGoal.currentItem - 1, true)
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
