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
                this@applyCurrencyFormat.removeTextChangedListener(this)
                val cleanString = s.toString().replace(",", "")
                this@applyCurrencyFormat.format(cleanString.toDouble())
                current = this@applyCurrencyFormat.text.toString()
                this@applyCurrencyFormat.setSelection(current.length)
                this@applyCurrencyFormat.addTextChangedListener(this)
            }
        }
    })
}

fun TextView.decimalFormat(amount: Double) {
    this.text = String.format(Locale.US, "%,.2f", amount)
}

fun TextView.format(amount: Double) {
    this.text = String.format(Locale.US, "%,d", Math.round(amount))
}