package org.supportcompact.events

import org.supportcompact.CoreApp
import org.supportcompact.R

data class ShowError(val error: String)

data class ShowErrorDialog(val title: String = CoreApp.getInstance().getString(R.string.app_name), val error: String)

data class ShowECutOffTimeDialog(
        val title: String = CoreApp.getInstance().getString(R.string.app_name),
        val error: String,
        val msg: String) {

    var fundList: ArrayList<String>? = null
        get() = if (field == null) {
            field = arrayListOf()
            msg.split(",").forEach { item: String? ->
                item?.trim()?.let { field?.add(it) }
            }
            field
        } else {
            field
        }
}