package com.tarrakki.api.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.tarrakki.api.AES
import org.json.JSONArray
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

fun String.toEncrypt() = AES.encrypt(this)

fun String.printRequest() = e("Request Data=>${this}")

fun JsonObject.printRequest() = e("Request Data=>${this}")

fun JSONArray.printRequest() = e("Request Data=>${this}")


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

inline fun <reified T> String.parseArray(): T? {
    return try {
        val data = JSONArray(this)
        e("Response Array=>$data")
        val listType = object : TypeToken<T>() {}.type
        Gson().fromJson(data.toString(), listType)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun ApiResponse.printResponse() {
    e("Response=>", "${data?.toDecrypt()}")
}