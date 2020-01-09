package org.supportcompact.ktx

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import libs.mjn.prettydialog.PrettyDialog
import org.supportcompact.R

/**
 * Created by jayeshparkariya on 28/2/18.
 */
fun Context.simpleAlert(msg: String, positiveButton: (() -> Unit)? = null) {
    val mDialog = PrettyDialog(this)
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.drawable.ic_info_white)
            .setTitle(getString(R.string.app_name))
            .setMessage(msg)
            .addButton(getString(R.string.ok), R.color.white, R.color.btn_bg_color) {
                positiveButton?.invoke()
                mDialog.dismiss()
            }.show()
    /*val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(getString(R.string.app_name))
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
                positiveButton?.invoke()
            }
    mDialog.create().show()*/
}

fun Context.simpleAlert(title: String, msg: String, positiveButton: (() -> Unit)? = null) {
    val mDialog = PrettyDialog(this)
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.drawable.ic_info_white)
            .setTitle(title)
            .setMessage(msg)
            .addButton(getString(R.string.ok), R.color.white, R.color.btn_bg_color) {
                positiveButton?.invoke()
                mDialog.dismiss()
            }.show()
    /*val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
                positiveButton?.invoke()
            }
    mDialog.create().show()*/
}

fun Context.simpleAlert(title: String, msg: String, btnTitle: String, positiveButton: (() -> Unit)? = null) {
    val mDialog = PrettyDialog(this)
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.drawable.ic_info_white)
            .setTitle(title)
            .setMessage(msg)
            .addButton(btnTitle, R.color.white, R.color.btn_bg_color) {
                positiveButton?.invoke()
                mDialog.dismiss()
            }.show()
    /*val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
                positiveButton?.invoke()
            }
    mDialog.create().show()*/
}

fun Context.appForceUpdate(title: String, msg: String, btnTitle: String, positiveButton: (() -> Unit)? = null) {
    val mDialog = PrettyDialog(this)
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.drawable.ic_info_white)
            .setTitle(title)
            .setMessage(msg)
            .addButton(btnTitle, R.color.white, R.color.btn_bg_color) {
                positiveButton?.invoke()
            }.show()
    /*val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
                positiveButton?.invoke()
            }
    mDialog.create().show()*/
}

fun Context.confirmationDialog(msg: String, btnPositiveClick: (() -> Unit)? = null, btnNegativeClick: (() -> Unit)? = null) {
    val mDialog = PrettyDialog(this)
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.drawable.ic_info_white)
            .setTitle(getString(R.string.app_name))
            .setMessage(msg)
            .addButton(getString(R.string.yes), R.color.white, R.color.btn_bg_color) {
                btnPositiveClick?.invoke()
                mDialog.dismiss()
            }.addButton(getString(R.string.cancel), R.color.white, R.color.btn_bg_color) {
                btnNegativeClick?.invoke()
                mDialog.dismiss()
            }.show()
    /*val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(getString(R.string.app_name))
            .setMessage(msg)
            .setPositiveButton(getString(R.string.no)) { _, which -> btnNegativeClick?.invoke() }
            .setNegativeButton(getString(R.string.yes)) { dialg, which -> btnPositiveClick?.invoke() }
            .create().show()*/
}

fun Context.confirmationDialog(title: String, msg: String, btnPositiveClick: (() -> Unit)? = null, btnNegativeClick: (() -> Unit)? = null) {
    val mDialog = PrettyDialog(this)
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.drawable.ic_info_white)
            .setTitle(title)
            .setMessage(msg)
            .addButton(getString(R.string.yes), R.color.white, R.color.btn_bg_color) {
                btnPositiveClick?.invoke()
                mDialog.dismiss()
            }.addButton(getString(R.string.cancel), R.color.white, R.color.btn_bg_color) {
                btnNegativeClick?.invoke()
                mDialog.dismiss()
            }.show()
    /*val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.no)) { _, which -> btnNegativeClick?.invoke() }
            .setNegativeButton(getString(R.string.yes)) { dialg, which -> btnPositiveClick?.invoke() }
            .create().show()*/
}

fun Context.confirmationDialog(title: String, msg: String, btnPositive: String, btnNegative: String, btnPositiveClick: (() -> Unit)? = null, btnNegativeClick: (() -> Unit)? = null) {
    val mDialog = PrettyDialog(this)
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.drawable.ic_info_white)
            .setTitle(title)
            .setMessage(msg)
            .addButton(btnPositive, R.color.white, R.color.btn_bg_color) {
                btnPositiveClick?.invoke()
                mDialog.dismiss()
            }.addButton(btnNegative, R.color.white, R.color.btn_bg_color) {
                btnNegativeClick?.invoke()
                mDialog.dismiss()
            }.show()

    /*val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.no)) { _, which -> btnNegativeClick?.invoke() }
            .setNegativeButton(getString(R.string.yes)) { dialg, which -> btnPositiveClick?.invoke() }
            .create().show()*/
}

fun Context.takePick(onGallery: (() -> Unit)? = null, onCamera: (() -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle("Select Picture")
            .setItems(R.array.select_image_from) { dialogInterface, which ->
                when (which) {
                    0 -> {
                        dialogInterface.dismiss()
                        /**Open gallery*/
                        onGallery?.invoke()
                    }
                    1 -> {
                        dialogInterface.dismiss()
                        /**Open camera*/
                        onCamera?.invoke()
                    }
                }
            }
            .create().show()
}

fun Context.accountTypes(onItemSelected: ((item: String) -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data = resources.getStringArray(R.array.accountTypes)
    mDialog.setTitle("Account Type")
            .setItems(data) { dialogInterface, which ->
                dialogInterface.dismiss()
                onItemSelected?.invoke(data[which])
            }
            .create().show()
}

fun Context.showListDialog(title: String?, list: ArrayList<String>, onItemSelected: ((item: String) -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data = list.toTypedArray()
    mDialog.setTitle(title)
            .setItems(data) { dialogInterface, which ->
                dialogInterface.dismiss()
                onItemSelected?.invoke(data[which])
            }
            .create().show()
}

fun Context.showListDialog(title: String?, data: Array<String>, onItemSelected: ((item: String) -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setItems(data) { dialogInterface, which ->
                dialogInterface.dismiss()
                onItemSelected?.invoke(data[which])
            }
            .create().show()
}

fun Context.showListDialog(@StringRes title: Int, @ArrayRes list: Int, onItemSelected: ((item: String) -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data = resources.getTextArray(list)
    mDialog.setTitle(title)
            .setItems(list) { dialogInterface, which ->
                dialogInterface.dismiss()
                onItemSelected?.invoke(data[which] as String)
            }
            .create().show()
}

fun Context.showListDialog(@StringRes title: Int, data: Array<String>, onItemSelected: ((item: String, which: Int) -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setItems(data) { dialogInterface, which ->
                dialogInterface.dismiss()
                onItemSelected?.invoke(data[which], which)
            }
            .create().show()
}


fun <T> Context.showCustomListDialog(title: String?, list: ArrayList<T>, onItemSelected: ((item: T) -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data: Array<String?> = arrayOfNulls(list.size)
    list.forEachIndexed { index, t -> data[index] = t.toString() }
    mDialog.setTitle(title)
            .setItems(data) { dialogInterface, which ->
                dialogInterface.dismiss()
                onItemSelected?.invoke(list[which])
            }
            .create().show()
}

fun <T> Context.showCustomListDialog(@StringRes title: Int, list: ArrayList<T>, onItemSelected: ((item: T) -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data: Array<String?> = arrayOfNulls(list.size)
    list.forEachIndexed { index, t -> data[index] = t.toString() }
    mDialog.setTitle(title)
            .setItems(data) { dialogInterface, which ->
                dialogInterface.dismiss()
                onItemSelected?.invoke(list[which])
            }
            .create().show()
}
