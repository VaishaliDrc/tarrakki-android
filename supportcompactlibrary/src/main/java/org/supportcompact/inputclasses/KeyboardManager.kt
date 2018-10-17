package org.supportcompact.inputclasses

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver


/**
 * Manages access to the Android soft keyboard.
 */
fun Activity.keyboardListener(keyboardListener: (isOpen: Boolean) -> Unit) {

    val parentView = findViewById<View>(android.R.id.content)
    var alreadyOpen = false
    val defaultKeyboardHeightDP = 100
    val estimatedKeyboardDP = defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
    val rect = Rect()
    // why are we using a global layout listener? Surely Android
    // has callback for when the keyboard is open or closed? Surely
    // Android at least lets you query the status of the keyboard?
    // Nope! https://stackoverflow.com/questions/4745988/
    val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {

        val estimatedKeyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, estimatedKeyboardDP.toFloat(), parentView.resources.displayMetrics).toInt()
        parentView.getWindowVisibleDisplayFrame(rect)
        val heightDiff = parentView.rootView.height - (rect.bottom - rect.top)
        val isShown = heightDiff >= estimatedKeyboardHeight

        if (isShown == alreadyOpen) {
            Log.i("Keyboard state", "Ignoring global layout change...")
            return@OnGlobalLayoutListener
        }
        alreadyOpen = isShown
        keyboardListener.invoke(isShown)
    }
    parentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}