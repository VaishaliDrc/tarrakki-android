package org.supportcompact.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat.startActivity


inline fun <reified T : Activity> Activity.startActivity() = startActivity(Intent(this, T::class.java))

inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int) = startActivityForResult(Intent(this, T::class.java), requestCode)

inline fun <reified T : Activity> androidx.fragment.app.Fragment.startActivity() = startActivity(Intent(activity, T::class.java))

fun Context.openPlayStore() {
    val appPackageName = packageName // getPackageName() from Context or Activity object
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
    } catch (anfe: android.content.ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
    }
}

fun Context.openPlayStore(appPackageName: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
    } catch (anfe: android.content.ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
    }
}