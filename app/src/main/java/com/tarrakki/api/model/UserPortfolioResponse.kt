package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger
import kotlin.text.StringBuilder

data class UserPortfolioResponse(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("direct_investment")
            val directInvestment: List<DirectInvestment>,
            @SerializedName("goal_based_investment")
            val goalBasedInvestment: List<GoalBasedInvestment>
    ) {
        data class GoalBasedInvestment(
                @SerializedName("current_value")
                val currentValue: BigInteger,
                @SerializedName("funds")
                val funds: List<Fund>,
                @SerializedName("goal_id")
                val goalId: Int,
                @SerializedName("goal_name")
                val goalName: String,
                @SerializedName("total_investment")
                val totalInvestment: BigInteger,
                @SerializedName("xirr")
                val xirr: BigInteger
        ) {
            data class Fund(
                    @SerializedName("current_value")
                    val currentValue: BigInteger,
                    @SerializedName("folio_list")
                    val folioList: List<Folio>,
                    @SerializedName("fund_id")
                    val fundId: Int,
                    @SerializedName("fund_name")
                    val fundName: String,
                    @SerializedName("total_investment")
                    val totalInvestment: BigInteger,
                    @SerializedName("xirr")
                    val xirr: BigInteger
            ) {
                data class Folio(
                        @SerializedName("amount")
                        val amount: BigInteger,
                        @SerializedName("folio_no")
                        val folioNo: String
                )

                var folioNoList : String = ""
                    get() = if (folioList.isNotEmpty()){
                        val string = StringBuilder()
                        folioList.forEachIndexed { index, folio ->
                            string.append(folio.folioNo)
                            if (index!=folioList.size-1){
                                string.append(", ")
                            }
                        }
                        string.toString()
                    }else{
                        ""
                    }
            }
        }

        data class DirectInvestment(
                @SerializedName("current_value")
                val currentValue: BigInteger,
                @SerializedName("folio_list")
                val folioList: List<Folio>,
                @SerializedName("fund_id")
                val fundId: Int,
                @SerializedName("fund_name")
                val fundName: String,
                @SerializedName("total_investment")
                val totalInvestment: BigInteger,
                @SerializedName("xirr")
                val xirr: BigInteger
        ) {
            data class Folio(
                    @SerializedName("amount")
                    val amount: BigInteger,
                    @SerializedName("folio_no")
                    val folioNo: String
            )

            var folioNoList : String = ""
               get() = if (folioList.isNotEmpty()){
                   val string = StringBuilder()
                   folioList.forEachIndexed { index, folio ->
                       string.append(folio.folioNo)
                       if (index!=folioList.size-1){
                           string.append(", ")
                       }
                   }
                   string.toString()
               }else{
                   ""
               }
        }
    }
}

data class FolioData(
        val amount: BigInteger,
        val folioNo: String
)