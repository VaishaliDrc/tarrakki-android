package com.tarrakki.module.yourgoal


import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.tarrakki.R
import com.tarrakki.api.model.Goal
import com.tarrakki.databinding.FragmentInitiateYourGoalBinding
import kotlinx.android.synthetic.main.fragment_initiate_your_goal.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [InitiateYourGoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InitiateYourGoalFragment : CoreFragment<YourGoalVM, FragmentInitiateYourGoalBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.your_goal)

    override fun getLayout(): Int {
        return R.layout.fragment_initiate_your_goal
    }

    override fun createViewModel(): Class<out YourGoalVM> {
        return YourGoalVM::class.java
    }

    override fun setVM(binding: FragmentInitiateYourGoalBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        arguments?.let {
            val goalId = it.getString(KEY_GOAL_ID)
            getViewModel().getGoalById("$goalId")
        }
        val data = arrayListOf<String>()//resources.getStringArray(R.array.automobile)
        getViewModel().goalVM.observe(this, Observer { it ->
            it?.let { goal ->
                goal.questions = goal.questions.sortedBy { q -> q.questionOrder }
                getViewModel().hasQuestions.set(goal.introQuestions?.isNotEmpty() == true)
                getBinding().goal = goal
                getBinding().executePendingBindings()
                data.clear()
                if (goal.introQuestions?.isNotEmpty() == true && goal.introQuestions.find { "Select".equals(it.questionType, true) } != null) {
                    val arr = goal.introQuestions.find { "Select".equals(it.questionType, true) }
                    arr?.options?.split(",")?.forEach { item ->
                        data.add(item)
                    }
                    val adapter = ArrayAdapter(
                            activity,
                            R.layout.simple_spinner_item_gray_small,
                            data
                    )
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    spnCategory.adapter = adapter
                }
            }
        })
        btnContinue?.setOnClickListener {
            getViewModel().goalVM.value?.let { goal ->
                goal.setAnsQ2(edtQ2Answer.text.toString())
                startFragment(YourGoalFragment.newInstance(), R.id.frmContainer)
                postSticky(goal)
            }
        }
        spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                getViewModel().goalVM.value?.setAnsQ1(data[position])
            }
        }

        btnContinue1?.setOnClickListener {
            startFragment(YourGoalFragment.newInstance(), R.id.frmContainer)
            getViewModel().goalVM.value?.let { goal ->
                postSticky(goal)
            }
        }
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
         *
         * @param basket As Bundle.
         * @return A new instance of fragment InitiateYourGoalFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = InitiateYourGoalFragment().apply { arguments = basket }
    }
}
