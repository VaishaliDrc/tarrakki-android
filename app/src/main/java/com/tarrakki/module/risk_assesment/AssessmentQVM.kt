package com.tarrakki.module.risk_assesment

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import com.tarrakki.api.model.RiskAssessmentQuestionsApiResponse
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class AssessmentQVM : FragmentViewModel() {

    val sliderQuestions = arrayListOf<SliderItem>()
    var questions = MutableLiveData<RiskAssessmentQuestionsApiResponse>()

    var questionNo =  ObservableField("")
    var question = ObservableField("")

    init {
        sliderQuestions.add(SliderItem("Below 5000"))
        sliderQuestions.add(SliderItem("5000 - 15000"))
        sliderQuestions.add(SliderItem("15000 - 30000"))
        sliderQuestions.add(SliderItem("30000 - 60000"))
        sliderQuestions.add(SliderItem("Above 60000"))
    }
}

data class CheckBoxItem(val image : String,val label: String, private val selected: Boolean = false) : BaseObservable() {

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



data class OptionsItem(val category: String?, val options : ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>?)