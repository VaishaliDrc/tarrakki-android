package com.tarrakki.module.portfolio

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import kotlin.concurrent.thread

class PortfolioVM : FragmentViewModel() {

    val portfolioData = MutableLiveData<UserPortfolioResponse>()
    val userPortfolioData = MutableLiveData<GetLiquiLoanPortFolioBaseResponse>()
    val isRefreshing = MutableLiveData<Boolean>()
    val isDirectEmpty = ObservableField<Boolean>(false)
    val isGoalEmpty = ObservableField<Boolean>(false)
    val isPortfolioEmpty = ObservableField<Boolean>(false)

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

    var portfolioList = ArrayList<GetLiquiLoanPortFolioData>()

    fun getLiquiloansPortfolioAPI() {
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getLiquiloansPortfolio(App.INSTANCE.getUserId()),

                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        o.printResponse()
                        if (o.status?.code == 1){
                            val data = o.data?.parseTo<GetLiquiLoanPortFolioBaseResponse>()
                            data?.let {
                                userPortfolioData.postValue(it)
                                portfolioList.clear()
                                portfolioList.addAll(it.data)

                                if(portfolioList.isNotEmpty())
                                    isPortfolioEmpty.set(false)
                                else
                                    isPortfolioEmpty.set(true)
                            }
                        }else{
                            postError("${o.status?.message}")
                        }
                        dismissProgress()
                    }

                    override fun onFailure(throwable: Throwable) {
                        throwable.postError()
                        dismissProgress()
                    }
                }
        )
    }

}

data class StopSIP(val transactionId: Int, val folioNo: String, val date: String) {
    var item: UserPortfolioResponse.Data.GoalBasedInvestment.Fund? = null
}