package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class RiskAssessmentQuestionsApiResponse(
        @SerializedName("data")
        val `data`: List<Data?>?
) {
    data class Data(
            @SerializedName("action")
            val action: String?,
            @SerializedName("is_active")
            val isActive: Boolean?,
            @SerializedName("option")
            val option: List<Option?>?,
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
                val optionImage: Any?,
                @SerializedName("option_type")
                val optionType: String?,
                @SerializedName("option_value")
                val optionValue: String?
        )
    }
}