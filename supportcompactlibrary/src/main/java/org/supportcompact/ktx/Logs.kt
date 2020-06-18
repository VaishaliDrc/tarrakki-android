package org.supportcompact.ktx

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

const val BUILD_TYPE_DEBUG = false

fun Any.e(e: Any) = if (BUILD_TYPE_DEBUG) Log.e(this.javaClass.name, "$e") else null

fun Any.e(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.e(tag, "$e") else null

fun Any.i(e: Any) = if (BUILD_TYPE_DEBUG) Log.i(this.javaClass.name, "$e") else null

fun Any.i(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.i(tag, "$e") else null

fun Any.d(e: Any) = if (BUILD_TYPE_DEBUG) Log.d(this.javaClass.name, "$e") else null

fun Any.d(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.d(tag, "$e") else null

fun Any.v(e: Any) = if (BUILD_TYPE_DEBUG) Log.v(this.javaClass.name, "$e") else null

fun Any.v(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.v(tag, "$e") else null

fun Any.w(e: Any) = if (BUILD_TYPE_DEBUG) Log.w(this.javaClass.name, "$e") else null

fun Any.w(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.w(tag, "$e") else null

fun Context.printHasKey() {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = packageInfo.signingInfo.apkContentsSigners
            val md = MessageDigest.getInstance("SHA")
            for (signature in signatures) {
                md.update(signature.toByteArray())
                val signatureBase64 = String(Base64.encode(md.digest(), Base64.DEFAULT))
                Log.d("Signature Base64", signatureBase64)
            }
        } else {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}