package com.tarrakki

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator
import com.tarrakki.databinding.RowInvestmentListItemBinding
import com.tarrakki.module.home.HomeItem
import net.cachapa.expandablelayout.ExpandableLayout
import org.supportcompact.adapters.setUpRecyclerView

@BindingAdapter(value = ["setAdapterH"], requireAll = false)
fun setAdapterH(view: RecyclerView, homeItems: ArrayList<HomeItem>?) {
    view.isFocusable = false
    view.isNestedScrollingEnabled = false
    homeItems?.let {
        view.setUpRecyclerView(R.layout.row_investment_list_item, it) { item: HomeItem, binder: RowInvestmentListItemBinding, position ->
            binder.homeItem = item
            binder.executePendingBindings()
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

@BindingAdapter("dividerH")
fun setDividerHorizontal(rv: RecyclerView, drawable: Drawable) {
    val divider = DividerItemDecoration(rv.context, LinearLayoutManager.HORIZONTAL)
    divider.setDrawable(drawable)
    rv.addItemDecoration(divider)
}

@BindingAdapter("dividerV")
fun setDividerVertical(rv: RecyclerView, drawable: Drawable) {
    val divider = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
    divider.setDrawable(drawable)
    rv.addItemDecoration(divider)
}

@BindingAdapter("indicator")
fun setIndicator(indicator: IndefinitePagerIndicator, rv: RecyclerView) {
    indicator.attachToRecyclerView(rv)
    // If you need to change the adapter size, you should call this function
    //indicator.forceUpdateItemCount();
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