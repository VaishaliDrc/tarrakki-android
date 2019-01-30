package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.text.TextUtils
import android.view.View
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.module.yourgoal.GoalSummary
import com.tarrakki.toYearWord
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.ktx.e

data class Goal(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("goal_data")
            val goalData: ArrayList<GoalData>,
            @SerializedName("inflation")
            val inflation: Double
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
                var questions: List<Question>
        ) {
            @SerializedName("year_summary")
            var yearSummary: String? = null
            @SerializedName("lumpsum_summary")
            var lumpsumSummary: String? = null
            @SerializedName("future_value_summary")
            var futureValueSummary: String? = null
            @SerializedName("recommendation_summary")
            var recommendationSummary: String? = null
            @SerializedName("no_inflation")
            var noInflation: Boolean = false

            var inflation: Double? = null
            var pmt: Double? = null
            var futureValue: Double? = null
            var customPMT: Double? = null

            data class Question(
                    @SerializedName("dependent_question")
                    val dependentQuestion: Any,
                    @SerializedName("max_value")
                    val maxValue: String,
                    @SerializedName("min_value")
                    val minValue: Double,
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
                            "cv", "tax_pmt", "pv" -> R.layout.question_amount
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

            fun getInitHint(): String? {
                return introQuestions.firstOrNull { q -> q.questionType == "Text" }?.options
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

            fun getCV(): Question? {
                return if (questions.isEmpty()) null else questions.firstOrNull { q -> q.parameter == "cv" }
            }

            fun getPMT(): Question? {
                return if (questions.isEmpty()) null else questions.firstOrNull { q -> q.parameter == "tax_pmt" }
            }

            fun setPMT(ans: String) {
                questions.firstOrNull { q -> q.parameter == "tax_pmt" }?.ans = ans
            }

            fun setCVAmount(ans: String) {
                questions.firstOrNull { q -> q.parameter == "cv" }?.ans = ans
            }

            fun getPVAmount(): String? {
                return if (questions.isEmpty()) "" else questions.firstOrNull { q -> q.parameter == "pv" }?.ans
            }

            fun setPVAmount(ans: String) {
                questions.firstOrNull { q -> q.parameter == "pv" }?.ans = ans
            }

            fun getDPAmount(): String? {
                return if (questions.isEmpty()) "" else questions.firstOrNull { q -> q.parameter == "dp" }?.ans
            }

            fun setDPAmount(ans: String) {
                questions.firstOrNull { q -> q.parameter == "dp" }?.ans = ans
            }

            fun getInvestmentAmount(): String? {
                return if (questions.isEmpty()) "" else questions.firstOrNull { q ->
                    when ("${q.parameter}") {
                        "cv", "tax_pmt" -> true
                        else -> false
                    }
                }?.ans
            }

            fun setInvestmentAmount(ans: String) {
                questions.firstOrNull { q ->
                    when ("${q.parameter}") {
                        "cv", "tax_pmt" -> true
                        else -> false
                    }
                }?.ans = ans
            }

            fun getNDuration(): String? {
                return if (questions.isEmpty()) "" else questions.firstOrNull { q -> q.parameter == "n" }?.ans
            }

            fun getNDurationInWord(): String? {
                return if (questions.isEmpty()) "" else questions.firstOrNull { q -> q.parameter == "n" }?.ans?.toYearWord()
            }

            fun setNDuration(ans: String) {
                questions.firstOrNull { q -> q.parameter == "n" }?.ans = ans
            }

            fun getN(): Question? {
                return if (questions.isEmpty()) null else questions.firstOrNull { q -> q.parameter == "n" }
            }

            fun inflationVisibility() = if (noInflation) View.GONE else View.VISIBLE

            fun isCustomInvestment(): Boolean {
                return questions.firstOrNull { q -> q.parameter == "tax_pmt" } == null
            }

            fun getPMTJSON(): String {
                val json = JsonObject()
                val dataList = questions.chunked(2)
                dataList.forEach { questions ->
                    var isBoolean: Boolean
                    when (questions.size) {
                        2 -> {
                            val item1 = questions[0]
                            val item2 = questions[1]
                            isBoolean = item1.questionType == "boolean"
                            if (isBoolean && item1.ansBoolean) {
                                if (!TextUtils.isEmpty(item2.ans))
                                json.addProperty("${item2.parameter}", "${item2.ans}".replace(",", ""))
                            } else if (!isBoolean) {
                                questions.forEach { q ->
                                    if (!TextUtils.isEmpty(q.ans))
                                        json.addProperty("${q.parameter}", "${q.ans}".replace(",", ""))
                                }
                            }
                        }
                        else -> {
                            val item1 = questions[0]
                            isBoolean = item1.questionType == "boolean"
                            if (!isBoolean) {
                                questions.forEach { q ->
                                    if (!TextUtils.isEmpty(q.ans))
                                        json.addProperty("${q.parameter}", "${q.ans}".replace(",", ""))
                                }
                            }
                        }
                    }
                }
                if (inflation != null) {
                    json.addProperty("i", inflation)
                }
                if (customPMT != null) {
                    json.addProperty("custom_pmt", customPMT)
                }
                json.addProperty("goal_id", this@GoalData.id)
                e("request->", json)
                return AES.encrypt(json.toString())
            }

            fun addGoalData(): String {
                val json = JsonObject()
                val dataList = questions.chunked(2)
                dataList.forEach { questions ->
                    var isBoolean: Boolean
                    when (questions.size) {
                        2 -> {
                            val item1 = questions[0]
                            val item2 = questions[1]
                            isBoolean = item1.questionType == "boolean"
                            if (isBoolean && item1.ansBoolean) {
                                json.addProperty("${item2.parameter}", "${item2.ans}".replace(",", ""))
                            } else if (!isBoolean) {
                                questions.forEach { q ->
                                    json.addProperty("${q.parameter}", "${q.ans}".replace(",", ""))
                                }
                            }
                        }
                        else -> {
                            val item1 = questions[0]
                            isBoolean = item1.questionType == "boolean"
                            if (!isBoolean) {
                                questions.forEach { q ->
                                    json.addProperty("${q.parameter}", "${q.ans}".replace(",", ""))
                                }
                            }
                        }
                    }
                }
                if (inflation != null) {
                    json.addProperty("i", inflation)
                }
                json.addProperty("goal_id", this@GoalData.id)
                json.addProperty("pmt", this@GoalData.pmt)
                json.addProperty("i", this@GoalData.inflation)
                if (introQuestions.isNotEmpty() && introQuestions.size > 1) {
                    json.addProperty("saving_for", getAnsQ1())
                    json.addProperty("user_purchase", getAnsQ2())
                } else if (introQuestions.isNotEmpty()) {
                    json.addProperty("saving_for", getAnsQ1())
                }
                e("addGoalData->", json)
                e("addGoalData->", AES.encrypt(json.toString()))
                return AES.encrypt(json.toString())
            }

            fun goalSummary(): ArrayList<WidgetsViewModel> {
                val rawData = "$goalSummary".split(" ")
                val data = ArrayList<WidgetsViewModel>()
                rawData.forEach { item ->
                    when (item) {
                        "#cv", "#cv.", "#fv", "#fv.", "#pv", "#pv.", "#pmt", "#pmt." -> {
                            data.add(GoalSummary(item, R.layout.summary_txt_currency))
                        }
                        "\$n", "\$n.", "\$fv", "\$fv." -> {
                            data.add(GoalSummary(item, R.layout.summary_label_bold))
                        }
                        "#i", "#dp" -> {
                            data.add(GoalSummary(item, R.layout.summary_txt_percetage))
                        }
                        "#n" -> {
                            data.add(GoalSummary(item, R.layout.summary_txt))
                        }
                        "years", "years." -> {
                            if (getNDuration() != null)
                                data.add(GoalSummary(getNDuration()?.toYearWord().plus(".")))
                            else
                                data.add(GoalSummary(item))
                        }
                        else -> {
                            data.add(GoalSummary(item))
                        }
                    }
                }
                return data
            }
        }
    }
}