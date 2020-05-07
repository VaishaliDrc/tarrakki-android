package com.tarrakki.module.risk_assessment_agree

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.RiskAssessmentQuestionsApiResponse
import com.tarrakki.api.model.printRequest
import com.tarrakki.api.model.toEncrypt
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress

class AssessmentDeclarationVM : FragmentViewModel() {

    var questions = MutableLiveData<RiskAssessmentQuestionsApiResponse>()


    fun submitRiskAssessmentAws(): MutableLiveData<ApiResponse> {
        showProgress()
        val apiResponse = MutableLiveData<ApiResponse>()
        val json = JsonObject()
        questions.value?.data?.forEach { q ->
            json.add("${q.questionId}", q.getAnswer())
        }
        json.printRequest()
        val data = json.toString().toEncrypt()
        data.printRequest()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).submitRiskAssessmentAws(App.INSTANCE.getUserId(), data), object : SingleCallback1<ApiResponse> {
            override fun onSingleSuccess(o: ApiResponse) {
                dismissProgress()
                if (o.status?.code == 1) {
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