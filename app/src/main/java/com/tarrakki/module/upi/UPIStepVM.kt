package com.tarrakki.module.upi

import androidx.lifecycle.MutableLiveData
import com.tarrakki.App
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.CheckPaymentStatus
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import com.tarrakki.api.subscribeToSingle
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.getUserId

class UPIStepVM : FragmentViewModel() {
    var transaction_ids: String = ""

    fun checkPaymentStatus(): MutableLiveData<CheckPaymentStatus> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val apiResponse = MutableLiveData<CheckPaymentStatus>()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).checkPaymentStatus(App.INSTANCE.getUserId(), JSONArray(transaction_ids).toString()),
                object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        if (o.status?.code == 1) {
                            o.printResponse()
                            val apiClient = o.data?.parseTo<CheckPaymentStatus>()
                            apiResponse.postValue(apiClient)
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        throwable.printStackTrace()
                    }
                })
        return apiResponse
    }

}