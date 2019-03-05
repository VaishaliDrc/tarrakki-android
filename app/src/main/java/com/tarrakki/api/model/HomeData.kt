package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.tarrakki.*
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.ktx.toReturnAsPercentage
import java.io.Serializable

data class HomeData(
        @SerializedName("data")
        val `data`: Data
) {
    data class Data(
            @SerializedName("category")
            val category: List<Category>,
            @SerializedName("goals")
            val goals: List<Goal>,
            @SerializedName("cart_count")
            val cartCount: Int?,
            @SerializedName("portfolio_details")
            val portfolioDetails: PortfolioDetails
    ) {
        data class PortfolioDetails(
                @SerializedName("current_value")
                val currentValue: Double,
                @SerializedName("total_investment")
                val totalInvestment: Double,
                @SerializedName("xirr")
                val xirr: String?
        ) {
            val totalReturn: Double
                get() = xirr?.toDoubleOrNull() ?: 0.0

            val totalReturnPercentage : Int
                get() = totalReturn.toInt()
        }

        data class Goal(
                @SerializedName("goal")
                val goal: String,
                @SerializedName("goal_image")
                val goalImage: String,
                @SerializedName("id")
                val id: Int
        ) : WidgetsViewModel {
            override fun layoutId(): Int {
                return R.layout.row_goal_home_list_item
            }
        }

        data class Category(
                @SerializedName("category_desctiption")
                val categoryDesctiption: String,
                @SerializedName("category_name")
                val categoryName: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("second_level_category")
                val secondLevelCategory: List<SecondLevelCategory>
        ) {
            data class SecondLevelCategory(
                    @SerializedName("short_descroption")
                    val categoryshortDesctiption: String?,
                    @SerializedName("category_desctiption")
                    val categoryDesctiption: String?,
                    @SerializedName("category_image")
                    val categoryImage: String,
                    @SerializedName("return_type")
                    val returnType: String?,
                    @SerializedName("risk_type")
                    val riskType: String?,
                    @SerializedName("category_name")
                    val categoryName: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("redirect_to")
                    val redirectTo: Int,
                    @SerializedName("is_goal")
                    val isGoal: Boolean,
                    @SerializedName("is_thematic")
                    val isThematic: Boolean,
                    @SerializedName("note_to_investor")
                    val noteToInvestor: String,
                    @SerializedName("third_level_category")
                    val thirdLevelCategory: List<ThirdLevelCategory>
            ) : WidgetsViewModel {
                override fun layoutId(): Int {
                    return R.layout.row_investment_list_item
                }

                var returnRiskDrawable: Int = R.drawable.ic_red_up
                    get() = getReturnLevelDrawable(returnType)

                var riskLevelDrawable: Int = R.drawable.ic_green_up
                    get() = getRiskLevelDrawable(riskType)

                var returnRiskVisible: Int = 0
                    get() = getReturnLevelVisibility(returnType)

                var riskLevelVisible: Int = 0
                    get() = getRiskLevelVisibility(riskType)

                var returnLevel: String = ""
                    get() = getReturnLevel(returnType)

                var riskLevel: String = ""
                    get() = getRiskLevel(riskType)

                var sectionName: String = ""

                data class ThirdLevelCategory(
                        @SerializedName("category_desctiption")
                        val categoryDesctiption: String?,
                        @SerializedName("category_image")
                        val categoryImage: String,
                        @SerializedName("category_name")
                        val categoryName: String,
                        @SerializedName("id")
                        val id: Int,
                        @SerializedName("short_descroption")
                        val shortDescroption: String?,
                        @SerializedName("return_type")
                        val returnType: String,
                        @SerializedName("risk_type")
                        val riskType: String
                ) : BaseObservable(), Serializable {
                    @get:Bindable
                    var hasNext: Boolean = true
                        set(value) {
                            field = value
                            notifyPropertyChanged(BR.hasNext)
                        }
                    @get:Bindable
                    var hasPrevious: Boolean = true
                        set(value) {
                            field = value
                            notifyPropertyChanged(BR.hasPrevious)
                        }

                    var returnRiskDrawable: Int = R.drawable.ic_red_up
                        get() = getReturnLevelDrawable(returnType)

                    var riskLevelDrawable: Int = R.drawable.ic_green_up
                        get() = getRiskLevelDrawable(riskType)

                    var returnRiskVisible: Int = 0
                        get() = getReturnLevelVisibility(returnType)

                    var riskLevelVisible: Int = 0
                        get() = getRiskLevelVisibility(riskType)

                    var returnLevel: String = ""
                        get() = getReturnLevel(returnType)

                    var riskLevel: String = ""
                        get() = getRiskLevel(riskType)

                }
            }
        }

        fun getGoals(): ArrayList<WidgetsViewModel> {
            val data = ArrayList<WidgetsViewModel>()
            data.addAll(goals)
            return data
        }

        fun <T : WidgetsViewModel> toWadgesArray(items: List<T>): ArrayList<WidgetsViewModel> {
            val data = ArrayList<WidgetsViewModel>()
            data.addAll(items)
            return data
        }
    }

}