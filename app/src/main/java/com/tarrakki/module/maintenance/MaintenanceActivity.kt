package com.tarrakki.module.maintenance

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log.e
import android.view.View
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.databinding.ActivityMaintenanceBinding
import org.supportcompact.ktx.MAINTENANCE_END_TIME
import org.supportcompact.ktx.toDate
import java.util.*
import java.util.concurrent.TimeUnit


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
        val endDate = intent.getStringExtra(MAINTENANCE_END_TIME)?.toDate("dd/MM/yyyy hh:mm a")
        endDate?.let {
            setTimer(endDate)
        }
    }

    private fun setTimer(endDate: Date) {
        val mHandler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                try {
                    val spBuilder = SpannableStringBuilder()
                    val currentDate = Date()
                    val duration = endDate.time - currentDate.time
                    if (duration < 0) {
                        finish()
                        return
                    }
                    val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                    val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                    val diffInHours = TimeUnit.MILLISECONDS.toHours(duration)
                    val diffInDays = TimeUnit.MILLISECONDS.toDays(duration)
                    e("Difference: ", "minutes: $diffInMinutes seconds: $diffInSeconds")
                    spBuilder.append(SpannableString("$diffInMinutes").apply {
                        setSpan(RelativeSizeSpan(1.5f), 0, "$diffInMinutes".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(StyleSpan(Typeface.BOLD), 0, "$diffInMinutes".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    spBuilder.append("\n")
                    spBuilder.append("mn")
                    getViewModel().timerValue.set(spBuilder)
                    mHandler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mHandler.postDelayed(runnable, 100)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}