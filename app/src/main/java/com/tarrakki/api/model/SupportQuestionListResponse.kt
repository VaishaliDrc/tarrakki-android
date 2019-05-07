package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class SupportQuestionListResponse(
        @SerializedName("data")
        val questions: ArrayList<Question>?
) {
    data class Question(
            @SerializedName("answer")
            val answer: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("is_my_question")
            val isMyQuestion: Boolean?,
            @SerializedName("question")
            val question: String?
    )
}