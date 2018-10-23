package org.supportcompact.ktx

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import java.util.*

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

private fun TextView.decimalFormat(amount: Double) {
    this.text = String.format(Locale.US, "%,.2f", amount)
}

private fun TextView.format(amount: Double) {
    this.text = String.format(Locale.US, "%,d", Math.round(amount))
}

fun Double.toDecimalCurrency() = String.format(Locale.US, "\u20B9%,.2f", this)

fun Double.toCurrency() = String.format(Locale.US, "\u20B9%,d", Math.round(this))