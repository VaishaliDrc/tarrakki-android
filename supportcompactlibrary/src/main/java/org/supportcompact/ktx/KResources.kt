package org.supportcompact.ktx

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import androidx.databinding.ObservableField
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import java.util.regex.Pattern

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

fun androidx.fragment.app.Fragment.getColor(@ColorRes clr: Int): Int? {
    return activity?.color(clr)
}

fun androidx.fragment.app.Fragment.string(@StringRes str: Int): String? {
    return activity?.string(str)
}

fun androidx.fragment.app.Fragment.drawable(@DrawableRes drw: Int): Drawable? {
    return activity?.drawable(drw)
}

fun androidx.fragment.app.Fragment.font(@FontRes font: Int): Typeface? {
    return activity?.font(font)
}

fun androidx.fragment.app.Fragment.stringArray(array: Int): Array<String>? {
    return activity?.stringArray(array)
}

fun androidx.fragment.app.Fragment.intArray(array: Int): IntArray? {
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

fun ObservableField<String>.isEmpty() = TextUtils.isEmpty(get())

fun ObservableField<String>.isEmail() = Patterns.EMAIL_ADDRESS.matcher(get()).matches()

fun ObservableField<String>.isPAN() = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]").matcher(get()).matches()

fun ObservableField<String>.isValidPassword() = Pattern.compile("^(?=.*?[A-Za-z])(?=.*?[0-9]).{6,20}").matcher(get()).matches()

fun ObservableField<String>.isValidMobile() = Pattern.compile("^[6-9]\\d{9}$").matcher(get()).matches()


fun String.isEmpty() = TextUtils.isEmpty(this)

fun String.isEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isPAN() = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]").matcher(this).matches()

fun String.isValidPassword() = Pattern.compile("^(?=.*?[A-Za-z])(?=.*?[0-9]).{6,20}").matcher(this).matches()

fun String.isValidMobile() = Pattern.compile("^[6-9]\\d{9}$").matcher(this).matches()
