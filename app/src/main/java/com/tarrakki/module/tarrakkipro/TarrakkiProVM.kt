package com.tarrakki.module.tarrakkipro

import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import kotlin.concurrent.thread

class TarrakkiProVM : FragmentViewModel() {

    val proBenefitList = arrayListOf<ProbenefitList>()
    var firstPlanPrice = 0
    var SecondPlanPrice = 0
    var tarrakkiProAndEquityPricingResponse = MutableLiveData<TarrakkiProAndEquityPricingResponse>()


    init {
        setBenefitList()
        firstPlanPrice = 200
        SecondPlanPrice = 300
    }

      fun getRandomOrderId():String{
        val tsLong = System.currentTimeMillis()
        val ts = tsLong.toString()
        return ts + "pro"
    }


    private fun setBenefitList() {
        proBenefitList.clear()
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.buy_sell_recommendation), R.drawable.ic_bsh_recomm))
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.prime_ratings), R.drawable.ic_prime_ratings))
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.mutual_fund_advisory), R.drawable.ic_mutual_fund_advisory))
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.detailed_risk), R.drawable.ic_detailed_risk_assessment))
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.portfolio_construction), R.drawable.ic_portfolio_construction))
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.portfolio_health_check_up), R.drawable.ic_portfolio_health_check_up__quarterly_))
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.on_call_appointment), R.drawable.ic_on_call_appointment_with_a_wealth_manager__monthly_))
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.premium_support), R.drawable.ic_premium_customer_support))
        proBenefitList.add(ProbenefitList(App.INSTANCE.getString(R.string.portfolio_analytics), R.drawable.ic_portfolio_analytics))

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

    fun getPaymentTokenAPI():MutableLiveData<PaymentTokenData> {
        val apiResponse = MutableLiveData<PaymentTokenData>()
        showProgress()
        val json = JsonObject()
        json.addProperty("amount", "600"  )
        json.addProperty("order_id",  getRandomOrderId())
        json.addProperty("currency", "INR")
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getTProAndEquityPaymentToken(App.INSTANCE.getUserId(),data),

                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        o.printResponse()
                        if (o.status?.code == 1){
                            val data = o.data?.parseTo<PaymentTokenData>()
                            data?.let {
                                apiResponse.value = it
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
        return apiResponse
    }



}

data class ProbenefitList(var title: String, @DrawableRes var imgRes: Int)
