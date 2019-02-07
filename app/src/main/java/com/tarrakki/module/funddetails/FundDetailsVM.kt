package com.tarrakki.module.funddetails

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.IntRange
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.module.invest.FundType
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

class FundDetailsVM : FragmentViewModel() {

    val earningBase = arrayListOf<TopHolding>()
    val durations = arrayListOf<FundType>()
    val fundDetailsResponse = MutableLiveData<FundDetails>()
    var fund: InvestmentFunds? = null

    init {

        durations.add(FundType("1Y"))
        durations.add(FundType("5Y"))
        durations.add(FundType("10Y"))
        durations.add(FundType("Since Inception", _isSelected = true))
    }

    fun getFundDetails(id: String) {
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getFundDetails(id),
                apiNames = WebserviceBuilder.ApiNames.getFundDetails,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                thread {
                                    o.printResponse()
                                    val fundDetails = o.data?.parseTo<FundDetails>()
                                    fundDetails?.topTenHoldings
                                    fundDetails?.returnsHistory
                                    fundDetailsResponse.postValue(fundDetails)
                                    EventBus.getDefault().post(DISMISS_PROGRESS)
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

data class KeyInfo(var key: String, var value: String?)

data class TopHolding(
        var name: String,
        @IntRange(from = 0, to = 100) var process: Int,
        var percentageHolding: Double,
        var amount: Double = 0.0
) {
    fun getReturns(): String = if (percentageHolding == 0.0) "NA" else "$percentageHolding".plus("% ").plus(App.INSTANCE.getString(R.string.return_))
}
