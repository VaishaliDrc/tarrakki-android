package com.tarrakki

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
import com.tarrakki.module.yourgoal.KEY_GOAL
import com.tarrakki.module.yourgoal.YourGoalFragment
import net.cachapa.expandablelayout.ExpandableLayout
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.applyCurrencyFormat
import org.supportcompact.ktx.startFragment
import org.supportcompact.widgets.DividerItemDecorationNoLast
import java.text.DecimalFormat
import java.util.*


val dformatter = DecimalFormat("##,##,##,##,###.00")
val formatter = DecimalFormat("##,##,##,##,###.##")

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

@BindingAdapter("price")
fun applyCurrencyFormat(txt: TextView, amount: Double) {
    txt.text = String.format(Locale.US, "%s%s", txt.context.getString(R.string.rs_symbol), formatter.format(amount))
}

@BindingAdapter("dprice")
fun applyDCurrencyFormat(txt: TextView, amount: Double) {
    txt.text = String.format(Locale.US, "%s%s", txt.context.getString(R.string.rs_symbol), dformatter.format(amount))
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

fun TextView.decimalFormat(amount: Double) {
    this.text = String.format(Locale.US, "%,.2f", amount)
}

fun TextView.format(amount: Double) {
    this.text = String.format(Locale.US, "%,d", Math.round(amount))
}

