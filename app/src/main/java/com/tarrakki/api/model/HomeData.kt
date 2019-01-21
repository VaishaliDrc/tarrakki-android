package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel

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
                    val categoryDesctiption: String,
                    @SerializedName("category_image")
                    val categoryImage: String,
                    @SerializedName("category_name")
                    val categoryName: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("third_level_category")
                    val thirdLevelCategory: List<ThirdLevelCategory>
            ) : WidgetsViewModel {
                override fun layoutId(): Int {
                    return R.layout.row_investment_list_item
                }

                data class ThirdLevelCategory(
                        @SerializedName("category_desctiption")
                        val categoryDesctiption: String,
                        @SerializedName("category_image")
                        val categoryImage: String,
                        @SerializedName("category_name")
                        val categoryName: String,
                        @SerializedName("id")
                        val id: Int
                )
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