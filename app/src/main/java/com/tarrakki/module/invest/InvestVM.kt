package com.tarrakki.module.invest

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.graphics.Color
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.InvestmentFunds
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.toEncrypt
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import org.supportcompact.ktx.toInt
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.io.Serializable

class InvestVM : FragmentViewModel() {

    var fundTypes = arrayListOf<FundType>()
    var fundReturns = arrayListOf<FundType>()
    var arrRiskLevel = arrayListOf<RiskLevel>()
    val loadMore = ObservableField(false)
    val filter = ObservableField<Boolean>(true)

    val response = MutableLiveData<InvestmentFunds>()
    var isInit = true

    /**Filter**/
    val ourRecommended = MutableLiveData<Boolean>()
    val riskLevel = MutableLiveData<Int?>()
    val sortByReturn = MutableLiveData<Pair<String, String>>()
    val investmentType = MutableLiveData<Pair<Boolean, Boolean>>()
    val category = MutableLiveData<Int?>()
    val subcategory = MutableLiveData<Int?>()
    val searchBy = MutableLiveData<String>()

    init {

        fundTypes.add(FundType(name = "Tarrakki Recommended", key = "is_tarrakki_recommended"))
        fundTypes.add(FundType(name = "NFO", key = "nfo"))


        fundReturns.add(FundType("1Y", "sort_by", "ttr_return_1_yr"))
        fundReturns.add(FundType("3Y", "sort_by", "ttr_return_3_yr", true))
        fundReturns.add(FundType("5Y", "sort_by", "ttr_return_5_yr"))
        fundReturns.add(FundType("AUM", "sort_by", "fna_aum"))

        val riskLevel = App.INSTANCE.resources.getStringArray(R.array.risk_level)
        val riskLevelColor = App.INSTANCE.resources.getStringArray(R.array.risk_level_color)
        riskLevel.forEachIndexed { index, value ->
            arrRiskLevel.add(RiskLevel(value, Color.parseColor(riskLevelColor[index]), index == 0))
        }
    }

    fun getFunds(offset: Int = 0): MutableLiveData<InvestmentFunds> {

        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        val filter = JsonObject()
        fundTypes.forEach { item ->
            filter.addProperty(item.key, item.isSelected.toInt())
        }
        filter.addProperty("risk_level_id", riskLevel.value ?: 0)
        if (investmentType.value == null) {
            /**Default value **/
            filter.addProperty("growth", 1)
            filter.addProperty("dividend_payout", 0)
        } else {
            investmentType.value?.let {
                filter.addProperty("growth", it.first.toInt())
                filter.addProperty("dividend_payout", it.second.toInt())
            }
        }
        filter.addProperty("category", category.value ?: 0)
        filter.addProperty("sub_category", subcategory.value ?: 0)
        json.add("filters", filter)
        if (sortByReturn.value == null) {
            /**Default value **/
            json.addProperty("sort_by", "ttr_return_3_yr")
        } else {
            sortByReturn.value?.let {
                json.addProperty(it.first, it.second)
            }
        }
        json.addProperty("search_by_name", searchBy.value)
        json.addProperty("limit", 10)
        json.addProperty("offset", offset)
        val data = json.toString().toEncrypt()
        e("Request Data=>$json")
        e("Request Encrypted Data=>$data")
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getFunds(data),
                apiNames = WebserviceBuilder.ApiNames.getFunds,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                response.value = o.data?.parseTo<InvestmentFunds>()
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
        return response
    }

}

data class Fund(
        var name: String,
        var description: String,
        var currentReturn: Float,
        var returnSinceLaunch: Float,
        var returnInYear: Float,
        var volatility: Float,
        var threeYearOfFundReturn: Float,
        var threeYearOfFDReturn: Float,
        var hasNegativeReturn: Boolean = false,
        var temp: String = "39.550",
        var fundType: String = "Equity",
        var whatNew: String = "Folio"
) : BaseObservable(), Serializable {
    @get:Bindable
    var hasOneTimeAmount: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasOneTimeAmount)
        }
    @get:Bindable
    var date: String = "07 Sep 2018"
        set(value) {
            field = value
            notifyPropertyChanged(BR.date)
        }
}

data class FundType(
        var name: String,
        val key: String = "",
        val value: String = "",
        var _isSelected: Boolean = false
) : BaseObservable() {
    @get:Bindable
    var isSelected: Boolean = _isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}

data class RiskLevel(
        var name: String,
        var color: Int,
        var _isSelected: Boolean = false
) : BaseObservable() {
    @get:Bindable
    var isSelected: Boolean = _isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}