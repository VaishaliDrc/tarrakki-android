package com.tarrakki

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.tarrakki.module.goal.Goal
import com.tarrakki.module.recommended.RecommendedFragment
import com.tarrakki.module.yourgoal.KEY_GOAL
import com.tarrakki.module.yourgoal.YourGoalFragment
import net.cachapa.expandablelayout.ExpandableLayout
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.*
import org.supportcompact.widgets.DividerItemDecorationNoLast
import java.util.*


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
                    mContext.startFragment(YourGoalFragment.newInstance(Bundle().apply { putSerializable(KEY_GOAL, item) }), R.id.frmContainer)
                    //mContext.startFragment(RecommendedFragment.newInstance(Bundle().apply { putSerializable(KEY_GOAL, item) }), R.id.frmContainer)
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

@BindingAdapter("amount")
fun applyAmountFormat(txt: TextView, amount: Double) {
    txt.text = String.format(Locale.US, "%,d", amount)
}

@BindingAdapter("dAmount")
fun applyDAmountFormat(txt: TextView, amount: Double) {
    txt.text = String.format(Locale.US, "%,.2f", amount)
}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("isSwappable")
fun disableSwipe(mPager: ViewPager, swappable: Boolean) {
    mPager.setOnTouchListener { p0, p1 -> !swappable }
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

fun TextView.decimalFormat(amount: Double) {
    this.text = String.format(Locale.US, "%,.2f", amount)
}

fun TextView.format(amount: Double) {
    this.text = String.format(Locale.US, "%,d", Math.round(amount))
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

fun returns(initialValue: Double, finalValue: Double, textview: TextView) {
    val valueAnimator = ValueAnimator.ofFloat(initialValue.toFloat(), finalValue.toFloat())
    valueAnimator.duration = 1500
    val returnType = if (finalValue > 0) "+" else "-"
    valueAnimator.addUpdateListener { it ->
        textview.text = returnType.plus(it.animatedValue.toString().toDouble().toDecimalCurrency())
    }
    valueAnimator.start()
}