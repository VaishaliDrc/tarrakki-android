package com.tarrakki.api.model


import android.text.TextUtils
import android.view.View
import com.google.gson.annotations.SerializedName

data class SupportQueryListResponse(
        @SerializedName("data")
        val `data`: ArrayList<Data>?
) {
    data class Data(
            @SerializedName("description")
            val description: String?,
            @SerializedName("id")
            val id: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("query_logo")
            val queryLogo: String?,
            @SerializedName("subquery")
            val subquery: ArrayList<Subquery>?
    ) {
        var subqueryName: String? = ""
        var subqueryId: String? = ""
        val descriptionVisibility: Int
            get() = if (TextUtils.isEmpty(description)) View.GONE else View.VISIBLE

        data class Subquery(
                @SerializedName("description")
                val description: Any?,
                @SerializedName("id")
                val id: String?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("is_my_question")
                val isMyQuestion: Boolean?
        )
    }
}