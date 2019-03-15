package com.tarrakki.module.goal

import android.arch.lifecycle.MutableLiveData
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.model.Goal
import com.tarrakki.api.subscribeToSingle

class GoalVM : FragmentViewModel() {

    val goalList = MutableLiveData<com.tarrakki.api.model.Goal>()

    fun getGoals(isRefreshing: Boolean = false): MutableLiveData<com.tarrakki.api.model.Goal> {
        if (!isRefreshing)
            EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getGoals(),
                apiNames = WebserviceBuilder.ApiNames.getGoals,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                o.printResponse()
                                val data = o.data?.parseTo<Goal>()
                                goalList.value = data
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
        return goalList
    }
}