package com.tarrakki.module.debitcart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import retrofit2.http.Field
import retrofit2.http.Path

class DebitCartInfoVM : FragmentViewModel() {

    val folioNo = ObservableField<String>()
    val cardHolerName = ObservableField<String>()
    val mothersName = ObservableField<String>()
    val dob = ObservableField<String>()
    val folioData = arrayListOf<FolioData>()

    private fun randomWithRange(): Int {
        val min = 1
        val max = 9999
        val range = (max - min) + 1
        return ((Math.random() * range) + min).toInt()
    }

    fun getPaymentTokenAPI():MutableLiveData<PaymentTokenData> {
        val apiResponse = MutableLiveData<PaymentTokenData>()
        showProgress()
        val json = JsonObject()
        json.addProperty("amount", "1")
        json.addProperty("order_id", folioNo.get()+"_"+App.INSTANCE.getUserId() + randomWithRange())
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

    fun applyForDebitCart(): MutableLiveData<ApiResponse> {
        val response = MutableLiveData<ApiResponse>()
        showProgress()
        val json = JsonObject()
        json.addProperty("folio_number", folioNo.get())
        json.addProperty("name_on_card", cardHolerName.get())
        json.addProperty("mother_maiden_name", mothersName.get())
        json.addProperty("dob", dob.get())
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .applyForDebitCart(App.INSTANCE.getUserId(), data),
                object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        if (o.status?.code == 1) {
                            response.postValue(o)
                        } else {
                            EventBus.getDefault().post(ShowError("${o.status?.message}"))
                        }
                        dismissProgress()
                    }

                    override fun onFailure(throwable: Throwable) {
                        dismissProgress()
                        throwable.postError()
                    }
                })
        return response
    }

    fun getFolioList(): MutableLiveData<ApiResponse> {
        val response = MutableLiveData<ApiResponse>()
        showProgress()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getFolioList(App.INSTANCE.getUserId()),
                object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        if (o.status?.code == 1) {
                            o.printResponse()
                            response.postValue(o)
                        } else {
                            EventBus.getDefault().post(ShowError("${o.status?.message}"))
                        }
                        dismissProgress()
                    }

                    override fun onFailure(throwable: Throwable) {
                        dismissProgress()
                        throwable.postError()
                    }
                })
        return response
    }

}