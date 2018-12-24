package com.tarrakki.module.yourgoal

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.text.SpannableStringBuilder
import android.view.View
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.PMTResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.module.goal.Goal
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class YourGoalVM : FragmentViewModel() {

    val goalVM: MutableLiveData<com.tarrakki.api.model.Goal.Data.GoalData> = MutableLiveData()
    val yourGoalSteps = arrayListOf<YourGoalSteps>()
    val whyInflationMatter = ObservableField(true)
    //val goalSummary = arrayListOf<GoalSummary>()
    val tmpFor = ObservableField<SpannableStringBuilder>()
    val lumpsumpFor = ObservableField<SpannableStringBuilder>()
    val gSummary = ObservableField<SpannableStringBuilder>()
    val hasQuestions = ObservableField<Boolean>(true)

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

        /*goalSummary.add(GoalSummary("You'd like to purchase a house currently costing", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("", SummaryWidget.TXT_CURRENCY, WidgetSpace.RIGHT_SPACE, "investment"))
        goalSummary.add(GoalSummary("in", SummaryWidget.LABEL, WidgetSpace.BOTH_SIDE_SPACE))
        goalSummary.add(GoalSummary("", SummaryWidget.TXT, WidgetSpace.BOTH_SIDE_SPACE, "durations"))
        goalSummary.add(GoalSummary("years.", SummaryWidget.LABEL, WidgetSpace.RIGHT_SPACE))
        goalSummary.add(GoalSummary("Since you plan to pay", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("30", SummaryWidget.TXT_PERCENTAGE, WidgetSpace.BOTH_SIDE_SPACE))
        goalSummary.add(GoalSummary("down payment", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("during your purchase, you need to build a corpus", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("of", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("3,82,884", SummaryWidget.TXT_CURRENCY, WidgetSpace.BOTH_SIDE_SPACE))
        goalSummary.add(GoalSummary("by the end of", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("", SummaryWidget.LABEL, WidgetSpace.BOTH_SIDE_SPACE, "durations"))
        goalSummary.add(GoalSummary("year", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("(inflation adjusted at", SummaryWidget.LABEL, WidgetSpace.NOTHING))
        goalSummary.add(GoalSummary("6", SummaryWidget.TXT_PERCENTAGE, WidgetSpace.LEFT_SPACE))
        goalSummary.add(GoalSummary(")", SummaryWidget.LABEL, WidgetSpace.NOTHING))*/

    }

    fun calculatePMT(goal: com.tarrakki.api.model.Goal.Data.GoalData): MutableLiveData<PMTResponse> {
        val pmtResponse = MutableLiveData<PMTResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).calculatePMT(goal.getPMTJSON()),
                apiNames = WebserviceBuilder.ApiNames.calculatePMT,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if (o.status.code == 1) {
                                val data = o.data?.parseTo<PMTResponse>()
                                pmtResponse.value = data
                            } else {
                                EventBus.getDefault().post(ShowError(o.status.message))
                            }
                        } else {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        EventBus.getDefault().post(ShowError("${throwable.message}"))
                    }
                }
        )
        return pmtResponse
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
                         val layout: Int = 0,
                         var goal: Goal? = null,
                         var question: String,
                         var answered: String = "",
                         var ans: Boolean = false,
                         var question2: String,
//                         var answered2: String = "",
                         var ans2: Boolean = false,
                         @DrawableRes
                         var drawable1: Int = 0,
                         @DrawableRes
                         var drawable2: Int = 0,
                         @DrawableRes
                         var drawable3: Int = 0
) : BaseObservable(), WidgetsViewModel {

    constructor(q1: String, q2: String) : this(question = q1, question2 = q2)

    override fun layoutId() = layout
    var onNext: View.OnClickListener? = null
    var onPrevious: View.OnClickListener? = null

    @get:Bindable
    var answered2: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.answered2)
        }

    @get:Bindable
    var isSelected: Boolean = ans
        set(value) {
            field = value
            answered2 = if (value) answered2 else ""
            notifyPropertyChanged(BR.selected)
        }
    val onToggleClick = View.OnClickListener {
        isSelected = !isSelected
    }
}