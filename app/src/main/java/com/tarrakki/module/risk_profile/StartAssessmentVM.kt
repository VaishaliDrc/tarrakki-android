package com.tarrakki.module.risk_profile

import androidx.lifecycle.MutableLiveData
import com.tarrakki.App
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.printResponse
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress

class StartAssessmentVM : FragmentViewModel() {

    fun getRiskAssessmentQuestions(): MutableLiveData<ApiResponse> {
        showProgress()
        val apiResponse = MutableLiveData<ApiResponse>()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getRiskAssessmentQuestions(App.INSTANCE.getUserId()), object : SingleCallback1<ApiResponse> {
            override fun onSingleSuccess(o: ApiResponse) {
                dismissProgress()
                o.printResponse()
                if (o.status?.code == 0) {
                    apiResponse.value = o
                } else {
                    postError("${o.status?.message}")
                }
            }

            override fun onFailure(throwable: Throwable) {
                dismissProgress()
                throwable.postError()
            }
        })
        return apiResponse
    }

}