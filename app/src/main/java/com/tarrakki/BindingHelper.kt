package com.tarrakki

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.tarrakki.api.model.Goal
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.DialogInvestBinding
import com.tarrakki.databinding.DialogInvestGoalBinding
import com.tarrakki.databinding.DialogInvestStratergyBinding
import net.cachapa.expandablelayout.ExpandableLayout
import org.greenrobot.eventbus.EventBus
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import org.supportcompact.networking.ApiClient
import org.supportcompact.widgets.DividerItemDecorationNoLast
import org.supportcompact.widgets.InputFilterMinMax
import java.util.*

const val IS_FROM_FORGOT_PASSWORD = "is_from_forgot_password"
const val IS_FROM_ACCOUNT = "is_from_account"
const val IS_FROM_INTRO = "is_from_Intro"
const val IS_FROM_BANK_ACCOUNT = "is_from_bank_account"


@BindingAdapter(value = ["setAdapterH", "isHome"], requireAll = false)
fun setAdapterH(view: RecyclerView, homeItems: ArrayList<WidgetsViewModel>?, isHome: Boolean) {
    view.isFocusable = false
    view.isNestedScrollingEnabled = false
    homeItems?.let {
        view.setUpMultiViewRecyclerAdapter(homeItems) { item, binder, position ->
            binder.setVariable(BR.widget, item)
            binder.executePendingBindings()
            binder.root.setOnClickListener { it ->
                if (isHome)
                    App.INSTANCE.widgetsViewModel.value = item
                else
                    App.INSTANCE.widgetsViewModelB.value = item
            }
        }
    }
}

@BindingAdapter(value = ["setAdapterH"], requireAll = false)
fun setAdapterH(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    val manager = LinearLayoutManager(view.context)
    manager.orientation = RecyclerView.HORIZONTAL
    view.layoutManager = manager
    view.adapter = adapter
}

@BindingAdapter(value = ["setAdapterV"], requireAll = false)
fun setAdapterV(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    val manager = LinearLayoutManager(view.context)
    manager.orientation = RecyclerView.VERTICAL
    view.layoutManager = manager
    view.adapter = adapter
}

@BindingAdapter("dividerH", requireAll = false)
fun setDividerHorizontal(rv: RecyclerView, drawable: Drawable? = null) {
    val divider = DividerItemDecorationNoLast(rv.context, LinearLayoutManager.HORIZONTAL)
    drawable?.let {
        divider.setDrawable(it)
    }
    rv.addItemDecoration(divider)
}

@BindingAdapter("dividerV", requireAll = false)
fun setDividerVertical(rv: RecyclerView, drawable: Drawable? = null) {
    val divider = DividerItemDecorationNoLast(rv.context, LinearLayoutManager.VERTICAL)
    drawable?.let {
        divider.setDrawable(it)
    }
    rv.addItemDecoration(divider)
}

@BindingAdapter("enableNestedScrollView")
fun enableNestedScrollView(rv: RecyclerView, enable: Boolean) {
    rv.isNestedScrollingEnabled = enable
    rv.isFocusable = enable
}

@BindingAdapter("imgUrl")
fun setIndicator(img: ImageView, @DrawableRes res: Int) {
    img.setImageResource(res)
}

@BindingAdapter("imgUrl")
fun setIndicator(img: ImageView, url: String?) {
    url?.let {
        Glide.with(img).load(ApiClient.IMAGE_BASE_URL.plus(it)).into(img)
    }
}

@BindingAdapter("expanded")
fun setIndicator(view: ExpandableLayout, value: Boolean) {
    if (value) {
        view.expand(true)
    } else {
        view.collapse(true)
    }
}

@BindingAdapter(value = ["price", "anim"], requireAll = false)
fun applyCurrencyFormat(txt: TextView, amount: String?, anim: Boolean?) {
    applyCurrencyFormat(txt, amount?.toDouble(), anim)
}

@BindingAdapter(value = ["price", "anim"], requireAll = false)
fun applyCurrencyFormat(txt: TextView, amount: Double?, anim: Boolean?) {
    amount?.let {
        if (anim != null && anim) {
            handleTextView((amount % 10).toInt(), amount.toInt(), txt)
            return
        }
        txt.text = amount.toCurrency()
    }
}

@BindingAdapter(value = ["price", "anim"], requireAll = false)
fun applyCurrencyFormat(txt: TextView, amount: Int?, anim: Boolean?) {
    amount?.let {
        if (anim != null && anim) {
            handleTextView((amount % 10), amount.toInt(), txt)
            return
        }
        txt.text = amount.toDouble().toCurrency()
    }
}

@BindingAdapter(value = ["dprice", "anim"], requireAll = false)
fun applyDCurrencyFormat(txt: TextView, amount: Double?, anim: Boolean?) {
    amount?.let {
        if (anim != null && anim) {
            handleTextView((amount % 10), amount, txt)
            return
        }
        txt.text = amount.toDecimalCurrency()
    }
}

@BindingAdapter(value = ["returns", "anim"], requireAll = false)
fun returns(txt: TextView, amount: Double, anim: Boolean = true) {
    if (anim) {
        returns((amount % 10), amount, txt)
        return
    }
    txt.text = amount.toCurrency()
}


@BindingAdapter("isCurrency")
fun inputType(edt: EditText, isCurrency: Boolean) = if (isCurrency) {
    edt.applyCurrencyFormat()
} else {

}

@BindingAdapter("isPositiveCurrency")
fun inputTypePositive(edt: EditText, isPositiveCurrency: Boolean) = if (isPositiveCurrency) {
    edt.applyCurrencyFormatPositiveOnly()
} else {
    edt.applyCurrencyFormat()
}

@BindingAdapter(value = ["minValue", "maxValue"])
fun setDecimalDigits(edt: EditText, minValue: Int, maxValue: Int) {
    try {
        edt.filters = arrayOf<InputFilter>(InputFilterMinMax(minValue, maxValue))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@BindingAdapter(value = ["qRange"])
fun setQRange(edt: EditText, q: Goal.Data.GoalData.Question) {
    try {
        if (!TextUtils.isEmpty("${q.minValue}") && !TextUtils.isEmpty(q.maxValue)) {
            edt.filters = arrayOf<InputFilter>(InputFilterMinMax(q.minValue.toFloat(), q.maxValue.toFloat()))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.setMinMax(minValue: Int, maxValue: Int) {
    try {
        filters = arrayOf<InputFilter>(InputFilterMinMax(minValue, maxValue))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


@BindingAdapter(value = ["isPANCard"])
fun setPANCard(edt: EditText, isPANCard: Boolean) {
    if (isPANCard) {
        edt.applyPAN()
    }
}

fun EditText.applyPAN() {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inputType = when (s?.length) {
                in 5..8 -> {
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                }
                else -> {
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                }
            }
            setSelection(text.length)
        }
    })
}


@BindingAdapter("onEditorAction")
fun setEditorAction(editText: EditText, onEditorActionListener: TextView.OnEditorActionListener) {
    editText.setOnEditorActionListener(onEditorActionListener)
}

@BindingAdapter("openYoutube")
fun watchYoutubeVideo(view: View, videoUrl: String) {
    view.setOnClickListener {
        try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            view.context.startActivity(webIntent)
        } catch (ex: Exception) {
            //view.context.startActivity(webIntent)
        }
    }
}

@BindingAdapter("HTML")
fun setHtml(txt: TextView, txtHtml: String?) {
    val txtHtmlNotNull = txtHtml ?: ""
    val html = "<!DOCTYPE html><html><body>$txtHtmlNotNull</body></html>"
    txt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(html)
    }
}

@BindingAdapter("onEditorAction")
fun setAction(txt: EditText?, listener: TextView.OnEditorActionListener?) {
    txt?.setOnEditorActionListener(listener)
}

@BindingAdapter("requestToEdit")
fun requestToEdit(txt: EditText, requestToEdit: Boolean?) {
    requestToEdit?.let {
        txt.requestFocus()
        txt.setSelection(txt.length())
        txt.showKeyboard()
    }
}

/*fun TextView.decimalFormat(amount: Double) {
    this.text = String.format(Locale.US, "%,.2f", amount)
}

fun TextView.format(amount: Double) {
    this.text = String.format(Locale.US, "%,d", Math.round(amount))
}*/

fun handleTextView(initialValue: Int, finalValue: Int, textview: TextView) {
    val valueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
    valueAnimator.duration = 1500
    valueAnimator.addUpdateListener { it ->
        textview.text = it.animatedValue.toString().toDouble().toCurrency()
    }
    valueAnimator.start()
}

fun handleTextView(initialValue: Double, finalValue: Double, textview: TextView) {
    val valueAnimator = ValueAnimator.ofFloat(initialValue.toFloat(), finalValue.toFloat())
    valueAnimator.duration = 1500
    valueAnimator.addUpdateListener { it ->
        textview.text = it.animatedValue.toString().toDouble().toDecimalCurrency()
    }
    valueAnimator.start()
}

fun returns(initialValue: Double, finalValue: Double, textview: TextView) {
    val valueAnimator = ValueAnimator.ofFloat(initialValue.toFloat(), finalValue.toFloat())
    valueAnimator.duration = 1500
    val returnType = if (finalValue >= 0) "+" else "-"
    valueAnimator.addUpdateListener { it ->
        textview.text = returnType.plus(it.animatedValue.toString().toDouble().toDecimalCurrency())
    }
    valueAnimator.start()
}

fun Context.investGoalDialog(goal: Goal.Data.GoalData? = null, onInvest: ((amountLumpsum: String, amountSIP: String, duration: String) -> Unit)? = null) {
    val mBinder = DialogInvestGoalBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()
    if (goal != null && goal.isCustomInvestment())
        mBinder.investment = goal.pmt?.toString()
    else
        mBinder.investment = goal?.getPMT()?.ans
    mBinder.lumpsum = goal?.getPVAmount()
    mBinder.durations = goal?.getNDuration()
    mBinder.btnInvest.setOnClickListener {
        if (goal != null) {
            //val cv = goal.getCV()
            val n = goal.getN()
            if (/*cv != null && */n != null) {
                try {
                    /*
                    * "pv" -> {
                    var amount = ""
                    getViewModel().goalVM.value?.let { goal ->
                        amount = "${goal.getCVAmount()}".replace(",", "")
                    }
                    val pvAmount = "${question.ans}".replace(",", "")
                    //amount = "${question.ans}".replace(",", "")
                    if ((TextUtils.isEmpty(amount) && TextUtils.isEmpty(pvAmount)) || pvAmount.toDouble() > amount.toDouble()) {
                        //var msg = "Please enter a valid number above".plus(" ".plus(question.minValue))
                        context?.simpleAlert("Your lumpsum investment cannot be equal to or more than your total investment goal.")
                        false
                    } else
                        true
                }
                    * */
                    val pvAmount = "${mBinder.lumpsum}".replace(",", "")
                    val amount = "${if (goal.isCustomInvestment()) goal.getInvestmentAmount() else goal.getPMT()?.ans}".replace(",", "")
                    if (!TextUtils.isEmpty(amount) && !TextUtils.isEmpty(pvAmount) && pvAmount.toDouble() > amount.toDouble()) {
                        //var msg = "Please enter a valid number above".plus(" ".plus(question.minValue))
                        EventBus.getDefault().post(ShowError("Your lumpsum investment cannot be equal to or more than your total investment goal."))
                    } else if (TextUtils.isEmpty(mBinder.durations) || "${mBinder.durations}".toDouble() !in n.minValue..n.maxValue.toDouble()) {
                        val msg = "Please enter a valid number of years between"
                                .plus(" ".plus(n.minValue))
                                .plus(" to ".plus(n.maxValue.toIntOrNull()))
                        EventBus.getDefault().post(ShowError(msg))
                    } else {
                        mDialog.dismiss()
                        onInvest?.invoke(mBinder.edtLumpsum.text.toString(), mBinder.edtSIPAmount.text.toString(), mBinder.edtDurations.text.toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            mDialog.dismiss()
            onInvest?.invoke(mBinder.edtLumpsum.text.toString(), mBinder.edtSIPAmount.text.toString(), mBinder.edtDurations.text.toString())
        }
        it.dismissKeyboard()
    }
    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
    }
    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun Context.investDialog(fundId: Int, minSIPAmount: Double,
                         minLumsumpAmount: Double,
                         onInvest: ((amountLumpsum: String, amountSIP: String, fundId: Int) -> Unit)? = null) {
    val mBinder = DialogInvestBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()
    mBinder.btnInvest.setOnClickListener {
        val lumpsumAmount = mBinder.edtLumpsum.text.toString().toCurrency()
        val sipAmount = mBinder.edtSIPAmount.text.toString().toCurrency()
        it.dismissKeyboard()
        if (this.isInvestDialogValid(minSIPAmount, minLumsumpAmount, sipAmount, lumpsumAmount)) {
            mDialog.dismiss()
            onInvest?.invoke(lumpsumAmount.toString(),
                    sipAmount.toString(),
                    fundId)
        }
    }
    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
    }

    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun Context.investmentStragiesDialog(
        thirdLevelCategory: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory,
        onInvest: ((thirdLevelCategory: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory,
                    amountLumpsum: Int, amountSIP: Int) -> Unit)? = null) {
    val mBinder = DialogInvestStratergyBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()

    /*mBinder.edtSIPAmount.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val sipAmount = p0.toString()
            val lumpsumAmount = mBinder.edtLumpsum.text.toString()

            if (sipAmount.isNotEmpty() || lumpsumAmount.isNotEmpty()) {
                mBinder.edtDurations.isFocusable = true
                mBinder.edtDurations.isFocusableInTouchMode = true
                mBinder.edtDurations.background = getDrawable(R.drawable.shape_edt_gray_round)
            } else {
                mBinder.edtDurations.isFocusable = false
                mBinder.edtDurations.isFocusableInTouchMode = false
                mBinder.edtDurations.background = getDrawable(R.drawable.shape_edt_disable_gray_round)
            }
        }

    })

    mBinder.edtLumpsum.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val lumpsumAmount = p0.toString()
            val sipAmount = mBinder.edtSIPAmount.text.toString()

            if (sipAmount.isNotEmpty() || lumpsumAmount.isNotEmpty()) {
                mBinder.edtDurations.isFocusable = true
                mBinder.edtDurations.isFocusableInTouchMode = true
                mBinder.edtDurations.background = getDrawable(R.drawable.shape_edt_gray_round)
            } else {
                mBinder.edtDurations.isFocusable = false
                mBinder.edtDurations.isFocusableInTouchMode = false
                mBinder.edtDurations.background = getDrawable(R.drawable.shape_edt_disable_gray_round)
            }
        }

    })*/

    mBinder.btnInvest.setOnClickListener {
        val lumpsumAmount = mBinder.edtLumpsum.text.toString().toCurrencyInt()
        val sipAmount = mBinder.edtSIPAmount.text.toString().toCurrencyInt()
        // val duration = mBinder.edtDurations.text.toString()
        it.dismissKeyboard()

        if (lumpsumAmount == 0 && sipAmount == 0) {
            this.simpleAlert("Please enter either the lumpsum or the SIP amount first.")
        } else {
            mDialog.dismiss()
            onInvest?.invoke(thirdLevelCategory, lumpsumAmount,
                    sipAmount)
        }
    }
    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
    }

    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun String.toYearWord(): String {
    return try {
        val n = this.toDoubleOrNull()
        if (n != null && n <= 1) "year" else "years"
    } catch (e: Exception) {
        "years"
    }
}

fun Context.isInvestDialogValid(minSIPAmount: Double,
                                minLumsumpAmount: Double,
                                sipAmount: Double,
                                lumpsumAmount: Double): Boolean {
    if (lumpsumAmount == 0.0 && sipAmount == 0.0) {
        this.simpleAlert("Please enter either the lumpsum or the SIP amount first.")
        return false
    }
    if (lumpsumAmount != 0.0) {
        if (lumpsumAmount < minLumsumpAmount) {
            this.simpleAlert("The lumpsum amount must be greater than or equal to ${minLumsumpAmount.toCurrency()}.")
            return false
        }
    }
    if (sipAmount != 0.0) {
        if (sipAmount < minSIPAmount) {
            this.simpleAlert("The SIP amount must be greater than or equal to ${minSIPAmount.toCurrency()}.")
            return false
        }
    }
    return true
}

fun Context.isLumpsumAmountValid(minLumsumpAmount: Int,
                                 lumpsumAmount: Int): Boolean {
    if (lumpsumAmount != 0) {
        if (lumpsumAmount < minLumsumpAmount) {
            this.simpleAlert("The lumpsum amount must be greater than or equal to ${minLumsumpAmount.toCurrency()}.")
            return false
        }
    }
    return true
}

fun Context.isSIPAmountValid(minSIPAmount: Int,
                             sipAmount: Int): Boolean {
    if (sipAmount != 0) {
        if (sipAmount < minSIPAmount) {
            this.simpleAlert("The SIP amount must be greater than or equal to ${minSIPAmount.toCurrency()}.")
            return false
        }
    }
    return true
}

fun getOrdinalFormat(num: Int): String {
    val suffix = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    val m = num % 100
    return num.toString() + suffix[if (m > 3 && m < 21) 0 else m % 10]
}