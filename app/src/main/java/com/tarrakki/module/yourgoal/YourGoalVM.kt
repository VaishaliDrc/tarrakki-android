package com.tarrakki.module.yourgoal

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.LayoutRes
import com.tarrakki.R
import com.tarrakki.module.goal.Goal
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class YourGoalVM : FragmentViewModel() {

    val goalVM: MutableLiveData<Goal?> = MutableLiveData()
    val yourGoalSteps = arrayListOf<YourGoalSteps>()

    init {
        yourGoalSteps.add(YourGoalSteps(
                layout = R.layout.set_your_goal_step1,
                question = "Current value of your dream home",
                question2 = "Numbers of years you'd like to buy it")
        )
        yourGoalSteps.add(YourGoalSteps(
                layout = R.layout.set_your_goal_step2,
                question = "Will you take a home loan?",
                ans = true,
                question2 = "Home much do they plan to make as down payment?")
        )
        yourGoalSteps.add(YourGoalSteps(
                layout = R.layout.set_your_goal_step3,
                question = "Do you want to make a lumpsum investment",
                ans = true,
                question2 = "Home much?")
        )
    }
}

class YourGoalSteps(@LayoutRes
                    val layout: Int,
                    var question: String,
                    var answered: String = "",
                    var ans: Boolean = false,
                    var question2: String,
                    var answered2: String = "",
                    var ans2: Boolean = false
) : WidgetsViewModel {
    override fun layoutId() = layout
}