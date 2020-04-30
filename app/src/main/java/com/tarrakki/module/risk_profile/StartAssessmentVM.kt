package com.tarrakki.module.risk_profile

import androidx.lifecycle.MutableLiveData
import com.tarrakki.App
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.RiskAssessmentQuestionsApiResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.subscribeToSingle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress

class StartAssessmentVM : FragmentViewModel() {

    val apiQuestionsResponse = MutableLiveData<RiskAssessmentQuestionsApiResponse>()

    fun getRiskAssessmentQuestions(): MutableLiveData<RiskAssessmentQuestionsApiResponse> {
        showProgress()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getRiskAssessmentQuestions(App.INSTANCE.getUserId()), object : SingleCallback1<ApiResponse> {
            override fun onSingleSuccess(o: ApiResponse) {
                dismissProgress()
                if (o.status?.code == 0) {
                    GlobalScope.launch {
                        coroutineScope {
                            val data = o.data?.parseTo<RiskAssessmentQuestionsApiResponse>()
                            apiQuestionsResponse.postValue(data)
                        }
                    }
                } else {
                    postError("${o.status?.message}")
                }
            }

            override fun onFailure(throwable: Throwable) {
                dismissProgress()
                throwable.postError()
            }
        })
        return apiQuestionsResponse
    }

}