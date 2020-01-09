package com.tarrakki.module.maintenance

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log.e
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.databinding.ActivityMaintenanceBinding
import com.tarrakki.getMaintenanceDetails
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
        try {
            val endDate = intent.getStringExtra(MAINTENANCE_END_TIME)?.toDate("dd/MM/yyyy hh:mm a")
            endDate?.let {
                this.endDate = it
                mHandler.postDelayed(runnable, 100)
            }
        } catch (e: Exception) {
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        try {
            val endDate = intent?.getStringExtra(MAINTENANCE_END_TIME)?.toDate("dd/MM/yyyy hh:mm a")
            endDate?.let {
                mHandler.removeCallbacks(runnable)
                this.endDate = it
                mHandler.postDelayed(runnable, 100)
            }
        } catch (e: Exception) {
        }
    }

    var endDate: Date? = null
    val mHandler = Handler()
    val runnable = object : Runnable {
        override fun run() {
            try {
                endDate?.let { endDate ->
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
                    e("Difference: ", "minutes: ${if (diffInMinutes == 0L) 1 else diffInMinutes} seconds: $diffInSeconds")
                    spBuilder.append(SpannableString("${if (diffInMinutes == 0L) 1 else diffInMinutes}").apply {
                        setSpan(RelativeSizeSpan(1.5f), 0, "$diffInMinutes".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(StyleSpan(Typeface.BOLD), 0, "$diffInMinutes".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    })
                    spBuilder.append("\n")
                    spBuilder.append("mins")
                    getViewModel().timerValue.set(spBuilder)
                    mHandler.postDelayed(this, 1000)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maintenance, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_refresh) {
            getMaintenanceDetails().observe(this, androidx.lifecycle.Observer {
                finish()
            })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mHandler.removeCallbacks(runnable)
        } catch (e: Exception) {
        }
    }
}