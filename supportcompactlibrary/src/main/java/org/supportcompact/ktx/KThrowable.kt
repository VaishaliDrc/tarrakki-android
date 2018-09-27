package org.supportcompact.ktx

import org.greenrobot.eventbus.EventBus
import org.supportcompact.events.ShowError

fun Throwable.postError() = EventBus.getDefault().postSticky(ShowError("$message"))

fun postError(error: String) = EventBus.getDefault().postSticky(ShowError(error))