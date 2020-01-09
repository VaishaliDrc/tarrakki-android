package org.supportcompact.adapters

import androidx.annotation.LayoutRes

interface WidgetsViewModel {

    @LayoutRes
    fun layoutId(): Int
}