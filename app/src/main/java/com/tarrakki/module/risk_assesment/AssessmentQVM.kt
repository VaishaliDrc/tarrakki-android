package com.tarrakki.module.risk_assesment

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.tarrakki.api.model.RiskAssessmentQuestionsApiResponse
import org.supportcompact.FragmentViewModel

class AssessmentQVM : FragmentViewModel() {

    var questions = MutableLiveData<RiskAssessmentQuestionsApiResponse>()
    var questionNo = ObservableField("")
    var questionTotal = ObservableField("")
    var question = ObservableField("")

}

data class OptionsItem(val category: String?, val options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>?)