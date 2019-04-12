package com.tarrakki.module.zyaada

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.TarrakkiZyaadaResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import kotlin.concurrent.thread

class TarrakkiZyaadaVM : FragmentViewModel() {

    val whatIsTarrakkiZyaada = ObservableField(true)
    val whereIsMyMoney = ObservableField(false)

    fun getTarrakkiZyaada(): MutableLiveData<TarrakkiZyaadaResponse> {
        showProgress()
        val response = MutableLiveData<TarrakkiZyaadaResponse>()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getTarrakkiZyaada(), object : SingleCallback1<ApiResponse> {

            override fun onSingleSuccess(o: ApiResponse) {
                thread {
                    if (o.status?.code == 1) {
                        val responseData = o.data?.parseTo<TarrakkiZyaadaResponse>()
                        response.postValue(responseData)
                    } else {
                        postError("${o.status?.message}")
                    }
                    dismissProgress()
                }
            }

            override fun onFailure(throwable: Throwable) {
                dismissProgress()
                throwable.postError()
            }
        })
        return response
    }

}