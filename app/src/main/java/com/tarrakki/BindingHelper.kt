package com.tarrakki

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.databinding.BindingAdapter
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.*
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.tarrakki.api.model.FolioData
import com.tarrakki.api.model.Goal
import com.tarrakki.api.model.HomeData
import com.tarrakki.api.model.SIPDetails
import com.tarrakki.databinding.*
import net.cachapa.expandablelayout.ExpandableLayout
import org.greenrobot.eventbus.EventBus
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import com.tarrakki.api.ApiClient
import org.supportcompact.widgets.DividerItemDecorationNoLast
import org.supportcompact.widgets.InputFilterMinMax
import java.math.BigInteger

const val IS_FROM_FORGOT_PASSWORD = "is_from_forgot_password"
const val IS_FROM_ACCOUNT = "is_from_account"
const val IS_FROM_INTRO = "is_from_Intro"
const val IS_FROM_BANK_ACCOUNT = "is_from_bank_account"
const val IS_FROM_COMLETE_REGISTRATION = "is_from_complete_registration"


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
fun setAdapterH(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
    val manager = LinearLayoutManager(view.context)
    manager.orientation = RecyclerView.HORIZONTAL
    view.layoutManager = manager
    view.adapter = adapter
}

@BindingAdapter(value = ["setAdapterV"], requireAll = false)
fun setAdapterV(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
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

@BindingAdapter("title")
fun toolbarText(toolbar: CenteredToolbar, title: String?) {
    if (title != null) {
        toolbar.title = title
    }
}

@BindingAdapter("imgUrl")
fun setIndicator(img: ImageView, @DrawableRes res: Int) {
    img.setImageResource(res)
}

@BindingAdapter("background")
fun setBackgroundImage(img: TextView, @DrawableRes res: Int) {
    img.setBackgroundResource(res)
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

@BindingAdapter("visibility")
fun setVisibility(view: View, visibility: Boolean) {
    Log.e("E", "Notify2=$visibility")
    view.visibility = if (visibility) View.VISIBLE else View.GONE
}

@BindingAdapter(value = ["price", "anim"], requireAll = false)
fun applyCurrencyFormat(txt: TextView, amount: BigInteger?, anim: Boolean?) {
    applyCurrencyFormat(txt, amount?.toString()?.toDouble(), anim)
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

@BindingAdapter("returnpercentage")
fun returns(progressBar: CircularProgressBar, returnpercentage: Int) {
    progressBar.backgroundColor = Color.parseColor("#2E1D3E")
    progressBar.progressBarWidth = App.INSTANCE.resources.getDimension(R.dimen.space_8)
    progressBar.backgroundProgressBarWidth = App.INSTANCE.resources.getDimension(R.dimen.space_8)
    if (returnpercentage >= 0) {
        progressBar.color = App.INSTANCE.color(R.color.colorAccent)
        progressBar.setProgressWithAnimation(returnpercentage.toFloat(), 1000)
    } else {
        progressBar.color = App.INSTANCE.color(R.color.red)
        progressBar.setProgressWithAnimation(returnpercentage.toFloat() * -1, 1000)
    }
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

@BindingAdapter(value = ["isIFSCCode"])
fun setIFSCCode(edt: EditText, isIFSCCode: Boolean) {
    if (isIFSCCode) {
        edt.applyIFSCCode()
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

fun EditText.applyIFSCCode() {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inputType = when (s?.length) {
                in 0..3 -> {
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                }
                else -> {
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                }
            }
            setSelection(text.length)
        }
    })
}

@BindingAdapter("percentageGuideline")
fun setGuideLinePercentage(guideLine: Guideline, percentageGuildeline: Float) {
    val params = guideLine.layoutParams as ConstraintLayout.LayoutParams
    params.guidePercent = percentageGuildeline
    guideLine.layoutParams = params
}

@BindingAdapter("isPasswordTransformation")
fun setPasswordTransformation(textview: TextView, isPasswordTransformation: Boolean) {
    if (isPasswordTransformation) {
        textview.transformationMethod = PasswordTransformationMethod()
    }
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
    if (requestToEdit == true) {
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

@SuppressLint("SetTextI18n")
fun returns(initialValue: Double, finalValue: Double, textview: TextView) {
    val valueAnimator = ValueAnimator.ofFloat(initialValue.toFloat(), finalValue.toFloat())
    valueAnimator.duration = 1500
    val returnType = if (finalValue >= 0) "+" else ""

    textview.setTextColor(textview.context.color(if (finalValue >= 0) R.color.colorAccent else R.color.red))

    valueAnimator.addUpdateListener { it ->
        textview.text = returnType + it.animatedValue.toString().toDouble().toReturnAsPercentage()
    }
    valueAnimator.start()
}

fun Context.investGoalDialog(goal: Goal.Data.GoalData? = null, onInvest: ((amountLumpsum: String, amountSIP: String, duration: String) -> Unit)? = null) {
    val mBinder = DialogInvestGoalBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()
    if (goal != null && goal.isCustomInvestment())
        mBinder.investment = goal.pmt?.format()
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
                                .plus(" to ".plus(n.maxValue))
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
        it.dismissKeyboard()
    }
    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun Context.investDialog(fundId: Int, minSIPAmount: BigInteger,
                         minLumsumpAmount: BigInteger,
                         onInvest: ((amountLumpsum: String, amountSIP: String, fundId: Int) -> Unit)? = null) {
    val mBinder = DialogInvestBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()
    mBinder.btnInvest.setOnClickListener {
        val lumpsumAmount = mBinder.edtLumpsum.text.toString().toCurrencyBigInt()
        val sipAmount = mBinder.edtSIPAmount.text.toString().toCurrencyBigInt()
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
        it.dismissKeyboard()
    }

    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun Context.investmentStragiesDialog(
        thirdLevelCategory: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory,
        onInvest: ((thirdLevelCategory: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory,
                    amountLumpsum: BigInteger, amountSIP: BigInteger) -> Unit)? = null) {
    val mBinder = DialogInvestStratergyBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()

    mBinder.btnInvest.setOnClickListener {
        it.dismissKeyboard()

        val lumpsumAmount = mBinder.edtLumpsum.text.toString().toCurrencyBigInt()
        val sipAmount = mBinder.edtSIPAmount.text.toString().toCurrencyBigInt()
        if (this.isInvestDialogValid(BigInteger.valueOf(500), BigInteger.valueOf(5000),
                        sipAmount, lumpsumAmount)) {
            mDialog.dismiss()
            onInvest?.invoke(thirdLevelCategory, lumpsumAmount,
                    sipAmount)
        }
        /*
          if (lumpsumAmount == BigInteger.ZERO && sipAmount == BigInteger.ZERO) {
              this.simpleAlert("Please enter either the lumpsum or the SIP amount first.")
          } else {

          }*/
    }
    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
        it.dismissKeyboard()
    }

    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun Context.addFundPortfolioDialog(portfolioList: MutableList<FolioData>,
                                   minAmountLumpsum: BigInteger, minAmountSIP: BigInteger,
                                   onAdd: ((portfolio: String, amountLumpsum: BigInteger,
                                            amountSIP: BigInteger) -> Unit)? = null) {
    val mBinder = DialogAddFundPortfolioBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()

    val folioList = portfolioList.map { it.folioNo } as ArrayList

    if (folioList.isNotEmpty()) {
        mBinder.folio = folioList[0]
        mBinder.isSingleFolio = folioList.size == 1
    } else {
        mBinder.isSingleFolio = true
    }

    mBinder.rbgFolioType.setOnCheckedChangeListener { group, checkedId ->
        if (R.id.rbNew != checkedId) {
            mBinder.tvChooseFolio.visibility = View.VISIBLE
            mBinder.edtChooseFolio.visibility = View.VISIBLE
        } else {
            mBinder.tvChooseFolio.visibility = View.GONE
            mBinder.edtChooseFolio.visibility = View.GONE
        }
    }

    mBinder.rbCurrent.isChecked = true

    mBinder.edtChooseFolio.setOnClickListener {
        this.showListDialog("Select Folio", folioList) { item ->
            mBinder.folio = item
        }
    }

    mBinder.btnInvest.setOnClickListener {
        val lumpsumAmount = mBinder.edtLumpsum.text.toString().toCurrencyBigInt()
        val sipAmount = mBinder.edtSIPAmount.text.toString().toCurrencyBigInt()
        it.dismissKeyboard()
        if (this.isInvestDialogValid(minAmountSIP, minAmountLumpsum, sipAmount, lumpsumAmount)) {
            mDialog.dismiss()
            val folioNo = if (!mBinder.rbNew.isChecked) {
                mBinder.edtChooseFolio.text.toString()
            } else {
                ""
            }
            onAdd?.invoke(folioNo, lumpsumAmount, sipAmount)
        }
    }
    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
        it.dismissKeyboard()
    }

    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun Context.redeemFundPortfolioDialog(portfolioList: MutableList<FolioData>,
                                      onRedeem: ((portfolioNo: String,
                                                  totalAmount: String,
                                                  allRedeem: String,
                                                  amount: String) -> Unit)? = null) {
    val mBinder = DialogRedeemPortfolioBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtTotalInvestedAmount.applyCurrencyFormatPositiveOnly()
    mBinder.edtAmount.applyCurrencyFormatPositiveOnly()

    val folioList = portfolioList.map { it.folioNo } as ArrayList

    if (folioList.isNotEmpty()) {
        mBinder.folio = folioList[0]
        val folio = portfolioList.find { it.folioNo == folioList[0] }
        mBinder.investmentAmount = folio?.cValue
        mBinder.isSingleFolio = folioList.size == 1
    } else {
        mBinder.isSingleFolio = true
    }

    mBinder.edtAmount.setOnClickListener {
        if (mBinder.chkAmount.isChecked) {
            this.simpleAlert("Please uncheck the full amount option first.")
        }
    }

    mBinder.chkAmount.setOnCheckedChangeListener { buttonView, isChecked ->
        mBinder.edtAmount.isFocusable = !isChecked
        mBinder.edtAmount.isFocusableInTouchMode = !isChecked
        if (isChecked) {
            mBinder.edtAmount.setText(mBinder.investmentAmount)
        } else {
            mBinder.edtAmount.setText("")
        }
    }

    mBinder.edtChooseFolio.setOnClickListener {
        this.showListDialog("Select Folio", folioList) { item ->
            mBinder.folio = item
            val selectedAmount = portfolioList.find { it.folioNo == item }
            if (selectedAmount != null) {
                mBinder.investmentAmount = selectedAmount.cValue
                mBinder.chkAmount.isChecked = false
            }
        }
    }

    mBinder.btnInvest.setOnClickListener {
        it.dismissKeyboard()
        val amount = mBinder.edtAmount.text.toString()
        val folioNo = mBinder.edtChooseFolio.text.toString()

        if (this.isAmountValid(amount.toCurrencyBigInt())) {
            if (amount.toCurrencyBigInt() <= "${mBinder.investmentAmount}".toCurrencyBigInt()) {
                mDialog.dismiss()
                val isRedeem = if (mBinder.chkAmount.isChecked) {
                    "Y"
                } else {
                    "N"
                }
                onRedeem?.invoke(folioNo, amount.toCurrencyBigInt().toString(), isRedeem, amount)
            } else {
                this.simpleAlert("The redemption amount can not be greater than the current value of the selected folio.")
            }
        }

    }

    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
        it.dismissKeyboard()
    }

    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun Context.stopFundPortfolioDialog(portfolioList: MutableList<FolioData>,
                                    onStop: ((transactionId: Int, folio: String, startDate: String) -> Unit)? = null) {
    val mBinder = DialogStopTransactionBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtAmount.applyCurrencyFormatPositiveOnly()
    val folioList = portfolioList.filter { it.sipDetails?.isNotEmpty() == true }.map { it.folioNo } as ArrayList
    var startDateList = arrayListOf<SIPDetails>()
    var sipDetail: SIPDetails? = null

    if (folioList.isNotEmpty()) {
        mBinder.folio = folioList[0]
        val selectedFolio = portfolioList.find { it.folioNo == folioList[0] }
        if (selectedFolio != null) {
            if (selectedFolio.sipDetails?.isNotEmpty() == true) {
                startDateList = selectedFolio.sipDetails as ArrayList<SIPDetails>
                if (startDateList.isNotEmpty()) {
                    mBinder.startDate = startDateList[0].convertedDate
                }
                sipDetail = selectedFolio.sipDetails[0]
                if (sipDetail != null) {
                    if (sipDetail.amount != null) {
                        mBinder.amount = sipDetail.amount
                    } else {
                        mBinder.amount = "0"
                    }
                } else {
                    mBinder.amount = "0"
                }
            }
        }
        mBinder.isSingleFolio = folioList.size == 1
    } else {
        mBinder.isSingleFolio = true
    }

    mBinder.edtChooseFolio.setOnClickListener {
        this.showListDialog("Select Folio", folioList) { item ->
            mBinder.folio = item
            val selectedFolio = portfolioList.find { it.folioNo == item }
            if (selectedFolio != null) {
                startDateList = selectedFolio.sipDetails as ArrayList<SIPDetails>
                if (startDateList.isNotEmpty()) {
                    mBinder.startDate = startDateList[0].convertedDate
                    if (selectedFolio.sipDetails.isNotEmpty()) {
                        sipDetail = selectedFolio.sipDetails[0]
                        if (sipDetail != null) {
                            if (sipDetail?.amount != null) {
                                mBinder.amount = sipDetail?.amount
                            } else {
                                mBinder.amount = "0"
                            }
                        } else {
                            mBinder.amount = "0"
                        }
                    }

                }
            }
        }
    }

    mBinder.edtStartDate.setOnClickListener {
        this.showCustomListDialog("Select StartDate", startDateList) { item ->
            mBinder.startDate = item.convertedDate
            val folioNo = mBinder.edtChooseFolio.text.toString()
            val selectedFolio = portfolioList.find { it.folioNo == folioNo }
            if (selectedFolio != null) {
                if (selectedFolio.sipDetails?.isNotEmpty() == true) {
                    sipDetail = selectedFolio.sipDetails.find { it.startDate == item.startDate }
                    if (sipDetail != null) {
                        if (sipDetail?.amount != null) {
                            mBinder.amount = sipDetail?.amount
                        } else {
                            mBinder.amount = "0"
                        }
                    } else {
                        mBinder.amount = "0"
                    }
                }
            }
        }
    }

    mBinder.btnInvest.setOnClickListener {
        it.dismissKeyboard()
        val folio = mBinder.edtChooseFolio.text.toString()
        val date = mBinder.edtStartDate.text.toString()

        if (sipDetail != null) {
            mDialog?.dismiss()
            sipDetail?.transId?.let { it1 -> onStop?.invoke(it1, folio, date) }
        }
    }

    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
        it.dismissKeyboard()
    }

    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}

fun Context.signatureDialog(btnDigitally: (() -> Unit), btnPhysically: (() -> Unit)) {
    val mBinder = com.tarrakki.databinding.SignatureDialogBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.llSignDigitally.setOnClickListener {
        mDialog.dismiss()
        btnDigitally.invoke()
    }
    mBinder.llSignPhysically.setOnClickListener {
        mDialog.dismiss()
        btnPhysically.invoke()
    }
    mBinder.ivClose.setOnClickListener { mDialog.dismiss() }
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

fun Context.isInvestDialogValid(minSIPAmount: BigInteger,
                                minLumsumpAmount: BigInteger,
                                sipAmount: BigInteger,
                                lumpsumAmount: BigInteger): Boolean {
    if (lumpsumAmount == BigInteger.ZERO && sipAmount == BigInteger.ZERO) {
        this.simpleAlert("Please enter either the lumpsum or the SIP amount first.")
        return false
    }
    if (lumpsumAmount != BigInteger.ZERO) {
        if (lumpsumAmount % BigInteger.valueOf(10) == BigInteger.ZERO) {
            if (lumpsumAmount < minLumsumpAmount) {
                this.simpleAlert("The lumpsum amount must be greater than or equal to ${minLumsumpAmount.toDouble().toCurrency()}.")
                return false
            }
        } else {
            this.simpleAlert("The lumpsum amount should be a multiple of 10.")
            return false
        }

    }
    if (sipAmount != BigInteger.ZERO) {
        if (sipAmount % BigInteger.valueOf(10) == BigInteger.ZERO) {
            if (sipAmount < minSIPAmount) {
                this.simpleAlert("The SIP amount must be greater than or equal to ${minSIPAmount.toDouble().toCurrency()}.")
                return false
            }
        } else {
            this.simpleAlert("The SIP amount should be a multiple of 10.")
            return false
        }
    }
    return true
}

fun Context.isLumpsumAmountValid(minLumsumpAmount: BigInteger,
                                 lumpsumAmount: BigInteger): Boolean {
    if (lumpsumAmount != BigInteger.ZERO) {
        if (lumpsumAmount % BigInteger.valueOf(10) == BigInteger.ZERO) {
            if (lumpsumAmount < minLumsumpAmount) {
                this.simpleAlert("The lumpsum amount must be greater than or equal to ${minLumsumpAmount.toDouble().toCurrency()}.")
                return false
            }
        } else {
            this.simpleAlert("The lumpsum amount should be a multiple of 10.")
            return false
        }
    }
    return true
}

fun Context.isSIPAmountValid(minSIPAmount: BigInteger,
                             sipAmount: BigInteger): Boolean {
    if (sipAmount != BigInteger.ZERO) {
        if (sipAmount % BigInteger.valueOf(10) == BigInteger.ZERO) {
            if (sipAmount < minSIPAmount) {
                this.simpleAlert("The SIP amount must be greater than or equal to ${minSIPAmount.toDouble().toCurrency()}.")
                return false
            }
        } else {
            this.simpleAlert("The SIP amount should be a multiple of 10.")
            return false
        }
    }
    return true
}

fun Context.isLumpsumAndSIPAmountValid(sipAmount: BigInteger,
                                       lumpsumAmount: BigInteger): Boolean {
    if (lumpsumAmount == BigInteger.ZERO && sipAmount == BigInteger.ZERO) {
        this.simpleAlert("Please enter either the lumpsum or the SIP amount first.")
        return false
    }
    return true
}

fun Context.isAmountValid(amount: BigInteger): Boolean {
    return if (amount != BigInteger.ZERO) {
        /*if (amount % BigInteger.valueOf(10) != BigInteger.ZERO) {
            this.simpleAlert("The redemption amount can not be greater than the current value of the selected folio.")
            return false
        }*/
        return true
    } else {
        this.simpleAlert("Please enter valid amount.")
        return false
    }
}

fun getOrdinalFormat(num: Int): String {
    val suffix = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    val m = num % 100
    return num.toString() + suffix[if (m > 3 && m < 21) 0 else m % 10]
}