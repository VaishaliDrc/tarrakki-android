package com.tarrakki.module.savedgoals

import android.support.annotation.DrawableRes
import com.tarrakki.R
import org.supportcompact.FragmentViewModel

class SavedGoalsVM : FragmentViewModel() {

    val savedGoals = arrayListOf<SavedGoal>()

    init {
        savedGoals.add(SavedGoal(
                "HOME GOAL",
                382884.00,
                "5 Years",
                R.drawable.own_a_home))
        savedGoals.add(SavedGoal(
                "AUTO GOAL",
                382884.00,
                "5 Years",
                R.drawable.own_a_home))
    }
}

data class SavedGoal(
        var name: String,
        var amount: Double,
        var duration: String,
        @DrawableRes
        var imgRes: Int
)