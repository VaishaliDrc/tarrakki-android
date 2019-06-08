package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName

data class AppUpdateResponse(
        @SerializedName("data")
        val `data`: Data?
) {
    data class Data(
            @SerializedName("force_update")
            val forceUpdate: Boolean?,
            @SerializedName("message")
            val message: String?,
            @SerializedName("version")
            val version: String?
    )
}