package org.supportcompact.ktx

import android.graphics.Color
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.TextView

fun String.toHTMl(): Spanned {
    val data = "<!DOCTYPE html><html><body>$this</body></html>"
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT) else Html.fromHtml(data)
}

fun String.toHTMl(fontFamily: String): String {
    val data = "<!DOCTYPE html><html><head><style>" +
            "        @font-face {" +
            "            font-family: '$fontFamily';" +
            "            src: url('font/$fontFamily');" +
            "        }" +
            "        #font {" +
            "            font-family: '$fontFamily';" +
            "        }</style></head><body>$this</body></html>"
    return data
}

fun String.toColorAndUnderlineFromHTML(color: String = "#00CB00") = "<font color='$color'><u>" + this + "</u></font>"

fun String.toColorFromHTML(color: String = "#FFFFFF") = "<font color='$color'>" + this + "</font>"

fun TextView.makeLinks(links: Array<String>, clickableSpans: Array<ClickableSpan>) {
    val spannableString = SpannableString(text)
    for (i in links.indices) {
        val clickableSpan = clickableSpans[i]
        val link = links[i]
        val startIndexOfLink = text.toString().indexOf(link)
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
    movementMethod = LinkMovementMethod.getInstance()
    setText(spannableString, TextView.BufferType.SPANNABLE)
}