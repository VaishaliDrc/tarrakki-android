package com.tarrakki

import android.content.Context
import android.os.Build
import androidx.appcompat.widget.Toolbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_toolbar_title.view.*


class CenteredToolbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.toolbarStyle) : Toolbar(context, attrs, defStyleAttr) {

    private var titleView: TextView = TextView(getContext())

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_toolbar_title, null)
        titleView = view.toolbar_title
        addView(titleView, Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        titleView.x = ((width - titleView.width) / 2).toFloat()
    }

    override fun setTitle(title: CharSequence?) {
        titleView.text = title
    }

}