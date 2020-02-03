package org.supportcompact.ktx

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

fun Context.isNetworkConnected(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var netInfo: NetworkInfo? = null
    netInfo = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnected
}


fun File.toMultipartBody(param: String): MultipartBody.Part {
    val requestFile = RequestBody.create(MediaType.parse("image/*"), this)
    return MultipartBody.Part.createFormData(param, name, requestFile)
}
