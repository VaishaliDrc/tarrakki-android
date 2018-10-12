package com.tarrakki.module.yourgoal

import android.arch.lifecycle.MutableLiveData
import com.tarrakki.module.goal.Goal
import org.supportcompact.FragmentViewModel

class YourGoalVM : FragmentViewModel() {

    val goalVM: MutableLiveData<Goal?> = MutableLiveData()

}