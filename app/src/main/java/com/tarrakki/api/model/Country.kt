package com.tarrakki.api.model

import com.google.gson.annotations.SerializedName

data class Country(
        @SerializedName("Code")
        val code: String,
        @SerializedName("Description")
        val name: String
) {
    override fun toString(): String {
        return name
    }
}