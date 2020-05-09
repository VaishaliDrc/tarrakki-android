package com.tarrakki.api.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.supportcompact.ktx.toBigIntDefaultZero
import java.util.*

data class RiskAssessmentQuestionsApiResponse(
        @SerializedName("data")
        val `data`: List<Data>?,
        var page: Int = 0,
        var isReassessment: Boolean = false
) {
    data class Data(
            @SerializedName("action")
            val action: String?,
            @SerializedName("is_active")
            val isActive: Boolean?,
            @SerializedName("option", alternate = ["options"])
            val option: List<Option>?,
            @SerializedName("question")
            val question: String?,
            @SerializedName("question_id")
            val questionId: Int?,
            @SerializedName("time")
            val time: String?
    ) : BaseObservable() {
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

            @get:Bindable
            var targetYear: String = ""
                set(value) {
                    field = value
                    notifyPropertyChanged(BR.targetYear)
                }
        }

        @get:Bindable
        var totalValue: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.totalValue)
            }

        fun getAnswer(): JsonObject {
            val json = JsonObject()
            when (option?.firstOrNull()?.optionType?.toLowerCase(Locale.US)) {
                "slider" -> {
                    var answers = ""
                    option.filter { it.isSelected }.forEachIndexed { index, option ->
                        if (index == 0) {
                            answers += option.optionId ?: ""
                        } else {
                            answers += "," + option.optionId
                        }
                    }
                    json.addProperty("options", answers)
                }
                "checkbox" -> {
                    var answers = ""
                    option.filter { it.isSelected }.forEachIndexed { index, option ->
                        if (index == 0) {
                            answers += option.optionId ?: ""
                        } else {
                            answers += "," + option.optionId
                        }
                    }
                    json.addProperty("options", answers)
                    json.addProperty("amount", toBigIntDefaultZero(totalValue))
                }
                "radio" -> {
                    option.filter { it.isSelected }.forEach { op ->
                        json.addProperty("options", op.optionId.toString())
                    }
                }
                "radio_emoji" -> {
                    option.filter { it.isSelected }.forEach { op ->
                        json.addProperty("options", op.optionId.toString())
                    }
                }
                "checkbox_goal" -> {
                    var goals = ""
                    var options = ""
                    var amount = ""
                    var targetYear = ""
                    val cal = Calendar.getInstance()
                    option.filter { it.isSelected }.forEachIndexed { index, option ->
                        if (index == 0) {
                            goals += option.optionId ?: ""
                            amount += toBigIntDefaultZero(option.goalAmount)
                            targetYear += option.targetYear
                            options += "" + ((option.targetYear.toIntOrNull()
                                    ?: 0) - cal.get(Calendar.YEAR))
                        } else {
                            goals += "," + option.optionId
                            amount += "," + toBigIntDefaultZero(option.goalAmount)
                            targetYear += "," + option.targetYear
                            options += "," + ((option.targetYear.toIntOrNull()
                                    ?: 0) - cal.get(Calendar.YEAR))
                        }
                    }
                    json.addProperty("amount", amount)
                    json.addProperty("options", options)
                    json.addProperty("target_year", targetYear)
                    json.addProperty("goals", goals)
                }
                "radio_returns" -> {
                    option.filter { it.isSelected }.forEach { op ->
                        json.addProperty("options", op.optionId.toString())
                    }
                }
            }
            return json
        }
    }
}