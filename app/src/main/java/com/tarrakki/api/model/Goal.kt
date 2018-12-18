package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class Goal(
        @SerializedName("data")
        val `data`: ArrayList<Data>
) {
    data class Data(
            @SerializedName("description")
            val description: String,
            @SerializedName("goal")
            val goal: String,
            @SerializedName("goal_image")
            val goalImage: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("intro_questions")
            val introQuestions: List<IntroQuestion>,
            @SerializedName("questions")
            val questions: List<Question>
    ) {
        data class Question(
                @SerializedName("dependent_question")
                val dependentQuestion: Int,
                @SerializedName("max_value")
                val maxValue: Any,
                @SerializedName("min_value")
                val minValue: Any,
                @SerializedName("parameter")
                val parameter: Any,
                @SerializedName("question")
                val question: String,
                @SerializedName("question_order")
                val questionOrder: Int,
                @SerializedName("question_type")
                val questionType: String
        )

        data class IntroQuestion(
                @SerializedName("options")
                val options: Any,
                @SerializedName("question")
                val question: String,
                @SerializedName("question_type")
                val questionType: String
        )
    }
}