package com.tarrakki.module.savedgoals

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.DrawableRes
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class SavedGoalsVM : FragmentViewModel() {

    val savedGoals = arrayListOf<SavedGoal>()
    val saveGoalResponse = MutableLiveData<List<GoalSavedResponse.Data>>()
    val refresh = MutableLiveData<Boolean>()

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

    fun getSavedGoals(userId: String?,isRefreshing: Boolean = false): MutableLiveData<List<GoalSavedResponse.Data>> {
        val apiResponse = MutableLiveData<GoalSavedResponse>()
        if (!isRefreshing)
            EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getSavedGoals(userId),
                apiNames = WebserviceBuilder.ApiNames.getGoals,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        refresh.value = true
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                o.printResponse()
                                val data = o.data?.parseTo<GoalSavedResponse>()
                                saveGoalResponse.value = data?.data
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
                        refresh.value = true
                    }
                }
        )
        return saveGoalResponse
    }

    fun deleteSavedGoals(userId: Int?): MutableLiveData<ApiResponse> {
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

    fun addGoalToCart(userGoalId: String): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).addGoalToCart(userGoalId),
                apiNames = WebserviceBuilder.ApiNames.addGoalToCart,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            e("Api Response=>${o.data?.toDecrypt()}")
                            o.printResponse()
                            if (o.status?.code == 1) {
                                apiResponse.value = o
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