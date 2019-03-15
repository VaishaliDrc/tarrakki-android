package com.tarrakki.module.portfolio

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.getUserId
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.subscribeToSingle
import kotlin.concurrent.thread

class PortfolioVM : FragmentViewModel() {

    val portfolioData = MutableLiveData<UserPortfolioResponse>()
    val isRefreshing = MutableLiveData<Boolean>()
    val isDirectEmpty = ObservableField<Boolean>(false)
    val isGoalEmpty = ObservableField<Boolean>(false)

    fun getUserPortfolio(isRefreshing: Boolean = false) {
        if (!isRefreshing)
            EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getUserPortfolio(App.INSTANCE.getUserId()),
                apiNames = WebserviceBuilder.ApiNames.getFundDetails,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                thread {
                                    o.printResponse()
                                    val fundDetails = o.data?.parseTo<UserPortfolioResponse>()
                                    portfolioData.postValue(fundDetails)
                                }
                            } else {
                                EventBus.getDefault().post(DISMISS_PROGRESS)
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        } else {
                            EventBus.getDefault().post(DISMISS_PROGRESS)
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

}