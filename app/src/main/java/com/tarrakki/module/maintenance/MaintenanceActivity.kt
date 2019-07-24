package com.tarrakki.module.maintenance

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.databinding.ActivityMaintenanceBinding

class MaintenanceActivity : BaseActivity() {

    var mBinder: ActivityMaintenanceBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinder = ActivityMaintenanceBinding.inflate(layoutInflater, getBinding().frmContainer, true)
        mBinder?.let {
            it.vm = getViewModel()
            it.executePendingBindings()
            init()
        }
    }

    private fun init() {
        getViewModel().footerVisibility.set(View.GONE)
        getViewModel().title.set(getString(R.string.down_for_maintenance))
        getViewModel().isBackEnabled.value = false
        val spBuilder = SpannableStringBuilder()
        //spBuilder.append("10", RelativeSizeSpan(1.2f), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spBuilder.append(SpannableString("10").apply {
            setSpan(RelativeSizeSpan(1.5f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
        spBuilder.append("\n")
        spBuilder.append("mn")
        getViewModel().timerValue.set(spBuilder)
    }
}