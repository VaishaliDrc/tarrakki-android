package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class SupportQueryListResponse(
        @SerializedName("data")
        val `data`: ArrayList<Data>?
) {
    data class Data(
            @SerializedName("description")
            val description: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("query_logo")
            val queryLogo: String?,
            @SerializedName("subquery")
            val subquery: List<Subquery?>?
    ) {
        data class Subquery(
                @SerializedName("description")
                val description: Any?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("name")
                val name: String?
        )
    }
}