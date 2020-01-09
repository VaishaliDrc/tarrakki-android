package org.supportcompact.ktx

import android.content.Context
import androidx.fragment.app.Fragment
import android.widget.Toast

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun androidx.fragment.app.Fragment.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    activity?.toast(message, duration)
}

fun androidx.fragment.app.Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    activity?.toast(message, duration)
}