package org.supportcompact.ktx

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
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

    //addTextChangedListener(InputCurrency(this))

    addTextChangedListener(object : TextWatcher {

        private var current = ""

        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(s: CharSequence?, p1: Int, delete: Int, add: Int) {
            if (s == null || s.isEmpty()) {
                current = ""
                return
            }
            if (s.toString() != current) {
                try {
                    this@applyCurrencyFormatPositiveOnly.removeTextChangedListener(this)
                    var cleanString = s.toString().replace(",", "")
                    if (cleanString.length > 16) {
                        cleanString = cleanString.dropLast(1)
                    }
                    val amount = cleanString.toDouble()
                    if (amount > 0) {
                        this@applyCurrencyFormatPositiveOnly.format(amount)
                    } else {
                        this@applyCurrencyFormatPositiveOnly.text.clear()
                    }
                    current = this@applyCurrencyFormatPositiveOnly.text.toString()
                    e("Current=>${current.length}")
                    e("Cursor=>$p1")
                    this@applyCurrencyFormatPositiveOnly.setSelection(current.length)
                    //this@applyCurrencyFormatPositiveOnly.setSelection(current.length)
                    this@applyCurrencyFormatPositiveOnly.addTextChangedListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })
}

fun EditText.applyCurrencyDecimalFormatPositiveOnly() {

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
                    this@applyCurrencyDecimalFormatPositiveOnly.removeTextChangedListener(this)
                    var cleanString = s.toString().replace(",", "")
                    if (cleanString.length > 16 || (cleanString.contains(".") && cleanString.split(".")[1].length > 2)) {
                        cleanString = cleanString.dropLast(1)
                    }
                    val amount = cleanString.toDouble()
                    if (amount > 0) {
                        this@applyCurrencyDecimalFormatPositiveOnly.setText(amount.decimalFormat())
                        if (cleanString.contains(".") && cleanString.split(".")[1].isEmpty()) {
                            this@applyCurrencyDecimalFormatPositiveOnly.append(".")
                        }
                        //this@applyCurrencyDecimalFormatPositiveOnly.decimalFormat(amount)
                    } else {
                        this@applyCurrencyDecimalFormatPositiveOnly.text.clear()
                    }
                    current = this@applyCurrencyDecimalFormatPositiveOnly.text.toString()
                    this@applyCurrencyDecimalFormatPositiveOnly.setSelection(current.length)
                    this@applyCurrencyDecimalFormatPositiveOnly.addTextChangedListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })
}

fun EditText.applyCurrencyDecimalFormatPositiveOnlyWithoutRoundOff() {

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
                    this@applyCurrencyDecimalFormatPositiveOnlyWithoutRoundOff.removeTextChangedListener(this)
                    var cleanString = s.toString().replace(",", "")
                    if (cleanString.length > 16 || (cleanString.contains(".") && cleanString.split(".")[1].length > 2)) {
                        cleanString = cleanString.dropLast(1)
                    }
                    val amount = cleanString.toDouble()
                    this@applyCurrencyDecimalFormatPositiveOnlyWithoutRoundOff.setText(amount.decimaldWithoutRoundOffFormat())
                    /*if (amount > 0) {
                        this@applyCurrencyDecimalFormatPositiveOnly.setText(amount.decimalFormat())
                        if (cleanString.contains(".") && cleanString.split(".")[1].isEmpty()) {
                            this@applyCurrencyDecimalFormatPositiveOnly.append(".")
                        }
                        //this@applyCurrencyDecimalFormatPositiveOnly.decimalFormat(amount)
                    } else {
                        this@applyCurrencyDecimalFormatPositiveOnly.text.clear()
                    }*/
                    current = this@applyCurrencyDecimalFormatPositiveOnlyWithoutRoundOff.text.toString()
                    this@applyCurrencyDecimalFormatPositiveOnlyWithoutRoundOff.setSelection(current.length)
                    this@applyCurrencyDecimalFormatPositiveOnlyWithoutRoundOff.addTextChangedListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })
}

fun EditText.applyCurrencyDecimalFormatPositiveOnly3D() {

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
                    this@applyCurrencyDecimalFormatPositiveOnly3D.removeTextChangedListener(this)
                    var cleanString = s.toString().replace(",", "")
                    if (cleanString.length > 16 || (cleanString.contains(".") && cleanString.split(".")[1].length > 3)) {
                        cleanString = cleanString.dropLast(1)
                    }
                    val amount = cleanString.toDouble()
                    if (amount > 0) {
                        if (cleanString.contains(".")/* && cleanString.split(".")[1].isEmpty()*/) {
                            this@applyCurrencyDecimalFormatPositiveOnly3D.setText(cleanString.substringBefore(".").toDouble().format())
                            this@applyCurrencyDecimalFormatPositiveOnly3D.append(cleanString.substring(cleanString.indexOf(".")))
                        } else {
                            this@applyCurrencyDecimalFormatPositiveOnly3D.setText(amount.format())
                        }
                    } else {
                        this@applyCurrencyDecimalFormatPositiveOnly3D.setText(cleanString)
                    }
                    current = this@applyCurrencyDecimalFormatPositiveOnly3D.text.toString()
                    this@applyCurrencyDecimalFormatPositiveOnly3D.setSelection(current.length)
                    this@applyCurrencyDecimalFormatPositiveOnly3D.addTextChangedListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })
}

fun EditText.applyCurrencyInfiniteDecimalFormatPositiveOnly() {

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
                    this@applyCurrencyInfiniteDecimalFormatPositiveOnly.removeTextChangedListener(this)
                    var cleanString = s.toString().replace(",", "")
                    if (cleanString.length > 16/* || (cleanString.contains(".") && cleanString.split(".")[1].length > 3)*/) {
                        cleanString = cleanString.dropLast(1)
                    }
                    val amount = cleanString.toDouble()
                    if (amount > 0) {
                        if (cleanString.contains(".")/* && cleanString.split(".")[1].isEmpty()*/) {
                            this@applyCurrencyInfiniteDecimalFormatPositiveOnly.setText(cleanString.substringBefore(".").toDouble().format())
                            this@applyCurrencyInfiniteDecimalFormatPositiveOnly.append(cleanString.substring(cleanString.indexOf(".")))
                        } else {
                            this@applyCurrencyInfiniteDecimalFormatPositiveOnly.setText(amount.format())
                        }
                    } else {
                        this@applyCurrencyInfiniteDecimalFormatPositiveOnly.setText(cleanString)
                    }
                    current = this@applyCurrencyInfiniteDecimalFormatPositiveOnly.text.toString()
                    this@applyCurrencyInfiniteDecimalFormatPositiveOnly.setSelection(current.length)
                    this@applyCurrencyInfiniteDecimalFormatPositiveOnly.addTextChangedListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })
}


//val formatter = NumberFormat.getIntegerInstance(Locale("en", "in"))
val formatter = DecimalFormat("##,##,##,##,##,##,##,##0")
val dFormatter = DecimalFormat("##,##,##,##,##,##,##,###.##")
val dFormatterCharges = DecimalFormat("##,##,##,##,##,##,##,###.#####")

val dFormatterWithoutRoundOff = DecimalFormat("##,##,##,##,##,##,##,###.##").apply {
    roundingMode = RoundingMode.FLOOR
}
val dFormatter3D = DecimalFormat("##,##,##,##,##,##,##,###.###").apply {
    roundingMode = RoundingMode.FLOOR
}


fun TextView.decimalFormat(amount: Double) {
    this.text = dFormatter.format(amount)//String.format(locale, "%,.2f", amount)
}

fun TextView.format(amount: Double) {
    this.text = formatter.format(Math.round(amount))//String.format(locale, "%,d", Math.round(amount))
}

fun Double.roundOff() = Math.round(this * 100).toDouble() / 100

fun Double.format() = formatter.format(Math.round(this))

fun Double.decimalFormat() = dFormatter.format(this)

fun Double.decimaldWithoutRoundOffFormat() = dFormatterWithoutRoundOff.format(this)

fun Double.decimalFormat3D() = dFormatter3D.format(this)

fun Double.toDecimalCurrencyWithoutRoundOff() = "\u20B9".plus(dFormatterWithoutRoundOff.format(this))//String.format(locale, "\u20B9%,.2f", this)

fun Double.toDecimalCurrency() = "\u20B9".plus(dFormatter.format(this))//String.format(locale, "\u20B9%,.2f", this)

fun Double.toCurrency() = "\u20B9".plus(formatter.format(Math.round(this)))//String.format(locale, "\u20B9%,d", Math.round(this))

fun Double.toCr() = "\u20B9".plus(dFormatter.format(this / 10000000))//String.format(locale, "\u20B9%,d", Math.round(this))

fun Double.toCurrencyWithSpace() = "\u20B9 ".plus(formatter.format(Math.round(this)))//String.format(locale, "\u20B9%,d", Math.round(this))

fun Double.toReturn(): String = dFormatter.format(this)

fun Double.toReturnAsPercentage() = dFormatter.format(this).plus("%")

fun Double.toCharges() = dFormatterCharges.format(this).plus("%")

fun parseToPercentageOrNA(num: String?): String {
    return try {
        val temp: Double? = num?.toDoubleOrNull()
        if (temp == null || temp == 0.0) "N/A" else temp.toReturnAsPercentage()
    } catch (e: java.lang.Exception) {
        "N/A"
    }
}

fun toCurrency(num: String?): String {
    return try {
        val temp: Double? = num?.toDoubleOrNull()
        temp?.toString() ?: return 0.0.toString()
    } catch (e: java.lang.Exception) {
        "0.0"
    }
}

fun parseAsReturnOrNA(num: String?): String {
    return try {
        val temp: Double? = num?.toDoubleOrNull()
        temp?.toReturn() ?: "N/A"
    } catch (e: java.lang.Exception) {
        "N/A"
    }
}

fun parseAsReturn(num: String?): String {
    return try {
        val temp: Double? = num?.toDoubleOrNull()
        if (temp == null || temp == 0.0) "0%" else temp.toReturnAsPercentage()
    } catch (e: java.lang.Exception) {
        "0%"
    }
}

fun parseAsNoZiroReturnOrNA(num: String?): String {
    return try {
        val temp: Double? = num?.toDoubleOrNull()
        if (temp == null || temp == 0.0) "N/A" else temp.toReturn()
    } catch (e: java.lang.Exception) {
        "N/A"
    }
}

fun String.toCurrency(): Double = try {
    this.replace(",", "").toDoubleOrNull() ?: 0.0
} catch (e: java.lang.Exception) {
    0.0
}

fun String.toCurrencyBigInt(): BigInteger = try {
    this.replace(",", "").replace("\u20B9", "").replace("₹", "").toBigIntegerOrNull()
            ?: BigInteger.ZERO
} catch (e: java.lang.Exception) {
    BigInteger.ZERO
}

fun toBigInt(amount: String?): BigInteger = try {
    amount?.replace(",", "")?.replace("\u20B9", "")?.replace("₹", "")?.toBigIntegerOrNull()
            ?: BigInteger.ONE
} catch (e: java.lang.Exception) {
    BigInteger.ONE
}

fun toBigIntDefaultZero(amount: String?): BigInteger = try {
    amount?.replace(",", "")?.replace("\u20B9", "")?.replace("₹", "")?.toBigIntegerOrNull()
            ?: BigInteger.ZERO
} catch (e: java.lang.Exception) {
    BigInteger.ZERO
}

fun BigInteger.toCurrency(): String {
    return try {
        val temp: Double? = this.toDouble()
        if (temp != null) {
            temp.toCurrency()
        } else {
            0.0.toCurrency()
        }
    } catch (e: java.lang.Exception) {
        0.0.toCurrency()
    }
}

fun String.toCurrencyBigDecimal(): BigDecimal = try {
    this.replace(",", "").toBigDecimalOrNull() ?: BigDecimal.ZERO
} catch (e: java.lang.Exception) {
    BigDecimal.ZERO
}

fun Int.toCurrency() = "\u20B9".plus(formatter.format(this))