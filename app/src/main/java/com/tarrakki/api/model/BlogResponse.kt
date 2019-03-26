package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel

data class BlogResponse(
        @SerializedName("data")
        val `data`: Data?
) {
    data class Data(
            @SerializedName("blogs")
            val blogs: ArrayList<Blog>?,
            @SerializedName("limit")
            val limit: Int,
            @SerializedName("offset")
            var offset: Int,
            @SerializedName("total")
            var total: Int
    )
}

data class Blog(
        @SerializedName("active")
        val active: Boolean,
        @SerializedName("author")
        val author: String,
        @SerializedName("category")
        val category: Int,
        @SerializedName("created")
        val created: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String,
        @SerializedName("modified")
        val modified: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("date")
        val date: String
) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_article_list_item
    }
}