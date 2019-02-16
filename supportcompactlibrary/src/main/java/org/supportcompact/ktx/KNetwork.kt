package org.supportcompact.ktx

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun Context.isNetworkConnected(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var netInfo: NetworkInfo? = null
    netInfo = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnected
}