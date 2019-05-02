package com.tarrakki.module.debitcart

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress

class DebitCartInfoVM : FragmentViewModel() {

    val folioNo = ObservableField<String>()
    val cardHolerName = ObservableField<String>()
    val mothersName = ObservableField<String>()
    val dob = ObservableField<String>()
    val folioData = arrayListOf<FolioData>()

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