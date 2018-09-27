package org.supportcompact.ktx

import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    activity?.toast(message, duration)
}

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    activity?.toast(message, duration)
}