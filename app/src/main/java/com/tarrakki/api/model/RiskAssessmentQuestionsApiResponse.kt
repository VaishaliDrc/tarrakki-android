package com.tarrakki.api.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName

data class RiskAssessmentQuestionsApiResponse(
        @SerializedName("data")
        val `data`: List<Data?>?,
        var page: Int = 0
) {
    data class Data(
            @SerializedName("action")
            val action: String?,
            @SerializedName("is_active")
            val isActive: Boolean?,
            @SerializedName("option")
            val option: List<Option>?,
            @SerializedName("question")
            val question: String?,
            @SerializedName("question_id")
            val questionId: Int?,
            @SerializedName("time")
            val time: String?
    ) {
        data class Option(
                @SerializedName("option_category")
                val optionCategory: String?,
                @SerializedName("option_id")
                val optionId: Int?,
                @SerializedName("option_image")
                val optionImage: String?,
                @SerializedName("option_type")
                val optionType: String?,
                @SerializedName("option_value")
                val optionValue: String?
        ) : BaseObservable() {

            @get:Bindable
            var isSelected: Boolean = false
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

            @get:Bindable
            var goalAmount: String = ""
                set(value) {
                    field = value
                    notifyPropertyChanged(BR.goalAmount)
                }

        }
    }
}