package com.tarrakki.module.zyaada

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
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

class TarrakkiZyaadaVM : FragmentViewModel() {

    val whatIsTarrakkiZyaada = ObservableField(false)
    val whereIsMyMoney = ObservableField(false)
    val cashfreeamount = ObservableField("")

    fun getTarrakkiZyaada(): MutableLiveData<TarrakkiZyaadaResponse> {
        showProgress()
        val response = MutableLiveData<TarrakkiZyaadaResponse>()
        val json = JsonObject()
        json.addProperty("user_id", App.INSTANCE.getUserId())
        val data = json.toString().toEncrypt()
        json.printRequest()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getTarrakkiZyaada(data), object : SingleCallback1<ApiResponse> {

            override fun onSingleSuccess(o: ApiResponse) {
                thread {
                    if (o.status?.code == 1) {
                        val responseData = o.data?.parseTo<TarrakkiZyaadaResponse>()
                        response.postValue(responseData)
                    } else {
                        postError("${o.status?.message}")
                        dismissProgress()
                    }
                }
            }

            override fun onFailure(throwable: Throwable) {
                dismissProgress()
                throwable.postError()
            }
        })
        return response
    }

    fun getAddressAmountAPI(): MutableLiveData<AddressAmountData> {
        val addressData = MutableLiveData<AddressAmountData>()
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getAddressAmount(App.INSTANCE.getUserId()),

                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        o.printResponse()
                        if (o.status?.code == 1){
                            val data = o.data?.parseTo<AddressAmountData>()
                            data?.let {
                                addressData.value = it
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
        return addressData
    }
}