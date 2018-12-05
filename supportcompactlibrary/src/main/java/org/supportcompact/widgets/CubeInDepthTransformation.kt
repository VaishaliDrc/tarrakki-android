package org.supportcompact.widgets

import android.support.v4.view.ViewPager
import android.view.View

class CubeInDepthTransformation : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.cameraDistance = 20000F

        when {
            position < -1 -> page.alpha = 0F
            position <= 0 -> {
                page.alpha = 1F
                page.pivotX = page.width.toFloat()
                page.rotationY = 90 * Math.abs(position)
            }
            position <= 1 -> {
                page.alpha = 1F
                page.pivotX = 0F
                page.rotationY = -90 * Math.abs(position)
            }
            else -> page.alpha = 0F
        }



        if (Math.abs(position) <= 0.5) {
            page.scaleY = Math.max(.4f, 1 - Math.abs(position))
        } else if (Math.abs(position) <= 1) {
            page.scaleY = Math.max(.4f, 1 - Math.abs(position))

        }

    }
}