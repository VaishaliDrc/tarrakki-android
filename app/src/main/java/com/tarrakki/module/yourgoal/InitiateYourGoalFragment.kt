package com.tarrakki.module.yourgoal


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.tarrakki.R
import com.tarrakki.databinding.FragmentInitiateYourGoalBinding
import com.tarrakki.module.goal.Goal
import kotlinx.android.synthetic.main.fragment_initiate_your_goal.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [InitiateYourGoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
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
        getViewModel().goal.set(arguments?.getSerializable(KEY_GOAL) as Goal)
        btnContinue?.setOnClickListener {
            getViewModel().iniateYourGoal.get()?.let { item ->
                when {
                    TextUtils.isEmpty(item.answered) -> context?.simpleAlert("All the fields are mandatory so please enter the required fields first.")
                    TextUtils.isEmpty(item.answered2) -> context?.simpleAlert("All the fields are mandatory so please enter the required fields first.")
                    else -> startFragment(YourGoalFragment.newInstance(Bundle().apply { putSerializable(KEY_GOAL, getViewModel().goal.get()) }), R.id.frmContainer)
                }
            }
        }
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
        fun newInstance(basket: Bundle) = InitiateYourGoalFragment().apply { arguments = basket }
    }
}
