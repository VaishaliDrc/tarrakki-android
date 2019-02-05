package org.supportcompact.ktx

import android.support.annotation.StringRes
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreApp
import org.supportcompact.events.ShowError

fun Throwable.postError() = EventBus.getDefault().postSticky(ShowError("$message"))

fun Throwable.postError(error: String) {
    EventBus.getDefault().postSticky(DISMISS_PROGRESS)
    EventBus.getDefault().postSticky(ShowError(error))
}

fun Throwable.postError(@StringRes error: Int) {
    EventBus.getDefault().postSticky(DISMISS_PROGRESS)
    EventBus.getDefault().postSticky(ShowError(error = CoreApp.getInstance().getString(error)))
}

fun postError(error: String) = EventBus.getDefault().postSticky(ShowError(error))