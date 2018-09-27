package org.supportcompact.ktx

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import org.supportcompact.R

/**
 * Created by jayeshparkariya on 28/2/18.
 */
fun Context.simpleAlert(msg: String, positiveButton: (() -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(getString(R.string.app_name))
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
                positiveButton?.invoke()
            }
    mDialog.create().show()
}

fun Context.simpleAlert(title: String, msg: String, positiveButton: (() -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
                positiveButton?.invoke()
            }
    mDialog.create().show()
}

fun Context.confirmationDialog(msg: String, btnPositiveClick: (() -> Unit)? = null, btnNegativeClick: (() -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(getString(R.string.app_name))
            .setMessage(msg)
            .setPositiveButton(getString(R.string.no)) { _, which -> btnNegativeClick?.invoke() }
            .setNegativeButton(getString(R.string.yes)) { dialg, which -> btnPositiveClick?.invoke() }
            .create().show()
}

fun Context.confirmationDialog(title: String, msg: String, btnPositiveClick: (() -> Unit)? = null, btnNegativeClick: (() -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.no)) { _, which -> btnNegativeClick?.invoke() }
            .setNegativeButton(getString(R.string.yes)) { dialg, which -> btnPositiveClick?.invoke() }
            .create().show()
}

fun Context.confirmationDialog(title: String, msg: String, btnPositive: String, btnNegative: String, btnPositiveClick: (() -> Unit)? = null, btnNegativeClick: (() -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.no)) { _, which -> btnNegativeClick?.invoke() }
            .setNegativeButton(getString(R.string.yes)) { dialg, which -> btnPositiveClick?.invoke() }
            .create().show()
}