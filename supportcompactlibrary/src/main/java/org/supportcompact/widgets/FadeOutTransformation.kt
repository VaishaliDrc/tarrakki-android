package org.supportcompact.widgets

import androidx.viewpager.widget.ViewPager
import android.view.View

class FadeOutTransformation : androidx.viewpager.widget.ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.alpha = 1 - Math.abs(position)
    }
}