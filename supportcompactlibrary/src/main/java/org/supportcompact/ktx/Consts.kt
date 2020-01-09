package org.supportcompact.ktx

import androidx.lifecycle.ViewModel
import org.greenrobot.eventbus.EventBus

const val MAINTENANCE_END_TIME = "end_time"

const val ONLOGOUT = "3"

const val SHOW_PROGRESS = "1"

const val DISMISS_PROGRESS = "0"

fun ViewModel.showProgress() = EventBus.getDefault().postSticky(SHOW_PROGRESS)

fun ViewModel.dismissProgress() = EventBus.getDefault().postSticky(DISMISS_PROGRESS)