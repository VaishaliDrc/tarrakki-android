package org.supportcompact.ktx

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.DisplayMetrics

fun Context.color(@ColorRes clr: Int): Int {
    return ContextCompat.getColor(this, clr)
}

fun Context.string(@StringRes str: Int): String {
    return getString(str)
}

fun Context.drawable(@DrawableRes drw: Int): Drawable? {
    return ContextCompat.getDrawable(this, drw)
}

fun Context.font(@FontRes font: Int): Typeface? {
    return ResourcesCompat.getFont(this, font)
}

fun Context.stringArray(array: Int): Array<String> {
    return resources.getStringArray(array)
}

fun Context.intArray(array: Int): IntArray {
    return resources.getIntArray(array)
}

fun Fragment.getColor(@ColorRes clr: Int): Int? {
    return activity?.color(clr)
}

fun Fragment.string(@StringRes str: Int): String? {
    return activity?.string(str)
}

fun Fragment.drawable(@DrawableRes drw: Int): Drawable? {
    return activity?.drawable(drw)
}

fun Fragment.font(@FontRes font: Int): Typeface? {
    return activity?.font(font)
}

fun Fragment.stringArray(array: Int): Array<String>? {
    return activity?.stringArray(array)
}

fun Fragment.intArray(array: Int): IntArray? {
    return activity?.intArray(array)
}

fun Activity.screenWidth(): Int {
    val metrics: DisplayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

fun Activity.screenHeight(): Int {
    val metrics: DisplayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.heightPixels
}

//converts pixels to DP
fun Float.convertToDp(): Float {
    val metrics = Resources.getSystem().displayMetrics
    val dp = this / (metrics.densityDpi / 160f)
    return Math.round(dp).toFloat()
}

fun Float.convertToPx(): Float {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return Math.round(px).toFloat()
}