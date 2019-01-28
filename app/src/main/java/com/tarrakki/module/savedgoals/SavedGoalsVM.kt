package com.tarrakki.module.savedgoals

import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.support.annotation.DrawableRes
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.GoalSavedResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

class SavedGoalsVM : FragmentViewModel() {

    val savedGoals = arrayListOf<SavedGoal>()
    val saveGoalResponse = MutableLiveData<GoalSavedResponse>()
    val isEmpty = MutableLiveData<Boolean>()

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

    fun getSavedGoals(userId : String?): MutableLiveData<GoalSavedResponse> {
        val apiResponse = MutableLiveData<GoalSavedResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getSavedGoals(userId),
                apiNames = WebserviceBuilder.ApiNames.getGoals,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                o.printResponse()
                                thread {
                                    val data = o.data?.parseTo<GoalSavedResponse>()
                                    saveGoalResponse.postValue(data)
                                }
                            } else {
                                isEmpty.value = true
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
        return saveGoalResponse
    }

    fun deleteSavedGoals(userId : Int?): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .deleteSavedGoals(userId),
                apiNames = WebserviceBuilder.ApiNames.deleteSavedGoals,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                o.printResponse()
                                apiResponse.value = o
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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

data class SavedGoal(
        var name: String,
        var amount: Double,
        var duration: String,
        @DrawableRes
        var imgRes: Int
)