package com.tarrakki.module.goal

import android.support.annotation.DrawableRes
import com.tarrakki.R
import org.supportcompact.FragmentViewModel

class GoalVM : FragmentViewModel() {

    var goals = ArrayList<Goal>()

    init {
        goals.add(Goal("Wealth creation", R.drawable.wealth_creation))
        goals.add(Goal("Holiday", R.drawable.holiday))
        goals.add(Goal("Electronic Gadget", R.drawable.electronic_gadget))
        goals.add(Goal("Automobile", R.drawable.automobile))
        goals.add(Goal("Own a Home", R.drawable.own_a_home))
        goals.add(Goal("Emergency Fund", R.drawable.emergency_fund))
        goals.add(Goal("Children's Education", R.drawable.childrens_education))
        goals.add(Goal("Wedding", R.drawable.wedding))
    }
}

data class Goal(var title: String, @DrawableRes var imgUrl: Int)
