package com.tarrakki.module.goal

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.DrawableRes
import com.tarrakki.BR
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import java.io.Serializable

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

data class Goal(var title: String, @DrawableRes var imgUrl: Int) : BaseObservable(), WidgetsViewModel, Serializable {

    @Bindable
    var investmentAmount: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.investmentAmount)
        }

    @Bindable
    var investmentDuration: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.investmentDuration)
        }

    override fun layoutId(): Int {
        return R.layout.row_goal_home_list_item
    }
}
