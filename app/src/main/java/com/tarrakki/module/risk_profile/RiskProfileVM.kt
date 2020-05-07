package com.tarrakki.module.risk_profile

import androidx.lifecycle.MutableLiveData
import com.tarrakki.R
import com.tarrakki.api.model.ApiResponse
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class RiskProfileVM : FragmentViewModel() {

    val data = arrayListOf<WidgetsViewModel>()
    val riskProfile = MutableLiveData<ApiResponse>()

    /*fun getReportOfRiskProfile(): MutableLiveData<RiskProfileResponse> {
        val apiResponse = MutableLiveData<RiskProfileResponse>()
        showProgress()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getReportOfRiskProfile(App.INSTANCE.getUserId()), object : SingleCallback1<ApiResponse> {
            override fun onSingleSuccess(o: ApiResponse) {
                if (o.status?.code == 1) {
                    GlobalScope.launch {
                        withContext(Dispatchers.Default) {
                            //o.printResponse()
                            val res = o.data?.parseTo<RiskProfileResponse>()
                            data.clear()
                            res?.data?.let { report ->
                                data.add(RiskProfile(
                                        report.userName ?: "",
                                        "as on ".plus(report.reportDate?.toDate("MM/dd/yyyy")?.convertTo()),
                                        report.userProfilePhoto ?: ""
                                ))
                                var observation = ""
                                report.observations?.forEach {
                                    observation += (it.observation ?: "").plus("\n\n")
                                }
                                data.add(RiskObservation(observation))
                                data.add(RiskSpeedometer(report.classification?.riskScore?.toFloatOrNull()
                                        ?: 0f))
                                data.add(SingleButton(R.string.retake_risk_assessment))
                            }
                            apiResponse.postValue(res)
                        }
                    }
                } else {
                    postError("${o.status?.message}")
                }
                dismissProgress()
            }

            override fun onFailure(throwable: Throwable) {
                dismissProgress()
                throwable.postError()
            }
        })
        return apiResponse
    }*/

}

data class RiskProfile(val name: String, val date: String, val imgUrl: String) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_risk_profile
    }
}

data class RiskObservation(val observation: String) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_risk_observation
    }
}

data class RiskSpeedometer(val value: Float) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_speedometer_risk_profile
    }
}