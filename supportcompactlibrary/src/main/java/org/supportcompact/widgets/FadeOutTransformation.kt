package org.supportcompact.widgets

import android.support.v4.view.ViewPager
import android.view.View

class FadeOutTransformation : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.alpha = 1 - Math.abs(position)
    }
}