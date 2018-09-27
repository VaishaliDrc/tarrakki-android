package org.supportcompact.ktx

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by jayeshparkariya on 8/3/18.
 */

interface PermissionCallBack {
    fun permissionGranted()
    fun permissionDenied()
    /**
     * Callback on permission "Never show again" checked and denied
     * */
    fun onPermissionDisabled()
}

fun Activity.checkPermissionRationale(permissions: Array<out String>): Boolean {
    var result = true
    for (permission in permissions) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            result = false
            break
        }
    }
    return result
}

fun Context.checkSelfPermissions(permissions: ArrayList<String>): Boolean {
    return permissions.none { ContextCompat.checkSelfPermission(this.applicationContext, it) != PackageManager.PERMISSION_GRANTED }
}

fun Activity.requestAllPermissions(permissions: ArrayList<out String>, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissions.toTypedArray(), requestCode)
}

