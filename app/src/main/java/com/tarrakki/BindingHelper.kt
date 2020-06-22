@file:Suppress("USELESS_ELVIS")

package com.tarrakki

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.tarrakki.api.model.*
import com.tarrakki.databinding.*
import com.tarrakki.module.debitcart.ApplyForDebitCartFragment
import com.tarrakki.module.debitcart.DebitCartInfoFragment
import com.tarrakki.module.funddetails.FundDetailsFragment
import com.tarrakki.module.funddetails.ITEM_ID
import com.tarrakki.module.portfolio.fragments.DirectInvestmentFragment
import com.tarrakki.speedometer.SpeedView
import com.tarrakki.speedometer.components.Section
import com.tarrakki.speedometer.components.indicators.ImageIndicator
import net.cachapa.expandablelayout.ExpandableLayout
import org.greenrobot.eventbus.EventBus
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setAutoWrapContentPageAdapter
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.events.ShowECutOffTimeDialog
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import org.supportcompact.widgets.DividerItemDecorationNoLast
import org.supportcompact.widgets.InputFilterMinMax
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

const val IS_FROM_FORGOT_PASSWORD = "is_from_forgot_password"
const val IS_FROM_ACCOUNT = "is_from_account"
const val IS_FROM_INTRO = "is_from_Intro"
const val IS_FROM_BANK_ACCOUNT = "is_from_bank_account"
const val IS_FROM_COMLETE_REGISTRATION = "is_from_complete_registration"

@BindingAdapter("riskLevel")
fun setRiskLevel(speedView: SpeedView, riskLevel: Int) {
    if (riskLevel == 0) {
        return
    }
    speedView.layoutParams.height = ((App.INSTANCE.resources.displayMetrics.widthPixels - 48f.convertToPx()) / 2).toInt()
    speedView.requestLayout()
    speedView.markWidth = 30.toFloat()
    ContextCompat.getDrawable(App.INSTANCE, R.drawable.indicator)?.let {
        val imageIndicator = ImageIndicator(App.INSTANCE, it)
        speedView.indicator = imageIndicator
    }
    speedView.sections.clear()
    speedView.addSections(Section(0f, .2f, App.INSTANCE.color(R.color.conservative), speedView.dpTOpx(30f))
            , Section(.2f, .4f, App.INSTANCE.color(R.color.moderately_conservative), speedView.dpTOpx(30f))
            , Section(.4f, .6f, App.INSTANCE.color(R.color.balanced), speedView.dpTOpx(30f))
            , Section(.6f, .8f, App.INSTANCE.color(R.color.moderately_aggressive), speedView.dpTOpx(30f))
            , Section(.8f, 1f, App.INSTANCE.color(R.color.aggressive), speedView.dpTOpx(30f)))
    speedView.setSpeedAt(riskLevel.toFloat() - 10)
}


@BindingAdapter("riskLevel")
fun setRiskLevel(speedView: SpeedView, riskLevel: Float) {
    if (riskLevel == 0f) {
        return
    }
    speedView.layoutParams.height = ((App.INSTANCE.resources.displayMetrics.widthPixels - 48f.convertToPx()) / 2).toInt()
    speedView.requestLayout()
    speedView.markWidth = 30.toFloat()
    ContextCompat.getDrawable(App.INSTANCE, R.drawable.indicator)?.let {
        val imageIndicator = ImageIndicator(App.INSTANCE, it)
        speedView.indicator = imageIndicator
    }
    speedView.sections.clear()
    speedView.addSections(Section(0f, .2f, App.INSTANCE.color(R.color.conservative), speedView.dpTOpx(30f))
            , Section(.2f, .4f, App.INSTANCE.color(R.color.moderately_conservative), speedView.dpTOpx(30f))
            , Section(.4f, .6f, App.INSTANCE.color(R.color.balanced), speedView.dpTOpx(30f))
            , Section(.6f, .8f, App.INSTANCE.color(R.color.moderately_aggressive), speedView.dpTOpx(30f))
            , Section(.8f, 1f, App.INSTANCE.color(R.color.aggressive), speedView.dpTOpx(30f)))
    speedView.setSpeedAt(riskLevel)
}

@BindingAdapter("redirectToFundDetails")
fun openFundDetails(txt: TextView, fundId: Int?) {
    txt.setOnClickListener {
        val mContext: Context? = txt.context
        if (mContext is androidx.fragment.app.FragmentActivity) {
            mContext.startFragment(FundDetailsFragment.newInstance(Bundle().apply {
                putString(ITEM_ID, "${fundId}")
            }), R.id.frmContainer)
        }
    }
}

@BindingAdapter("targetYear")
fun setTargetYear(txt: TextView, question: RiskAssessmentQuestionsApiResponse.Data.Option?) {

    txt.setOnClickListener {
        val years = arrayListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 1..30) {
            years.add(cal.get(Calendar.YEAR).toString())
            cal.add(Calendar.YEAR, 1)
        }
        it?.context?.showListDialog("Select Target Year", years) { item: String ->
            question?.targetYear = item
        }
    }
}

@BindingAdapter(value = ["setAdapterH", "isHome"], requireAll = false)
fun setAdapterH(view: androidx.recyclerview.widget.RecyclerView, homeItems: ArrayList<WidgetsViewModel>?, isHome: Boolean) {
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
fun setAdapterH(view: androidx.recyclerview.widget.RecyclerView, adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>?) {
    val manager = androidx.recyclerview.widget.LinearLayoutManager(view.context)
    manager.orientation = androidx.recyclerview.widget.RecyclerView.HORIZONTAL
    view.layoutManager = manager
    view.adapter = adapter
}

@BindingAdapter(value = ["setAdapterV"], requireAll = false)
fun setAdapterV(view: androidx.recyclerview.widget.RecyclerView, adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>?) {
    val manager = androidx.recyclerview.widget.LinearLayoutManager(view.context)
    manager.orientation = androidx.recyclerview.widget.RecyclerView.VERTICAL
    view.layoutManager = manager
    view.adapter = adapter
}


@BindingAdapter(value = ["setAdapterV"], requireAll = false)
fun setAdapterV(view: androidx.recyclerview.widget.RecyclerView, itemList: ArrayList<String>?) {
    itemList?.let {
        val manager = androidx.recyclerview.widget.LinearLayoutManager(view.context)
        manager.orientation = androidx.recyclerview.widget.RecyclerView.VERTICAL
        view.layoutManager = manager
        view.setUpRecyclerView(R.layout.row_cut_off_time_fund_list_item, it) { item: String, binder: RowCutOffTimeFundListItemBinding, position: Int ->
            binder.fund = item
            binder.executePendingBindings()
        }
    }
}

@BindingAdapter("dividerH", requireAll = false)
fun setDividerHorizontal(rv: androidx.recyclerview.widget.RecyclerView, drawable: Drawable? = null) {
    val divider = DividerItemDecorationNoLast(rv.context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL)
    drawable?.let {
        divider.setDrawable(it)
    }
    rv.addItemDecoration(divider)
}

@BindingAdapter("dividerV", requireAll = false)
fun setDividerVertical(rv: androidx.recyclerview.widget.RecyclerView, drawable: Drawable? = null) {
    val divider = DividerItemDecorationNoLast(rv.context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL)
    drawable?.let {
        divider.setDrawable(it)
    }
    rv.addItemDecoration(divider)
}

@BindingAdapter("hasLink")
fun setHasLink(txt: TextView, hasLink: Boolean) {
    txt.movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("applyForDebitCart")
fun applyForDebitCart(txt: TextView, folioData: ArrayList<FolioData>?) {
    txt.setOnClickListener {
        val mContext = txt.context
        if (mContext is androidx.fragment.app.FragmentActivity) {
            getAddressAmountAPI().observe(mContext, androidx.lifecycle.Observer {addressAmountData ->
                /*TarrakkiSingleton.getInstance().debitCardAddress = it.data.userAddress
                TarrakkiSingleton.getInstance().debitCardAmount = it.data.cashfreeAmount.toString()
                startFragment(ApplyForDebitCartFragment.newInstance(), R.id.frmContainer)*/
                mContext.startFragment(DebitCartInfoFragment.newInstance(), R.id.frmContainer)
                folioData?.let { EventBus.getDefault().postSticky(it) }
                addressAmountData?.let { EventBus.getDefault().postSticky(addressAmountData) }
            })
            /*mContext.startFragment(DebitCartInfoFragment.newInstance(), R.id.frmContainer)
            folioData?.let { EventBus.getDefault().postSticky(it) }*/
        }
    }
}

@BindingAdapter("enableNestedScrollView")
fun enableNestedScrollView(rv: androidx.recyclerview.widget.RecyclerView, enable: Boolean) {
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
        Glide.with(img).load(it/*ApiClient.IMAGE_BASE_URL.plus(it)*/).into(img)
    }
}

@BindingAdapter("profile")
fun setProfile(ivProfile: ImageView, url: String?) {
    url?.let {
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.ic_profile_default)
        requestOptions.error(R.drawable.ic_profile_default)
        Glide.with(ivProfile)
                .setDefaultRequestOptions(requestOptions)
                .load(it/*ApiClient.IMAGE_BASE_URL.plus(it)*/)
                .into(ivProfile)
    }
}

@BindingAdapter("imgUrl")
fun setIndicator(img: ImageView, url: Uri?) {
    url?.let {
        Glide.with(img).load(it).into(img)
    }
}

/*@BindingAdapter("imgUrl")
fun setIndicator(img: PhotoView, url: String?) {
    url?.let {
        //Glide.with(img).load(it).into(img)
        *//*Glide.with(img).asBitmap().load(ApiClient.IMAGE_BASE_URL.plus(it)).listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                return true
            }

            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                resource?.let { img.setImage(ImageSource.bitmap(it)) }
                return false
            }
        }).submit()*//*
    }
}*/

@BindingAdapter("imageDialog")
fun showImageDialog(img: ImageView, imgUrl: String?) {
    img.setOnClickListener {
        imgUrl?.let {
            img.context?.showImageDialog(it)
        }
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

@BindingAdapter(value = ["copyToClipBoard"], requireAll = false)
fun applyCurrencyFormat(txt: TextView, enable: Boolean?) {
    if (enable == true)
        txt.setOnLongClickListener {
            val clipboard = txt.context?.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager?
            val clip = ClipData.newPlainText("Message copied", txt.text)
            clipboard?.primaryClip = clip
            txt.context?.toast("Message copied")
            return@setOnLongClickListener true
        }
}

@BindingAdapter(value = ["price", "anim"], requireAll = false)
fun applyCurrencyFormat(txt: TextView, amount: BigInteger?, anim: Boolean?) {
    applyCurrencyFormat(txt, amount?.toString()?.toDouble(), anim)
}

@BindingAdapter(value = ["price", "anim"], requireAll = false)
fun applyCurrencyFormatDouble(txt: TextView, amount: Double?, anim: Boolean?) {
    applyCurrencyFormat(txt, amount, anim)
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
        //progressBar.setProgressWithAnimation(returnpercentage.toFloat(), 1000)
    } else {
        progressBar.color = App.INSTANCE.color(R.color.colorAccent)
        //progressBar.setProgressWithAnimation(returnpercentage.toFloat() * -1, 1000)
    }
    progressBar.setProgressWithAnimation(100f, 500)
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

fun getTarrakkiDir(): File {
    val root = Environment.getExternalStorageDirectory().absolutePath + "/${App.INSTANCE.getString(R.string.app_name)}"
    val mFile = File(root)
    if (!mFile.exists()) {
        mFile.mkdirs()
    }
    return mFile
}

fun getFileDownloadDir(): String {
    val root = Environment.getExternalStorageDirectory().absolutePath + "/${App.INSTANCE.getString(R.string.app_name)}/Download"
    val mFile = File(root)
    if (!mFile.exists()) {
        mFile.mkdirs()
    }
    return mFile.absolutePath
}

// url = file path or whatever suitable URL you want.
fun getMimeType(url: String): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

@BindingAdapter(value = ["openSentFile"])
fun openSentFile(txt: TextView, fileName: String?) {
    txt.setOnClickListener { v ->
        try {
            val file = File(getFileDownloadDir(), fileName)
            // Get URI and MIME type of file
            val uri = file.toUri()//Uri.fromFile(file)
            val mime = App.INSTANCE.contentResolver.getType(uri)
            // Open file with user selected app
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.setDataAndType(uri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            v.context?.startActivity(Intent.createChooser(intent, "Open With"))
        } catch (e: Exception) {
            e.printStackTrace()
            v.context?.toast(e.message ?: "")
        }
    }
}

@BindingAdapter(value = ["openDownloadedFile"])
fun openDownloadedFile(txt: TextView, fileName: String?) {
    txt.setOnClickListener { v ->
        try {
            v.context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getFileDownloadDir().plus("/").plus(fileName))))
        } catch (e: Exception) {
            e.printStackTrace()
            v.context?.toast(e.message ?: "")
        }
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
    txt.movementMethod = LinkMovementMethod.getInstance()
    txt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(html)
    }
    //txt.movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("HTML")
fun setWebviewData(txt: WebView, txtHtml: String?) {
    val txtHtmlNotNull = txtHtml ?: ""
    txt.loadDataWithBaseURL("file:///android_res/", txtHtmlNotNull.toHTMl("lato_regular.ttf"), "text/html", "utf-8", null)
    txt.webViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return onPageRequest(view, "${url}")
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return onPageRequest(view, "${request.url}")
        }

        fun onPageRequest(view: WebView, url: String): Boolean {
            return when {
                url.startsWith("tel:") -> {
                    view.context?.initiateCall(url)
                    true
                }
                url.startsWith("mailto:") -> {
                    view.context?.sendEmail(url.substring(7))
                    true
                }
                else -> {
                    // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                    try {
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            view.context?.startActivity(this)
                        }
                    } catch (e: Exception) {
                        EventBus.getDefault().post(ShowError("${e.message}"))
                    }
                    return true
                }
            }
        }
    }
}

@BindingAdapter("setQuestionDetails")
fun setQuestionDetails(txt: WebView, txtHtml: String?) {
    val txtHtmlNotNull = txtHtml ?: ""
    var htmlData = App.INSTANCE.resources.openRawResource(R.raw.question_details).bufferedReader().use { it.readText() }
    htmlData = htmlData.replace("%s", txtHtmlNotNull)
    txt.loadDataWithBaseURL("file:///android_res/", htmlData, "text/html", "utf-8", null)
    txt.webViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return onPageRequest(view, "${url}")
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return onPageRequest(view, "${request.url}")
        }

        fun onPageRequest(view: WebView, url: String): Boolean {
            return when {
                url.startsWith("tel:") -> {
                    view.context?.initiateCall(url)
                    true
                }
                url.startsWith("mailto:") -> {
                    view.context?.sendEmail(url.substring(7))
                    true
                }
                else -> {
                    // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                    try {
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            view.context?.startActivity(this)
                        }
                    } catch (e: Exception) {
                        EventBus.getDefault().post(ShowError("${e.message}"))
                    }
                    return true
                }
            }
        }
    }
}

private fun Context.sendEmail(add: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(add))
    try {
        startActivity(Intent.createChooser(intent, "Send mail..."))
    } catch (ex: android.content.ActivityNotFoundException) {
        EventBus.getDefault().post(ShowError("There are no email clients installed."))
    }

}

private fun Context.initiateCall(url: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(url)
        startActivity(intent)
    } catch (e: android.content.ActivityNotFoundException) {
        EventBus.getDefault().post(ShowError("${e.message}"))
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

@BindingAdapter("color")
fun setFont(textView: TextView, color: Int) {
    textView.setTextColor(textView.context.color(color))
}

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

    textview.setTextColor(textview.context.color(if (finalValue >= 0) R.color.colorAccent else R.color.white))

    valueAnimator.addUpdateListener { it ->
        textview.text = returnType + it.animatedValue.toString().toDouble().toReturnAsPercentage()
    }
    valueAnimator.start()
}

fun Context.showCutOffTimeDialog(data: ShowECutOffTimeDialog) {
    val mBinder = DialoCutOffTimeAlertBinding.inflate(LayoutInflater.from(this))
    mBinder.data = data
    mBinder.executePendingBindings()
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
    }
    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
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
            val n = goal.getN()
            if (n != null) {
                try {
                    val pvAmount = "${mBinder.lumpsum}".replace(",", "")
                    val amount = "${if (goal.isCustomInvestment()) goal.getInvestmentAmount() else goal.getPMT()?.ans}".replace(",", "")
                    if (!TextUtils.isEmpty(amount) && !TextUtils.isEmpty(pvAmount) && pvAmount.toDouble() > amount.toDouble()) {
                        EventBus.getDefault().post(ShowError(this.getString(R.string.alert_valid_goal_lumpsum)))
                    } else if (TextUtils.isEmpty(mBinder.durations) || "${mBinder.durations}".toDouble() !in n.minValue..n.maxValue.toDouble()) {
                        val msg = this.getString(R.string.alert_valid_years)
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

fun Context.investDialog(fundId: Int, minSIPAmount: BigInteger, minLumsumpAmount: BigInteger, bseData: BSEData?, onInvest: ((amountLumpsum: String, amountSIP: String, fundId: Int) -> Unit)? = null) {
    val mBinder = DialogInvestBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()
    bseData?.let {
        mBinder.enableLumpsum = "Y".equals(it.lumpsumAllowed, true)
        mBinder.enableSIP = "Y".equals(it.sipAllowed, true)
    }
    mBinder.btnInvest.setOnClickListener {
        val lumpsumAmount = mBinder.edtLumpsum.text.toString().toCurrencyBigInt()
        val sipAmount = mBinder.edtSIPAmount.text.toString().toCurrencyBigInt()
        it.dismissKeyboard()
        if (this.isInvestDialogValid(minSIPAmount, minLumsumpAmount, sipAmount, lumpsumAmount, bseData)) {
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

fun Context.investCartDialog(item: CartData.Data.OrderLine, onInvest: ((amountLumpsum: String, amountSIP: String) -> Unit)? = null) {
    val mBinder = DialogInvestBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()

    mBinder.lumpsum = item.lumpsumAmount.replace("\u20B9", "")
    mBinder.investment = item.sipAmount.replace("\u20B9", "")
    mBinder.executePendingBindings()

    mBinder.edtLumpsum.applyCurrencyFormatPositiveOnly()
    mBinder.edtSIPAmount.applyCurrencyFormatPositiveOnly()
    mBinder.edtLumpsum.setSelection(mBinder.edtLumpsum.length())
    if (item.bseData != null) {
        mBinder.enableSIP = "Y".equals(item.bseData.sipAllowed, true)
        mBinder.enableLumpsum = "Y".equals(item.bseData.lumpsumAllowed, true)
    } else {
        mBinder.enableSIP = true
        mBinder.enableLumpsum = true
    }

    mBinder.btnInvest.setText(R.string.update)
    mBinder.btnInvest.setOnClickListener {
        val lumpsumAmount = mBinder.edtLumpsum.text.toString().toCurrencyBigInt()
        val sipAmount = mBinder.edtSIPAmount.text.toString().toCurrencyBigInt()
        it.dismissKeyboard()
        if (isLumpsumAndSIPAmountValid(sipAmount, lumpsumAmount)) {
            val minLumpsum = if (item.bseData?.isAdditional == true) {
                item.additionalMinLumpsum
            } else {
                item.validminlumpsumAmount
            }
            val minSIPAmount = if (item.bseData?.isAdditional == true) {
                item.validminSIPAmount
            } else {
                item.validminSIPAmount
            }
            if (this.isInvestDialogValid(minSIPAmount, minLumpsum, sipAmount, lumpsumAmount, item.bseData)) {
                mDialog.dismiss()
                onInvest?.invoke(lumpsumAmount.toString(), sipAmount.toString())
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
        if (this.isInvestDialogValid(BigInteger.valueOf(500), BigInteger.valueOf(5000), sipAmount, lumpsumAmount)) {
            mDialog.dismiss()
            onInvest?.invoke(thirdLevelCategory, lumpsumAmount, sipAmount)
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
                                   minAmountLumpsum: BigInteger, minAmountSIP: BigInteger, bseData: BSEData?,
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
        this.showCustomListDialog("Select Folio", folioList) { item ->
            mBinder.folio = item
        }
    }

    mBinder.btnInvest.setOnClickListener {
        val folioData = portfolioList.firstOrNull { it.folioNo == mBinder.folio }
        val lumpsumAmount = mBinder.edtLumpsum.text.toString().toCurrencyBigInt()
        val sipAmount = mBinder.edtSIPAmount.text.toString().toCurrencyBigInt()
        val minLumpsum = if (mBinder.rbCurrent.isChecked && folioData != null) {
            folioData.additionalLumpsumMinAmt
        } else {
            minAmountLumpsum
        }
        val minSIPAmount = if (mBinder.rbCurrent.isChecked && folioData != null) {
            folioData.additionalSIPMinAmt
        } else {
            minAmountSIP
        }
        it.dismissKeyboard()
        bseData?.isAdditional = mBinder.rbCurrent.isChecked
        if (this.isInvestDialogValid(minSIPAmount, minLumpsum, sipAmount, lumpsumAmount, bseData)) {
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
                                                  folioId: String,
                                                  allRedeem: String,
                                                  units: String) -> Unit)? = null) {
    val mBinder = DialogRedeemPortfolioBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtTotalInvestedAmount.applyCurrencyInfiniteDecimalFormatPositiveOnly()
    mBinder.edtAmount.applyCurrencyInfiniteDecimalFormatPositiveOnly()

    val folioList = portfolioList.map { it.folioNo } as ArrayList

    if (folioList.isNotEmpty()) {
        mBinder.folio = folioList[0]
        val folio = portfolioList.find { it.folioNo == folioList[0] }
        mBinder.investmentAmount = folio?.units
        mBinder.isSingleFolio = folioList.size == 1
    } else {
        mBinder.isSingleFolio = true
    }

    mBinder.edtAmount.setOnClickListener {
        if (mBinder.chkAmount.isChecked) {
            this.simpleAlert("Please uncheck the all withdraw option first.")
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
        this.showCustomListDialog("Select Folio", folioList) { item ->
            mBinder.folio = item
            val selectedAmount = portfolioList.find { it.folioNo == item }
            if (selectedAmount != null) {
                mBinder.investmentAmount = selectedAmount.units/*(selectedAmount.cValue.toDouble() / todayNAV).roundOff().toString()*/
                mBinder.chkAmount.isChecked = false
            }
        }
    }

    mBinder.btnInvest.setOnClickListener {
        it.dismissKeyboard()
        val units = mBinder.edtAmount.text.toString()
        val folioNo = mBinder.edtChooseFolio.text.toString()
        val folioId = portfolioList.find { it.folioNo == folioNo }?.folioId
        if (isAmountValid(units.toCurrencyBigDecimal())) {
            if (units.toCurrencyBigDecimal() <= "${mBinder.investmentAmount}".toCurrencyBigDecimal()) {
                mDialog.dismiss()
                val isRedeem = if (mBinder.chkAmount.isChecked) {
                    "Y"
                } else {
                    "N"
                }
                onRedeem?.invoke(folioNo, "$folioId", isRedeem, units)
            } else {
                this.simpleAlert(getString(R.string.greater_units))
            }
        } else {
            this.simpleAlert(getString(R.string.alert_req_units))
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

fun androidx.fragment.app.Fragment.redeemFundTarrakkiZyaadaDialog(portfolioList: MutableList<FolioData>,
                                                                  onRedeem: ((portfolioNo: String,
                                                                              folioId: String,
                                                                              allRedeem: String,
                                                                              units: String) -> Unit)? = null,
                                                                  onInstaRedeem: ((portfolioNo: String,
                                                                                   folioId: String,
                                                                                   amount: String,
                                                                                   allRedeem: String) -> Unit)? = null) {
    context?.let { mContext ->

        val mBinder = DialogRedeemTarrakkiZyaadaBinding.inflate(LayoutInflater.from(mContext))
        val mDialog = AlertDialog.Builder(mContext).setView(mBinder.root).create()
        mBinder.edtTotalInvestedAmount.applyCurrencyInfiniteDecimalFormatPositiveOnly()
        mBinder.edtAmount.applyCurrencyInfiniteDecimalFormatPositiveOnly()
        var instaAmount = 0.0
        val folioData = Observer<SchemeDetails> {
            it?.data?.let {
                val instaRedeemEligible = it.instaRedeemEligible == "Y" && (it.instaAmount?.toDoubleOrNull()
                        ?: 0.0) > 0.0
                instaAmount = it.instaAmount?.toDoubleOrNull() ?: 0.0
                mBinder.isInstantRedeem = instaRedeemEligible
                mBinder.switchOnOff.isEnabled = instaRedeemEligible
                mBinder.switchOnOff.isChecked = instaRedeemEligible
                if (!instaRedeemEligible) {
                    mContext.let {
                        it.simpleAlert(it.getString(R.string.lower_amount_for_insta_redeem))
                    }
                }
            }
            mBinder.mProgress.visibility = View.GONE
        }

        App.INSTANCE.isRefreshing.observe(this, Observer {
            mBinder.mProgress.visibility = View.GONE
        })

        val folioList = portfolioList.map { it.folioNo } as ArrayList
        if (folioList.isNotEmpty()) {
            mBinder.folio = folioList[0]
            val folio = portfolioList.find { it.folioNo == folioList[0] }
            mBinder.investmentAmount = folio?.units
            mBinder.isSingleFolio = folioList.size == 1
            //val hasMoreThenFiveHounred = (folio?.cValue?.toDouble() ?: 0.0) >= 100.00
            mBinder.isInstantRedeem = false
            mBinder.switchOnOff.isEnabled = false
            mBinder.mProgress.visibility = View.VISIBLE
            getFolioDetails("${folio?.folioNo}").observe(this, folioData)
        } else {
            mBinder.isSingleFolio = true
        }
        mBinder.executePendingBindings()
        mBinder.switchOnOff.setOnCheckedChangeListener { buttonView, isChecked ->
            portfolioList.find { it.folioNo == mBinder.edtChooseFolio.text.toString() }?.let { folio ->
                if (isChecked) {
                    //mBinder?.edtTotalInvestedAmount.applyCurrencyDecimalFormatPositiveOnlyWithoutRoundOff()
                    //mBinder?.edtAmount.applyCurrencyDecimalFormatPositiveOnlyWithoutRoundOff()
                    mBinder.investmentAmount = "$instaAmount"
                    val currentAmount = (folio.currentValue ?: 0.0) / 2
                    mBinder.tvNote.setText(if (instaAmount > currentAmount) R.string.upto_rs_50_000_or_90 else R.string.upto_rs_50_000_or_50)
                } else {
                    //mBinder.edtTotalInvestedAmount.applyCurrencyInfiniteDecimalFormatPositiveOnly()
                    //mBinder.edtAmount.applyCurrencyInfiniteDecimalFormatPositiveOnly()s
                    mBinder.investmentAmount = folio.units
                }
            }
            mBinder.edtAmount.setText("")
            mBinder.chkAmount.isChecked = false
            mBinder.isInstantRedeem = isChecked
            mBinder.tvNote.visibility = if (isChecked) View.VISIBLE else View.GONE
            mBinder.executePendingBindings()
        }

        mBinder.edtAmount.setOnClickListener {
            if (mBinder.chkAmount.isChecked) {
                mContext.simpleAlert("Please uncheck the all withdraw option first.")
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
            mContext.showCustomListDialog("Select Folio", folioList) { item ->
                mBinder.folio = item
                val selectedAmount = portfolioList.find { it.folioNo == item }
                if (selectedAmount != null) {
                    mBinder.investmentAmount = selectedAmount.units
                    mBinder.chkAmount.isChecked = false
                    mBinder.isInstantRedeem = false
                    mBinder.switchOnOff.isEnabled = false
                    mBinder.mProgress.visibility = View.VISIBLE
                    getFolioDetails("${selectedAmount.folioNo}").observe(this, folioData)
                }
            }
        }

        mBinder.btnInvest.setOnClickListener {
            it.dismissKeyboard()
            if (mBinder.switchOnOff.isChecked) {
                val amount = mBinder.edtAmount.text.toString()
                val folioNo = mBinder.edtChooseFolio.text.toString()
                val folioId = portfolioList.find { it.folioNo == folioNo }?.folioId
                if (isAmountValid(amount.toCurrencyBigDecimal())) {
                    if (amount.toCurrencyBigDecimal().toDouble() >= 100.00) {
                        if (amount.toCurrencyBigDecimal() <= "${mBinder.investmentAmount}".toCurrencyBigDecimal()) {
                            if (amount.toCurrencyBigDecimal().toDouble() % 1.00 == 0.00) {
                                mDialog.dismiss()
                                val isRedeem = if (mBinder.chkAmount.isChecked) {
                                    "F"
                                } else {
                                    "P"
                                }
                                onInstaRedeem?.invoke(folioNo, "$folioId", amount, isRedeem)
                            } else {
                                mContext.simpleAlert(mContext.getString(R.string.multi_insta_redeem_amount))
                            }
                        } else {
                            mContext.simpleAlert("The redemption amount can not be greater than the total amount of the selected folio.")
                        }
                    } else {
                        mContext.simpleAlert(mContext.getString(R.string.min_insta_redeem_amount))
                    }
                } else {
                    mContext.simpleAlert(getString(R.string.alert_req_amount))
                }
            } else {
                val units = mBinder.edtAmount.text.toString()
                val folioNo = mBinder.edtChooseFolio.text.toString()
                val folioId = portfolioList.find { it.folioNo == folioNo }?.folioId
                if (isAmountValid(units.toCurrencyBigDecimal())) {
                    if (units.toCurrencyBigDecimal() <= "${mBinder.investmentAmount}".toCurrencyBigDecimal()) {
                        mDialog.dismiss()
                        val isRedeem = if (mBinder.chkAmount.isChecked) {
                            "Y"
                        } else {
                            "N"
                        }
                        onRedeem?.invoke(folioNo, "$folioId", isRedeem, units)
                    } else {
                        mContext.simpleAlert(mContext.getString(R.string.greater_units))
                    }
                } else {
                    mContext.simpleAlert(getString(R.string.alert_req_units))
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
                    mBinder.startDate = startDateList[0].sipDay
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
        this.showCustomListDialog("Select Folio", folioList) { item ->
            mBinder.folio = item
            val selectedFolio = portfolioList.find { it.folioNo == item }
            if (selectedFolio != null) {
                startDateList = selectedFolio.sipDetails as ArrayList<SIPDetails>
                if (startDateList.isNotEmpty()) {
                    mBinder.startDate = startDateList[0].sipDay
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
            mBinder.startDate = item.sipDay
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
        val date = sipDetail?.convertedDate ?: ""//mBinder.edtStartDate.text.toString()

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

fun Context.updateEmailOrMobile(isEmailUpdate: Boolean = true, updateField: String = "", onUpdate: ((String) -> Unit)) {
    val mBinder = DialogUpdateEmailPhoneBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.isEmailUpdate = isEmailUpdate
    mBinder.email = updateField
    mBinder.mobile = updateField
    mBinder.executePendingBindings()
    if (isEmailUpdate) {
        mBinder.edtEmail.requestFocus()
        mBinder.edtEmail.setSelection(mBinder.edtEmail.length())
    } else {
        mBinder.edtMobile.requestFocus()
        mBinder.edtMobile.setSelection(mBinder.edtMobile.length())
    }
    mBinder.btnUpdate.setOnClickListener {
        if (isEmailUpdate) {
            when {
                TextUtils.isEmpty(mBinder.edtEmail.text.trim()) -> simpleAlert(getString(R.string.pls_enter_email_address)) {
                    Handler().postDelayed({
                        mBinder.edtEmail.requestFocus()
                        mBinder.edtEmail.setSelection(mBinder.edtEmail.length())
                    }, 100)
                }
                !"${mBinder.edtEmail.text.trim()}".isEmail() -> simpleAlert(getString(R.string.pls_enter_valid_email_address)) {
                    Handler().postDelayed({
                        mBinder.edtEmail.requestFocus()
                        mBinder.edtEmail.setSelection(mBinder.edtEmail.length())
                    }, 100)
                }
                else -> {
                    mDialog.dismiss()
                    onUpdate.invoke("${mBinder.edtEmail.text.trim()}".toLowerCase())
                }
            }
        } else {
            when {
                TextUtils.isEmpty(mBinder.edtMobile.text.trim()) -> simpleAlert(getString(R.string.pls_enter_mobile_number)) {
                    Handler().postDelayed({
                        mBinder.edtMobile.requestFocus()
                        mBinder.edtMobile.setSelection(mBinder.edtMobile.length())
                    }, 100)
                }
                !"${mBinder.edtMobile.text.trim()}".isValidMobile() -> simpleAlert(getString(R.string.pls_enter_valid_indian_mobile_number)) {
                    Handler().postDelayed({
                        mBinder.edtMobile.requestFocus()
                        mBinder.edtMobile.setSelection(mBinder.edtMobile.length())
                    }, 100)
                }
                else -> {
                    mDialog.dismiss()
                    onUpdate.invoke("${mBinder.edtMobile.text.trim()}".toLowerCase())
                }
            }
        }
    }
    mBinder.ivClose.setOnClickListener {
        it.dismissKeyboard()
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

fun BigInteger.toNonZeroValue() = if (this == BigInteger.ZERO) BigInteger.ONE else this


fun Context.isInvestDialogValid(minSIPAmount: BigInteger,
                                minLumsumpAmount: BigInteger,
                                sipAmount: BigInteger,
                                lumpsumAmount: BigInteger,
                                bseData: BSEData? = null): Boolean {
    if (lumpsumAmount == BigInteger.ZERO && sipAmount == BigInteger.ZERO) {
        simpleAlert(this.getString(R.string.alert_req_sip_or_lumpsump))
        return false
    } else if (bseData?.isAdditional == false && bseData?.isTarrakkiZyaada == true && lumpsumAmount != BigInteger.ZERO && sipAmount != BigInteger.ZERO) {
        simpleAlert(getString(R.string.tarrakki_zyaada_investment_alert))
        return false
    }
    val lumpsumAmountMultipler = if (bseData?.isAdditional == true) App.piMinimumSubsequentMultiple else App.piMinimumInitialMultiple

    if (lumpsumAmount != BigInteger.ZERO) {
        val amountRange = bseData?.lumpsumList?.filter { "Y".equals(it.lumpsumAllowed, true) }
        if ("Y".equals(bseData?.lumpsumAllowed, true) && bseData?.lumpsumCheckFlag == true && amountRange?.isNotEmpty() == true) {
            if (lumpsumAmount % lumpsumAmountMultipler.toNonZeroValue() == BigInteger.ZERO) {
                val minLumpsum = if (bseData.isAdditional == true) {
                    amountRange.minBy {
                        it.lumpsumAdditionalMinAmount ?: BigInteger.ZERO
                    }?.lumpsumAdditionalMinAmount
                } else {
                    amountRange.minBy { it.minAmount ?: BigInteger.ZERO }?.minAmount
                }
                val maxLumpsumUnlimited = amountRange.find { it.maxAmount ?: BigInteger.ZERO == BigInteger.ZERO }?.maxAmount
                val maxLumpsum = amountRange.maxBy { it.maxAmount ?: BigInteger.ZERO }?.maxAmount
                if (minLumpsum != null && maxLumpsum != null) {
                    if (maxLumpsum == BigInteger.ZERO || maxLumpsumUnlimited == BigInteger.ZERO) {
                        if (lumpsumAmount < minLumpsum) {
                            this.simpleAlert(alertLumpsumMin(minLumpsum.toDouble().toCurrency()))
                            return false
                        }
                    } else {
                        if (lumpsumAmount < minLumpsum || lumpsumAmount > maxLumpsum) {
                            this.simpleAlert(alertLumpsumBetween(minLumpsum.toDouble().toCurrency(), maxLumpsum.toDouble().toCurrency()))
                            return false
                        }
                    }
                } else if (lumpsumAmount < minLumsumpAmount) {
                    this.simpleAlert(alertLumpsumMin(minLumsumpAmount.toDouble().toCurrency()))
                    return false
                }
            } else {
                //this.simpleAlert(getString(R.string.alert_valid_multiple_lumpsum))
                this.simpleAlert(lumpsumMultiplier("$lumpsumAmountMultipler"))
                return false
            }
        } else {
            if (lumpsumAmount % lumpsumAmountMultipler.toNonZeroValue() == BigInteger.ZERO) {
                if (lumpsumAmount < minLumsumpAmount) {
                    this.simpleAlert(alertLumpsumMin(minLumsumpAmount.toDouble().toCurrency()))
                    return false
                }
            } else {
                //his.simpleAlert(getString(R.string.alert_valid_multiple_lumpsum))
                this.simpleAlert(lumpsumMultiplier("$lumpsumAmountMultipler"))
                return false
            }
        }

    }
    if (sipAmount != BigInteger.ZERO) {
        val amountRange = bseData?.sipList?.filter { "Y".equals(it.sipAllowed, true) }
        if ("Y".equals(bseData?.sipAllowed, true) && bseData?.sipCheckFlag == true && amountRange?.isNotEmpty() == true) {

            if (sipAmount % App.additionalSIPMultiplier.toNonZeroValue() == BigInteger.ZERO) {
                val minSip = amountRange.minBy { it.minAmount ?: BigInteger.ZERO }?.minAmount /*if (bseData.isAdditional == true) {
                    amountRange.minBy {
                        it.sipAdditionalMinAmount ?: BigInteger.ZERO
                    }?.sipAdditionalMinAmount
                } else {
                    amountRange.minBy { it.minAmount ?: BigInteger.ZERO }?.minAmount
                }*/
                val maxSipUnlimited = amountRange.find { it.maxAmount ?: BigInteger.ZERO == BigInteger.ZERO }?.maxAmount
                val maxSip = amountRange.maxBy { it.maxAmount ?: BigInteger.ZERO }?.maxAmount
                if (minSip != null && maxSip != null) {
                    if (maxSip == BigInteger.ZERO || maxSipUnlimited == BigInteger.ZERO) {
                        if (sipAmount < minSip) {
                            this.simpleAlert(alertSIPMin(minSip.toDouble().toCurrency()))
                            return false
                        }
                    } else {
                        if (sipAmount < minSip || sipAmount > maxSip) {
                            this.simpleAlert(alertSIPBetween(minSip.toDouble().toCurrency(), maxSip.toDouble().toCurrency()))
                            return false
                        }
                    }
                } else if (sipAmount < minSIPAmount) {
                    this.simpleAlert(alertSIPMin(minSIPAmount.toDouble().toCurrency()))
                    return false
                }
            } else {
                //this.simpleAlert(getString(R.string.alert_valid_multiple_sip))
                this.simpleAlert(SIPMultiplier("${App.additionalSIPMultiplier}"))
                return false
            }

        } else if (sipAmount % App.additionalSIPMultiplier.toNonZeroValue() == BigInteger.ZERO) {
            if (sipAmount < minSIPAmount) {
                this.simpleAlert(alertSIPMin(minSIPAmount.toDouble().toCurrency()))
                return false
            }
        } else {
            //this.simpleAlert(getString(R.string.alert_valid_multiple_sip))
            this.simpleAlert(SIPMultiplier("${App.additionalSIPMultiplier}"))
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
                this.simpleAlert(alertLumpsumMin(minLumsumpAmount.toDouble().toCurrency()))
                return false
            }
        } else {
            this.simpleAlert(getString(R.string.alert_valid_multiple_lumpsum))
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
                this.simpleAlert(alertSIPMin(minSIPAmount.toDouble().toCurrency()))
                return false
            }
        } else {
            this.simpleAlert(getString(R.string.alert_valid_multiple_sip))
            return false
        }
    }
    return true
}

fun Context.isLumpsumAndSIPAmountValid(sipAmount: BigInteger,
                                       lumpsumAmount: BigInteger): Boolean {
    if (lumpsumAmount == BigInteger.ZERO && sipAmount == BigInteger.ZERO) {
        this.simpleAlert(this.getString(R.string.alert_req_sip_or_lumpsump))
        return false
    }
    return true
}

fun isAmountValid(amount: BigDecimal): Boolean = amount > BigDecimal.ZERO

fun getOrdinalFormat(num: Int): String {
    val suffix = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    val m = num % 100
    return num.toString() + suffix[if (m > 3 && m < 21) 0 else m % 10]
}

fun Context.portfolioIntro(list: MutableList<DirectInvestmentFragment.InvestmentPortfolioIntro>, currentPosition: Int? = null) {
    val context = this
    val bottomSheetDialog = this.bottomSheetDialog({
        val mBinder = DialogHowReturnsIntroBinding.inflate(LayoutInflater.from(context))
        customView(mBinder.root)

        mBinder.pagerIntro.setAutoWrapContentPageAdapter(R.layout.row_porfolio_calculates_intro, list as java.util.ArrayList<DirectInvestmentFragment.InvestmentPortfolioIntro>) { binder: RowPorfolioCalculatesIntroBinding, item: DirectInvestmentFragment.InvestmentPortfolioIntro ->
            binder.vm = item
            binder.executePendingBindings()
        }
        mBinder.pageIndicator.setViewPager(mBinder.pagerIntro)
        mBinder.pagerIntro.interval = 7000
        mBinder.pagerIntro.startAutoScroll()
        mBinder.imgDown.setOnClickListener {
            dismiss()
        }
        if (currentPosition != null || currentPosition != -1) {
            Handler().postDelayed({
                mBinder.pagerIntro.currentItem = currentPosition!!
            }, 10)
        }

    }, R.style.AppBottomSheetDialogTheme)
    bottomSheetDialog.show()
}

fun Context.stopFundPortfolioDialog(mySipData: MySipData,
                                    onStop: ((transactionId: Int, folio: String, startDate: String) -> Unit)? = null) {
    val mBinder = DialogStopMySipTransactionBinding.inflate(LayoutInflater.from(this))
    val mDialog = AlertDialog.Builder(this).setView(mBinder.root).create()
    mBinder.edtAmount.applyCurrencyFormatPositiveOnly()
//    val folioList = portfolioList.filter { it.sipDetails?.isNotEmpty() == true }.map { it.folioNo } as ArrayList
//    var startDateList = arrayListOf<SIPDetails>()
//    var sipDetail: SIPDetails? = null

//    if (folioList.isNotEmpty()) {
//        mBinder.folio = folioList[0]
//        val selectedFolio = portfolioList.find { it.folioNo == folioList[0] }
//        if (selectedFolio != null) {
//            if (selectedFolio.sipDetails?.isNotEmpty() == true) {
//                startDateList = selectedFolio.sipDetails as ArrayList<SIPDetails>
//                if (startDateList.isNotEmpty()) {
//                    mBinder.startDate = startDateList[0].sipDay
//                }
//                sipDetail = selectedFolio.sipDetails[0]
//                if (sipDetail != null) {
//                    if (sipDetail.amount != null) {
//                        mBinder.amount = sipDetail.amount
//                    } else {
//                        mBinder.amount = "0"
//                    }
//                } else {
//                    mBinder.amount = "0"
//                }
//            }
//        }
//        mBinder.isSingleFolio = folioList.size == 1
//    } else {
//        mBinder.isSingleFolio = true
//    }
    mBinder.startDate = mySipData.sipStartDate?.toDate()?.day()?.let {
        getOrdinalFormat(it) + " of every month"
    }
    mBinder.amount = "" + mySipData.amount

//    mBinder.edtChooseFolio.setOnClickListener {
//        this.showCustomListDialog("Select Folio", folioList) { item ->
//            mBinder.folio = item
//            val selectedFolio = portfolioList.find { it.folioNo == item }
//            if (selectedFolio != null) {
//                startDateList = selectedFolio.sipDetails as ArrayList<SIPDetails>
//                if (startDateList.isNotEmpty()) {
//                    mBinder.startDate = startDateList[0].sipDay
//                    if (selectedFolio.sipDetails.isNotEmpty()) {
//                        sipDetail = selectedFolio.sipDetails[0]
//                        if (sipDetail != null) {
//                            if (sipDetail?.amount != null) {
//                                mBinder.amount = sipDetail?.amount
//                            } else {
//                                mBinder.amount = "0"
//                            }
//                        } else {
//                            mBinder.amount = "0"
//                        }
//                    }
//
//                }
//            }
//        }
//    }

//    mBinder.edtStartDate.setOnClickListener {
//        this.showCustomListDialog("Select StartDate", startDateList) { item ->
//            mBinder.startDate = item.sipDay
//            val folioNo = mBinder.edtChooseFolio.text.toString()
//            val selectedFolio = portfolioList.find { it.folioNo == folioNo }
//            if (selectedFolio != null) {
//                if (selectedFolio.sipDetails?.isNotEmpty() == true) {
//                    sipDetail = selectedFolio.sipDetails.find { it.startDate == item.startDate }
//                    if (sipDetail != null) {
//                        if (sipDetail?.amount != null) {
//                            mBinder.amount = sipDetail?.amount
//                        } else {
//                            mBinder.amount = "0"
//                        }
//                    } else {
//                        mBinder.amount = "0"
//                    }
//                }
//            }
//        }
//    }

    mBinder.btnInvest.setOnClickListener {
        mDialog?.dismiss()
        mySipData.id?.let { it1 -> mySipData.folioNo?.let { it2 -> mySipData.sipStartDate?.let { it3 -> onStop?.invoke(it1, it2, it3) } } }
    }

    mBinder.tvClose.setOnClickListener {
        mDialog.dismiss()
        it.dismissKeyboard()
    }

    val v: View? = mDialog?.window?.decorView
    v?.setBackgroundResource(android.R.color.transparent)
    mDialog.show()
}
