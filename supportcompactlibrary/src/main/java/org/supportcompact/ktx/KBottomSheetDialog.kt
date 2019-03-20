package org.supportcompact.ktx

import android.content.Context
import android.graphics.Paint
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.view.View

fun Fragment.bottomSheetDialog(init: BottomSheetDialogBuilder.() -> Unit,@StyleRes theme : Int? = null): BottomSheetDialogBuilder? = activity?.bottomSheetDialog(init,theme)

fun Context.bottomSheetDialog(init: BottomSheetDialogBuilder.() -> Unit,@StyleRes theme : Int? = null) = BottomSheetDialogBuilder(this,theme).apply(init)

class BottomSheetDialogBuilder(context: Context,@StyleRes theme : Int? = null) {

    var dialog: BottomSheetDialog? = null

    init {
        dialog = if (theme!=null) {
            BottomSheetDialog(context, theme)
        }else{
            BottomSheetDialog(context)
        }
    }

    fun dismiss() = dialog?.dismiss()

    fun show(): BottomSheetDialogBuilder {
        dialog!!.show()
        return this
    }

    fun customView(view: View) {
        dialog?.setContentView(view)
    }

    fun cancelable(value: Boolean = true) {
        dialog?.setCancelable(value)
    }

    fun cancelableTouchOutside(value: Boolean = true) {
        dialog?.setCanceledOnTouchOutside(value)
    }

}