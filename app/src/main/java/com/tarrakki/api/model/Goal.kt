package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.AES
import org.supportcompact.adapters.WidgetsViewModel

data class Goal(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("goal_data")
            val goalData: ArrayList<GoalData>,
            @SerializedName("inflation")
            val inflation: Int
    ) {
        data class GoalData(
                @SerializedName("goal_summary")
                val goalSummary: String,
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
                @SerializedName("order_sequence")
                val orderSequence: Int,
                @SerializedName("questions")
                val questions: List<Question>
        ) {
            data class Question(
                    @SerializedName("dependent_question")
                    val dependentQuestion: Any,
                    @SerializedName("max_value")
                    val maxValue: String,
                    @SerializedName("min_value")
                    val minValue: Int,
                    @SerializedName("parameter")
                    val parameter: String,
                    @SerializedName("question")
                    val question: String,
                    @SerializedName("question_order")
                    val questionOrder: Int,
                    @SerializedName("question_type")
                    val questionType: String

            ) : BaseObservable(), WidgetsViewModel {
                @get:Bindable
                var ans: String = ""
                    set(value) {
                        field = value
                        notifyPropertyChanged(BR.ans)
                    }
                @get:Bindable
                var ansBoolean: Boolean = true
                    set(value) {
                        field = value
                        notifyPropertyChanged(BR.ansBoolean)
                    }

                override fun layoutId(): Int {
                    return when ("$questionType") {
                        "float" -> when ("$parameter") {
                            "cv", "pv" -> R.layout.question_amount
                            "n" -> R.layout.question_years
                            "dp" -> R.layout.question_percetage
                            else -> R.layout.question_amount
                        }
                        else -> R.layout.question_boolean
                    }
                }
            }

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

            fun getCVAmount(): String? {
                return if (questions.isEmpty()) "" else questions.firstOrNull { q -> q.parameter == "cv" }?.ans
            }

            fun getNDuration(): String? {
                return if (questions.isEmpty()) "" else questions.firstOrNull { q -> q.parameter == "n" }?.ans
            }

            fun getPMTJSON(): String {
                val json = JsonObject()
                questions.forEach { q ->
                    if ("${q.questionType}" != "boolean") {
                        json.addProperty("${q.parameter}", "${q.ans}".replace(",", ""))
                    }
                }
                json.addProperty("goal_id", this@GoalData.id)
                return AES.encrypt(json.toString())
            }

            fun goalSummary() = "$goalSummary".split(" ") as ArrayList<String>
        }
    }
}
/*
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
}*/
