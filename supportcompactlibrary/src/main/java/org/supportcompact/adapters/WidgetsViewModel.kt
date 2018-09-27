package org.supportcompact.adapters

import android.support.annotation.LayoutRes

interface WidgetsViewModel {

    @LayoutRes
    fun layoutId(): Int
}