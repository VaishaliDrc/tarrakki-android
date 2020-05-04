package com.tarrakki.api.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.util.*

data class RiskAssessmentQuestionsApiResponse(
        @SerializedName("data")
        val `data`: List<Data>?,
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
                var optionValue: String?
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

        fun getAnswer(): JsonObject {
            val json = JsonObject()
            val type = option?.firstOrNull()?.optionType?.toLowerCase(Locale.US)
            when (type) {
                "slider" -> {
                    option?.filter { it.isSelected }?.forEach { op ->
                        json.addProperty("options", op.optionId)
                    }
                }
                "checkbox" -> {
                    option?.filter { it.isSelected }?.forEach { op ->
                        json.addProperty("options", op.optionId)
                    }
                }
                "radio" -> {
                    option?.filter { it.isSelected }?.forEach { op ->
                        json.addProperty("options", op.optionId)
                    }
                }
                "radio_emoji" -> {
                    option?.filter { it.isSelected }?.forEach { op ->
                        json.addProperty("options", op.optionId)
                    }
                }
                "checkbox_goal" -> {
                    option?.filter { it.isSelected }?.forEach { op ->
                        json.addProperty("goals", op.optionId)
                        json.addProperty("amount", op.goalAmount)
                    }
                }
                "radio_returns" -> {
                    option?.filter { it.isSelected }?.forEach { op ->
                        json.addProperty("options", op.optionId)
                    }
                }
            }
            return json
        }
    }
}