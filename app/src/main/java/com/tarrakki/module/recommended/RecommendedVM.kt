package com.tarrakki.module.recommended

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.support.annotation.ColorRes
import android.text.Spanned
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
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

    val investment = MutableLiveData<InvestmentOption>()
    val funds = MutableLiveData<RecommendedFunds>()
    val goalVM: MutableLiveData<com.tarrakki.api.model.Goal.Data.GoalData> = MutableLiveData()
    val lumpsumpFor = ObservableField<Spanned>()
    var userGoalId: String = ""

    val secondLevelCategory = MutableLiveData<HomeData.Data.Category.SecondLevelCategory>()
    val thirdLevelCategory = MutableLiveData<HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory>()
    val recommendedFunds = MutableLiveData<List<InvestmentRecommendFundResponse.Data>>()

    val categoryImg = ObservableField<String>("")
    val categoryName = ObservableField<String>("")
    val categoryshortDes = ObservableField<String>("")
    val categoryDes = ObservableField<String>("")

    val secondaryCategoryName = ObservableField<String>("")
    val secondaryCategoryImage = ObservableField<String>("")
    val secondaryCategoryDes = ObservableField<String>("")
    val secondaryCategoryShortDes = ObservableField<String>("")
    val returnLevel = ObservableField<String>("")
    val riskLevel = ObservableField<String>("")

    val sipAmount = ObservableField<Int>()
    val lumpsumAmount = ObservableField<Int>()
    val isFrom = ObservableField<Int>()

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