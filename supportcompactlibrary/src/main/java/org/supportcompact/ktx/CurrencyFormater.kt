package org.supportcompact.ktx

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import java.text.DecimalFormat

fun EditText.applyCurrencyFormat() {

    addTextChangedListener(object : TextWatcher {
        private var current = ""
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s == null || s.isEmpty()) {
                current = ""
                return
            }
            if (s.toString() != current) {
                try {
                    this@applyCurrencyFormat.removeTextChangedListener(this)
                    val cleanString = s.toString().replace(",", "")
                    this@applyCurrencyFormat.format(cleanString.toDouble())
                    current = this@applyCurrencyFormat.text.toString()
                    this@applyCurrencyFormat.setSelection(current.length)
                    this@applyCurrencyFormat.addTextChangedListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })
}

fun EditText.applyCurrencyFormatPositiveOnly() {

    addTextChangedListener(object : TextWatcher {
        private var current = ""
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s == null || s.isEmpty()) {
                current = ""
                return
            }
            if (s.toString() != current) {
                try {
                    this@applyCurrencyFormatPositiveOnly.removeTextChangedListener(this)
                    val cleanString = s.toString().replace(",", "")
                    val amount = cleanString.toDouble()
                    if (amount > 0) {
                        this@applyCurrencyFormatPositiveOnly.format(amount)
                    } else {
                        this@applyCurrencyFormatPositiveOnly.text.clear()
                    }
                    current = this@applyCurrencyFormatPositiveOnly.text.toString()
                    this@applyCurrencyFormatPositiveOnly.setSelection(current.length)
                    this@applyCurrencyFormatPositiveOnly.addTextChangedListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })
}


//val formatter = NumberFormat.getIntegerInstance(Locale("en", "in"))
val formatter = DecimalFormat("##,##,##0")
val dFormatter = DecimalFormat("##,##,##.##")

fun TextView.decimalFormat(amount: Double) {
    this.text = dFormatter.format(amount)//String.format(locale, "%,.2f", amount)
}

fun TextView.format(amount: Double) {
    this.text = formatter.format(Math.round(amount))//String.format(locale, "%,d", Math.round(amount))
}

fun Double.format() = formatter.format(Math.round(this))

fun Double.decimalFormat() = dFormatter.format(Math.round(this))

fun Double.toDecimalCurrency() = "\u20B9".plus(dFormatter.format(this))//String.format(locale, "\u20B9%,.2f", this)

fun Double.toCurrency() = "\u20B9".plus(formatter.format(Math.round(this)))//String.format(locale, "\u20B9%,d", Math.round(this))

fun Double.toCurrencyWithSpace() = "\u20B9 ".plus(formatter.format(Math.round(this)))//String.format(locale, "\u20B9%,d", Math.round(this))

fun Double.toReturn() = dFormatter.format(this)

fun Double.toReturnAsPercentage() = dFormatter.format(this).plus("%")

fun parseToPercentageOrNA(num: String?): String {
    return try {
        val temp: Double? = num?.toDoubleOrNull()
        temp?.toReturnAsPercentage() ?: "NA"
    } catch (e: java.lang.Exception) {
        "NA"
    }
}

fun String.toCurrency(): Double = try {
    this.replace(",", "").toDoubleOrNull() ?: 0.0
} catch (e: java.lang.Exception) {
    0.0
}