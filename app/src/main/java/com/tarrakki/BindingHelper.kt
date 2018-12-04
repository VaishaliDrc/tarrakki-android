package com.tarrakki

import android.animation.ValueAnimator
import android.content.Intent
import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.tarrakki.module.goal.Goal
import com.tarrakki.module.investmentstrategies.SelectInvestmentStrategyFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL
import net.cachapa.expandablelayout.ExpandableLayout
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.*
import org.supportcompact.widgets.DividerItemDecorationNoLast
import java.util.*

const val IS_FROM_ACCOUNT = "is_from_account"
const val IS_FROM_BANK_ACCOUNT = "is_from_bank_account"

@BindingAdapter(value = ["setAdapterH"], requireAll = false)
fun setAdapterH(view: RecyclerView, homeItems: ArrayList<WidgetsViewModel>?) {
    view.isFocusable = false
    view.isNestedScrollingEnabled = false
    homeItems?.let {
        view.setUpMultiViewRecyclerAdapter(homeItems) { item, binder, position ->
            binder.setVariable(BR.widget, item)
            binder.executePendingBindings()
            binder.root.setOnClickListener { it ->
                val mContext = it.context
                if (mContext is AppCompatActivity && item is Goal) {
                    mContext.startFragment(InitiateYourGoalFragment.newInstance(Bundle().apply { putSerializable(KEY_GOAL, item) }), R.id.frmContainer)
                    //mContext.startFragment(RecommendedFragment.newInstance(Bundle().apply { putSerializable(KEY_GOAL, item) }), R.id.frmContainer)
                } else if (mContext is AppCompatActivity) {
                    mContext.startFragment(SelectInvestmentStrategyFragment.newInstance(), R.id.frmContainer)
                }
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

@BindingAdapter("expanded")
fun setIndicator(view: ExpandableLayout, value: Boolean) {
    if (value) {
        view.expand(true)
    } else {
        view.collapse(true)
    }
}

@BindingAdapter(value = ["price", "anim"], requireAll = false)
fun applyCurrencyFormat(txt: TextView, amount: Double, anim: Boolean?) {
    if (anim != null && anim) {
        handleTextView((amount % 10).toInt(), amount.toInt(), txt)
        return
    }
    txt.text = amount.toCurrency()
}

@BindingAdapter(value = ["dprice", "anim"], requireAll = false)
fun applyDCurrencyFormat(txt: TextView, amount: Double, anim: Boolean?) {
    if (anim != null && anim) {
        handleTextView((amount % 10), amount, txt)
        return
    }
    txt.text = amount.toDecimalCurrency()
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
fun setHtml(txt: TextView, txtHtml: String) {
    val html = "<!DOCTYPE html><html><body>$txtHtml</body></html>"
    txt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(html)
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
    val returnType = if (finalValue > 0) "+" else "-"
    valueAnimator.addUpdateListener { it ->
        textview.text = returnType.plus(it.animatedValue.toString().toDouble().toDecimalCurrency())
    }
    valueAnimator.start()
}