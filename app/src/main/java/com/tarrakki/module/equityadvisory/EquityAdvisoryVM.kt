package com.tarrakki.module.equityadvisory

import android.text.TextUtils
import android.view.View
import androidx.annotation.DrawableRes
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import com.tarrakki.module.tarrakkipro.ProbenefitList
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import kotlin.concurrent.thread

class EquityAdvisoryVM : FragmentViewModel() {

    var equityAdvisoryVisibility = ObservableField(BuildConfig.FLAVOR.isTarrakki())
    val whyTarrakkiList = arrayListOf<WhyTarrakkiList>()
    val equityBenefitList = arrayListOf<WhyTarrakkiList>()
    var firstPlanPrice = 0
    var tarrakkiProAndEquityPricingResponse = MutableLiveData<TarrakkiProAndEquityPricingResponse>()

    init {
        setTarrakkiList()
        setEquityBenefitList()
        firstPlanPrice = 356

    }

    private fun getRandomOrderId():String{
        val tsLong = System.currentTimeMillis()
        val ts = tsLong.toString()
        return ts + "equ"
    }

    private fun setTarrakkiList() {
        whyTarrakkiList.clear()
        whyTarrakkiList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.sebi_registered_investment), R.drawable.ic_sebi_registered))
        whyTarrakkiList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.unbiased_research), R.drawable.ic_unbiased_research_advisory))
        whyTarrakkiList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.well_researched), R.drawable.ic_well_researched_stock_ideas))
        whyTarrakkiList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.complete_control), R.drawable.ic_complete_control_account))

    }

    private fun setEquityBenefitList() {
        equityBenefitList.clear()
        equityBenefitList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.model_portfolio), R.drawable.ic_model_portfolio))
        equityBenefitList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.recommended_weight), R.drawable.ic_recommended_weightages))
        equityBenefitList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.monthly_uopdates), R.drawable.ic_monthly_updates))
        equityBenefitList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.buy_sell_hold), R.drawable.ic_buy_sell_hold))
        equityBenefitList.add(WhyTarrakkiList(App.INSTANCE.getString(R.string.dedicated_helpdesk), R.drawable.ic_dedicated_helpdesk))

    }


    fun getTarrakkiAndWquityPricing(): MutableLiveData<TarrakkiProAndEquityPricingResponse> {
        subscribeToSingle(
                ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getTarrakkiProAndEquityPricing(),
                object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            if (o.status?.code == 1) {
                                o.printResponse()
                                val res = o.data?.parseTo<TarrakkiProAndEquityPricingResponse>()
                                tarrakkiProAndEquityPricingResponse.postValue(res)
                            } else {
                                postError("${o.status?.message}")
                            }
                            dismissProgress()
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        throwable.postError()
                        dismissProgress()
                    }
                }
        )
        return tarrakkiProAndEquityPricingResponse
    }


    fun getPaymentTokenAPI(amount : Int, planType : String): MutableLiveData<PaymentTokenData> {
        val apiResponse = MutableLiveData<PaymentTokenData>()
        showProgress()
        val json = JsonObject()
        json.addProperty("amount", amount)
        json.addProperty("order_id",  getRandomOrderId())
        json.addProperty("tranx_type", planType)

        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getTProAndEquityPaymentToken(App.INSTANCE.getUserId(), data),

                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        o.printResponse()
                        if (o.status?.code == 1) {
                            val data = o.data?.parseTo<PaymentTokenData>()
                            data?.let {
                                apiResponse.value = it
                            }
                        } else {
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
        return apiResponse
    }


}

data class WhyTarrakkiList(var title: String, @DrawableRes var imgRes: Int)

