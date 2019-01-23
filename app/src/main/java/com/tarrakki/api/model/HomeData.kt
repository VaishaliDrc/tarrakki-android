package com.tarrakki.api.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel
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
            val cartCount: Int?
    ) {
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
                    @SerializedName("category_desctiption")
                    val categoryDesctiption: String?,
                    @SerializedName("category_image")
                    val categoryImage: String,
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
                        val shortDescroption: String?
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

                    var returnRisk: String = ""
                        get() =
                            when {
                                categoryName.equals("Aggressive", false) -> "High Return"
                                categoryName.equals("Moderate", false) -> "Medium Return"
                                categoryName.equals("Conservative", false) -> "Low Return"
                                else -> ""
                            }

                    var riskLevel: String = ""
                        get() =
                            when {
                                categoryName.equals("Aggressive", false) -> "High Risk"
                                categoryName.equals("Moderate", false) -> "Medium Risk"
                                categoryName.equals("Conservative", false) -> "Low Risk"
                                else -> ""
                            }

                    @DrawableRes var returnRiskDrawable: Int = R.drawable.ic_green_up
                        get() =
                            when {
                                categoryName.equals("Aggressive", false) -> R.drawable.ic_green_up
                                categoryName.equals("Moderate", false) -> R.drawable.ic_green_equal
                                categoryName.equals("Conservative", false) -> R.drawable.ic_green_down
                                else -> R.drawable.ic_green_up
                            }

                    @DrawableRes var riskLevelDrawable: Int = R.drawable.ic_red_up
                        get() =
                            when {
                                categoryName.equals("Aggressive", false) -> R.drawable.ic_red_up
                                categoryName.equals("Moderate", false) -> R.drawable.ic_red_equal
                                categoryName.equals("Conservative", false) -> R.drawable.ic_red_down
                                else -> R.drawable.ic_red_up
                            }
                    var returnRiskVisible: Int = View.GONE
                        get() =
                            when {
                                categoryName.equals("Aggressive", false) -> View.VISIBLE
                                categoryName.equals("Moderate", false) -> View.VISIBLE
                                categoryName.equals("Conservative", false) -> View.VISIBLE
                                else -> View.GONE
                            }

                    var riskLevelVisible: Int = View.GONE
                        get() =
                            when {
                                categoryName.equals("Aggressive", false) -> View.VISIBLE
                                categoryName.equals("Moderate", false) -> View.VISIBLE
                                categoryName.equals("Conservative", false) -> View.VISIBLE
                                else -> View.GONE
                            }

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