package org.supportcompact.ktx

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import org.greenrobot.eventbus.EventBus
import java.util.*

const val MAINTENANCE_END_TIME = "end_time"

const val ONLOGOUT = "3"

const val SHOW_PROGRESS = "1"

const val DISMISS_PROGRESS = "0"

fun ViewModel.showProgress() = EventBus.getDefault().postSticky(SHOW_PROGRESS)

fun ViewModel.dismissProgress() = EventBus.getDefault().postSticky(DISMISS_PROGRESS)

fun String.getUDID() = try {
    plus("_").plus(SystemClock.elapsedRealtime()).plus("_${UUID.randomUUID().toString().subSequence(0, 6)}")
} catch (e: Exception) {
    plus("_").plus(SystemClock.elapsedRealtime()).plus("_${SystemClock.elapsedRealtimeNanos()}")
}
