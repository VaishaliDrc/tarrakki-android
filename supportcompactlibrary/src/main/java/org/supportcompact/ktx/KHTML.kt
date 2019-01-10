package org.supportcompact.ktx

import android.os.Build
import android.text.Html
import android.text.Spanned

fun String.toHTMl(): Spanned {
    val data = "<!DOCTYPE html><html><body>" + this + "</body></html>"
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT) else Html.fromHtml(data)
}

fun String.toColorAndUnderlineFromHTML(color: String = "#00CB00") = "<font color='$color'><u>" + this + "</u></font>"

fun String.toColorFromHTML(color: String = "#FFFFFF") = "<font color='$color'>" + this + "</font>"