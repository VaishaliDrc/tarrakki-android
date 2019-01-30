package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class GoalSavedResponse(
        @SerializedName("data")
        val `data`: List<Data>
) {
    data class Data(
            @SerializedName("cv")
            val cv: String,
            @SerializedName("dp")
            val dp: String,
            @SerializedName("fv")
            val fv: String,
            @SerializedName("goal_details")
            val goalDetails: Goal.Data.GoalData,
            @SerializedName("inflation")
            val inflation: String,
            @SerializedName("is_loan")
            val isLoan: Boolean,
            @SerializedName("n")
            val n: String,
            @SerializedName("pmt")
            val pmt: String,
            @SerializedName("pv")
            val pv: String,
            @SerializedName("user_goal_id")
            val userGoalId: Int
    ){
            fun getGoal() : Goal.Data.GoalData{
                    goalDetails.setCVAmount(cv)
                    goalDetails.setDPAmount(dp)
                    goalDetails.futureValue = fv.toDoubleOrNull()
                    goalDetails.inflation = inflation.toDoubleOrNull()
                    goalDetails.setNDuration(n)
                    goalDetails.setPMT(pmt)
                    goalDetails.setPVAmount(pv)
                    return goalDetails
            }
    }
}