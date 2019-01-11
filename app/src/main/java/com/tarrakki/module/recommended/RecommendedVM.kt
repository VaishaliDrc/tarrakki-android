package com.tarrakki.module.recommended

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.support.annotation.ColorRes
import android.text.SpannableStringBuilder
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.RecommendedFunds
import com.tarrakki.api.model.printResponse
import com.tarrakki.api.model.toDecrypt
import com.tarrakki.module.investmentstrategies.InvestmentOption
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class RecommendedVM : FragmentViewModel() {

    val AMCList = arrayListOf<AMC>()
    val investment = MutableLiveData<InvestmentOption>()
    /****Api***/
    val funds = MutableLiveData<RecommendedFunds>()
    val goalVM: MutableLiveData<com.tarrakki.api.model.Goal.Data.GoalData> = MutableLiveData()
    val lumpsumpFor = ObservableField<SpannableStringBuilder>()
    var userGoalId: String = ""


    init {
        AMCList.add(AMC(
                "ICICI Prudential NIFTY Next 50 Index Growth Direct Plan",
                "Equity - India Multi Cap Equity",
                0.0,
                2300.toDouble(),
                16.1f,
                "Last 5Y",
                48f,
                R.color.balanced_fund_color))
        AMCList.add(AMC(
                "DSP 10Y G SEC Growth Direct Plan",
                "Debt - Bonds - Government Bond Fund",
                3900.toDouble(),
                1800.toDouble(),
                7f,
                "Last 5Y",
                38.5f,
                R.color.equity_fund_color))

        AMCList.add(AMC(
                "IDFC NIFTY Growth Direct Plan",
                "Equity - India Large Cap Equity",
                6200.toDouble(),
                600.toDouble(),
                14.7f,
                "Last 5Y",
                13.5f,
                R.color.debt_fund_color
        ))
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


data class AMC(
        var name: String,
        var description: String,
        var oneTime: Double,
        var monthly: Double,
        var returns: Float,
        var returnsSince: String,
        var weightage: Float,
        @ColorRes var fundColor: Int = R.color.equity_fund_color
)