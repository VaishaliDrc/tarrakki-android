package com.tarrakki.api.model

import android.view.View
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
                val options: String,
                @SerializedName("question")
                val question: String,
                @SerializedName("question_type")
                val questionType: String,
                var ans1: String,
                var ans2: String
        )

        fun getInitQ1(): String {
            return if (introQuestions.isEmpty()) "" else introQuestions[0].question
        }

        fun getInitQ2(): String {
            return if (!introQuestions.isEmpty() && introQuestions.size == 2) introQuestions[1].question else ""
        }

        fun setAnsQ1(ans: String) {
            if (introQuestions.isNotEmpty()) {
                introQuestions[0].ans1 = ans
            }
        }

        fun getAnsQ1() = if (introQuestions.isNotEmpty()) introQuestions[0].ans1 else ""

        fun setAnsQ2(ans: String) {
            if (introQuestions.isNotEmpty() && introQuestions.size > 1) {
                introQuestions[1].ans1 = ans
            }
        }

        fun getAnsQ2() = if (introQuestions.isNotEmpty() && introQuestions.size > 1) introQuestions[1].ans1 else ""

        fun initQ1Visibility(): Int {
            return if (introQuestions.isNotEmpty() && introQuestions.size == 1 && introQuestions[0].questionType == "Select")
                View.VISIBLE
            else if (introQuestions.isNotEmpty() && introQuestions.size == 1)
                View.GONE
            else
                View.VISIBLE
        }

        fun initQ2Visibility(): Int {
            return if (introQuestions.isNotEmpty() && introQuestions.size == 1 && introQuestions[0].questionType == "Text")
                View.VISIBLE
            else if (introQuestions.isNotEmpty() && introQuestions.size == 1)
                View.GONE
            else
                View.VISIBLE
        }
    }
}