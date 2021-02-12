package com.tarrakki.module.tarrakkipro

import android.text.TextUtils
import android.view.View
import androidx.annotation.DrawableRes
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.BuildConfig
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import com.tarrakki.getVisibility
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*

class TarrakkiProVM : FragmentViewModel() {

    val proBenefitList = arrayListOf<ProbenefitList>()
    var firstPlanPrice = 0
    var SecondPlanPrice = 0


    init {
        setBenefitList()
        firstPlanPrice = 200
        SecondPlanPrice = 300
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

    fun getPaymentTokenAPI():MutableLiveData<PaymentTokenData> {
        val apiResponse = MutableLiveData<PaymentTokenData>()
        showProgress()
        val json = JsonObject()
        json.addProperty("amount", "600"  )
        json.addProperty("order_id",  "123456789012" )
        json.addProperty("currency", "INR")
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getPaymentToken(App.INSTANCE.getUserId(),data),

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
