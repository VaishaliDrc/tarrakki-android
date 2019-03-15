package com.tarrakki.module.yourgoal

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.text.Spanned
import com.google.gson.Gson
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.subscribeToSingle

class YourGoalVM : FragmentViewModel() {

    val goalVM: MutableLiveData<com.tarrakki.api.model.Goal.Data.GoalData> = MutableLiveData()
    val yourGoalSteps = arrayListOf<YourGoalSteps>()
    val whyInflationMatter = ObservableField(true)
    val tmpFor = ObservableField<Spanned>()
    val lumpsumpFor = ObservableField<Spanned>()
    val gSummary = ObservableField<Spanned>()
    val hasQuestions = ObservableField<Boolean>(true)

    init {

        yourGoalSteps.add(YourGoalSteps(
                drawable1 = R.drawable.icon_status_purple_right,
                drawable2 = R.drawable.icon_status_gray_middle,
                drawable3 = R.drawable.icon_status_gray_left)
        )
        yourGoalSteps.add(YourGoalSteps(
                drawable1 = R.drawable.icon_status_green_right,
                drawable2 = R.drawable.icon_status_purple_middle,
                drawable3 = R.drawable.icon_status_gray_left)
        )
        yourGoalSteps.add(YourGoalSteps(
                drawable1 = R.drawable.icon_status_green_right,
                drawable2 = R.drawable.icon_status_green_middle,
                drawable3 = R.drawable.icon_status_purple_left)
        )
    }

    fun getGoalById(goalId: String) {
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getGoalsById(goalId),
                apiNames = WebserviceBuilder.ApiNames.getGoalById,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                try {
                                    /*
                                    * {"data":{"inflation":5,"goal_data":{"id":1,"goal_summary":"You'd like to purchase a house currently costing #cv in #n years. Since you plan to pay #dp downpayment during your purchase, you need to build a corpus of $fv by the end of $n years adjusted for inflation adjusted at #i","goal":"OWN A HOME","description":"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.","questions":[{"dependent_question":null,"parameter":"cv","question_order":1,"min_value":1000,"question":"How much does your dream home cost today?","max_value":5000000000,"question_type":"float"},{"dependent_question":null,"parameter":"n","question_order":2,"min_value":0,"question":"In how many years do you want to buy this home?","max_value":15,"question_type":"float"},{"dependent_question":null,"parameter":null,"question_order":3,"min_value":null,"question":"Do you plan to take a home loan?","max_value":null,"question_type":"boolean"},{"dependent_question":null,"parameter":"dp","question_order":4,"min_value":1,"question":"How much down payment do you intend to pay?","max_value":99,"question_type":"float"},{"dependent_question":null,"parameter":null,"question_order":5,"min_value":null,"question":"Do you want to make a lump sum investment?","max_value":null,"question_type":"boolean"},{"dependent_question":null,"parameter":"pv","question_order":6,"min_value":1000,"question":"How much?","max_value":null,"question_type":"float"}],"order_sequence":1,"goal_image":"\/media\/goals\/Own-a-home.png","intro_questions":[]}}}
                                    * */
                                    val json = JSONObject(o.data?.toDecrypt())
                                    val data = json.optJSONObject("data")
                                    val goal = Gson().fromJson(data.optString("goal_data"), Goal.Data.GoalData::class.java)
                                    goal?.inflation = data.optDouble("inflation")
                                    goalVM.value = goal
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                                }
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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
                            if ((o.status?.code == 1)) {
                                val data = o.data?.parseTo<PMTResponse>()
                                pmtResponse.value = data
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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

    fun addGoal(goal: com.tarrakki.api.model.Goal.Data.GoalData): MutableLiveData<RecommendedFunds> {
        val apiResponse = MutableLiveData<RecommendedFunds>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).addGoal(goal.addGoalData()),
                apiNames = WebserviceBuilder.ApiNames.addGoal,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            e("Api Response=>${o.data?.toDecrypt()}")
                            if (o.status?.code == 1) {
                                val fund = o.data?.parseTo<RecommendedFundsData>()
                                apiResponse.value = fund?.data
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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
        return apiResponse
    }

}

data class GoalSummary(
        var txt: String,
        @LayoutRes
        var layout: Int = R.layout.summary_label
) : WidgetsViewModel {
    var value: String = ""
    override fun layoutId(): Int {
        return layout
    }
}

data class YourGoalSteps(
        @DrawableRes
        var drawable1: Int = 0,
        @DrawableRes
        var drawable2: Int = 0,
        @DrawableRes
        var drawable3: Int = 0
)