package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class RiskProfileResponse(
        val `data`: Data?
) {
    data class Data(
            val classification: Classification?,
            val observations: List<Observation>?,
            val report: List<Report>?,
            @SerializedName("user_profile_photo")
            val userProfilePhoto: String?, // /media/profiles/ProfileImage_x0cu10f.png
            @SerializedName("username")
            val userName: String?,
            @SerializedName("report_date")
            val reportDate: String?
    ) {
        data class Classification(
                @SerializedName("risk_profile")
                val riskProfile: String?, // Aggressive
                @SerializedName("risk_score")
                val riskScore: String? // 93.8
        )

        data class Observation(
                val observation: String?, // Awesome! You are a true blue investor who understands the importance of investing early and reap the benefits of compunding.
                @SerializedName("observation_id")
                val observationId: Int? // 1000894
        )

        data class Report(
                val options: List<Option>?,
                val question: String?, // You have Rs.25 lakh to invest at the start of the year. Which from the below 3 hypothetical return scenarios with likely best and worst-case annual returns do you prefer?
                @SerializedName("question_id")
                val questionId: Int?, // 1000030
                val score: Int?, // 5
                @SerializedName("weightage_perc")
                val weightagePerc: String?, // 12.5
                @SerializedName("weighted_score")
                val weightedScore: String? // 0.63
        ) {
            data class Option(
                    @SerializedName("option_category")
                    val optionCategory: String?,
                    @SerializedName("option_id")
                    val optionId: Int?, // 1000193
                    @SerializedName("option_image")
                    val optionImage: Any?, // null
                    @SerializedName("option_type")
                    val optionType: String?, // radio_returns
                    @SerializedName("option_value")
                    val optionValue: String? // Average Returns: 18%, Best Returns: 25%, Worst Returns: -15%
            )
        }
    }
}