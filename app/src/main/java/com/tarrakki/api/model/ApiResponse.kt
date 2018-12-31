package com.tarrakki.api.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.tarrakki.api.AES
import org.json.JSONObject
import org.supportcompact.ktx.e

data class ApiResponse(
        @SerializedName("data")
        val `data`: String?,
        @SerializedName("status")
        val status: Status?
) {
    data class Status(
            @SerializedName("code")
            val code: Int,
            @SerializedName("message")
            val message: String
    )
}

fun String.toDecrypt() = AES.decrypt(this)

inline fun <reified T> String.parseTo(): T? {
    return try {
        val data = JSONObject(this.toDecrypt())
        e("Response=>$data")
        Gson().fromJson(data.toString(), T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}