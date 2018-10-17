package org.supportcompact.inputclasses

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import io.reactivex.Observable
import android.os.Build
import android.util.Log
import android.util.TypedValue


/**
 * Manages access to the Android soft keyboard.
 */
class KeyboardManager constructor(private val activity: Activity) {

    /**
     * Observable of the status of the keyboard. Subscribing to this creates a
     * Global Layout Listener which is automatically removed when this
     * observable is disposed.
     */
    fun status() = Observable.create<KeyboardStatus> { emitter ->
        val rootView = activity.findViewById<View>(android.R.id.content)

        // why are we using a global layout listener? Surely Android
        // has callback for when the keyboard is open or closed? Surely
        // Android at least lets you query the status of the keyboard?
        // Nope! https://stackoverflow.com/questions/4745988/
        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {

            val rect = Rect().apply { rootView.getWindowVisibleDisplayFrame(this) }

            val screenHeight = rootView.height

            // rect.bottom is the position above soft keypad or device button.
            // if keypad is shown, the rect.bottom is smaller than that before.
            val keypadHeight = screenHeight - rect.bottom

            // 0.15 ratio is perhaps enough to determine keypad height.
            if (keypadHeight > screenHeight * 0.15) {
                emitter.onNext(KeyboardStatus.OPEN)
            } else {
                emitter.onNext(KeyboardStatus.CLOSED)
            }
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        emitter.setCancellable {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }

    }.distinctUntilChanged()
}

enum class KeyboardStatus {
    OPEN, CLOSED
}


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
        /*val rect = Rect().apply { rootView.getWindowVisibleDisplayFrame(this) }

        val screenHeight = rootView.height

        // rect.bottom is the position above soft keypad or device button.
        // if keypad is shown, the rect.bottom is smaller than that before.
        val keypadHeight = screenHeight - rect.bottom

        // 0.15 ratio is perhaps enough to determine keypad height.
        if (keypadHeight > screenHeight * 0.15) {
            keyboardListener.invoke(true)
        } else {
            keyboardListener.invoke(false)
        }*/
    }
    parentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}