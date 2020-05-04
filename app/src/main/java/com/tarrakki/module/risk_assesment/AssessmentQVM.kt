package com.tarrakki.module.risk_assesment

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.library.baseAdapters.BR
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

class AssessmentQVM : FragmentViewModel() {

    val sliderQuestions = arrayListOf<SliderItem>()
    var questions = MutableLiveData<RiskAssessmentQuestionsApiResponse>()

    var questionNo = ObservableField("")
    var questionTotal = ObservableField("")
    var question = ObservableField("")

    init {
        sliderQuestions.add(SliderItem("Below 5000"))
        sliderQuestions.add(SliderItem("5000 - 15000"))
        sliderQuestions.add(SliderItem("15000 - 30000"))
        sliderQuestions.add(SliderItem("30000 - 60000"))
        sliderQuestions.add(SliderItem("Above 60000"))
    }

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

data class CheckBoxItem(val image: String, val label: String, private val selected: Boolean = false) : BaseObservable() {

    @get:Bindable
    var isSelected: Boolean = selected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}

data class SliderItem(val label: String, private val selected: Boolean = false) : BaseObservable() {

    @get:Bindable
    var isSelected: Boolean = selected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    @get:Bindable
    var isMovedOver: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.movedOver)
        }
}


data class OptionsItem(val category: String?, val options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>?)