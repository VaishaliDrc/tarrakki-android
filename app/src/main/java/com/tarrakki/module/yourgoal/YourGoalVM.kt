package com.tarrakki.module.yourgoal

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.text.SpannableStringBuilder
import android.view.View
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.module.goal.Goal
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class YourGoalVM : FragmentViewModel() {

    val goalVM: MutableLiveData<Goal?> = MutableLiveData()
    val yourGoalSteps = arrayListOf<YourGoalSteps>()
    val whyInflationMatter = ObservableField(true)
    val goalSummary = arrayListOf<GoalSummary>()
    val gSummary = ObservableField<SpannableStringBuilder>()

    init {
        yourGoalSteps.add(YourGoalSteps(
                layout = R.layout.set_your_goal_step1,
                question = "Current value of your dream home",
                question2 = "Numbers of years you'd like to buy it",
                drawable1 = R.drawable.icon_status_purple_right,
                drawable2 = R.drawable.icon_status_gray_middle,
                drawable3 = R.drawable.icon_status_gray_left)
        )
        yourGoalSteps.add(YourGoalSteps(
                layout = R.layout.set_your_goal_step2,
                question = "Will you take a home loan?",
                ans = true,
                question2 = "Home much do they plan to make as down payment?",
                drawable1 = R.drawable.icon_status_green_right,
                drawable2 = R.drawable.icon_status_purple_middle,
                drawable3 = R.drawable.icon_status_gray_left)
        )
        yourGoalSteps.add(YourGoalSteps(
                layout = R.layout.set_your_goal_step3,
                question = "Do you want to make a lumpsum investment",
                ans = true,
                question2 = "Home much?",
                drawable1 = R.drawable.icon_status_green_right,
                drawable2 = R.drawable.icon_status_green_middle,
                drawable3 = R.drawable.icon_status_purple_left)
        )

        goalSummary.add(GoalSummary("You'd like to purchase a house currently costing", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("10,00,000", SummaryWidget.TXT_CURRENCY, WidgetSpace.RIGHT_SPACE, "investment"))
        goalSummary.add(GoalSummary("in", SummaryWidget.LABEL, WidgetSpace.BOTH_SIDE_SPACE))
        goalSummary.add(GoalSummary("5", SummaryWidget.TXT, WidgetSpace.BOTH_SIDE_SPACE, "durations"))
        goalSummary.add(GoalSummary("years.", SummaryWidget.LABEL, WidgetSpace.RIGHT_SPACE))
        goalSummary.add(GoalSummary("Since you plan to pay", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("30", SummaryWidget.TXT_PERCENTAGE, WidgetSpace.BOTH_SIDE_SPACE))
        goalSummary.add(GoalSummary("down payment", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("during your purchase, you would need to build a", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("corpus of", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("3,82,884", SummaryWidget.TXT_CURRENCY, WidgetSpace.BOTH_SIDE_SPACE))
        goalSummary.add(GoalSummary("by the end of", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("5", SummaryWidget.LABEL, WidgetSpace.BOTH_SIDE_SPACE, "durations"))
        goalSummary.add(GoalSummary("your (inflation adjusted)", SummaryWidget.LABEL, WidgetSpace.NOTHING))

    }
}

data class GoalSummary(
        var txt: String,
        var widgetType: SummaryWidget,
        var widgetSpace: WidgetSpace,
        var type: String = ""
) : BaseObservable()

enum class SummaryWidget {
    LABEL, TXT, TXT_CURRENCY, TXT_PERCENTAGE
}

enum class WidgetSpace {
    LEFT_SPACE, RIGHT_SPACE, BOTH_SIDE_SPACE, NOTHING
}

data class YourGoalSteps(@LayoutRes
                         val layout: Int,
                         var question: String,
                         var answered: String = "",
                         var ans: Boolean = false,
                         var question2: String,
                         var answered2: String = "",
                         var ans2: Boolean = false,
                         @DrawableRes
                         var drawable1: Int,
                         @DrawableRes
                         var drawable2: Int,
                         @DrawableRes
                         var drawable3: Int
) : BaseObservable(), WidgetsViewModel {
    override fun layoutId() = layout
    var onNext: View.OnClickListener? = null
    var onPrevious: View.OnClickListener? = null
    @get:Bindable
    var isSelected: Boolean = ans
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}