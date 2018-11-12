package org.supportcompact.ktx

/**
 * Created by jayeshparkariya on 27/2/18.
 */
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


const val IS_LOGIN = "is_login"

public val Context.getPreferences: SharedPreferences
    get() {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

public fun SharedPreferences.clear() {
    apply(getEditor().clear())
}

public fun SharedPreferences.putBoolean(key: String, value: Boolean) {
    apply(getEditor().putBoolean(key, value))
}

public fun SharedPreferences.putFloat(key: String, value: Float) {
    apply(getEditor().putFloat(key, value))
}

public fun SharedPreferences.putInt(key: String, value: Int) {
    apply(getEditor().putInt(key, value))
}

public fun SharedPreferences.putLong(key: String, value: Long) {
    apply(getEditor().putLong(key, value))
}

public fun SharedPreferences.putString(key: String, value: String?) {
    apply(getEditor().putString(key, value))
}

public fun SharedPreferences.putStringSet(key: String, values: Set<String>?) {
    apply(getEditor().putStringSet(key, values))
}

public fun SharedPreferences.remove(key: String) {
    apply(getEditor().remove(key))
}

public fun SharedPreferences.bulk(): SharedPreferences {
    this.bulkEditor = this.edit()
    return this
}

public fun SharedPreferences.applyBulk(): SharedPreferences {
    this.bulkEditor?.apply()
    return this
}

public fun SharedPreferences.discardBulk(): SharedPreferences {
    this.bulkEditor = null
    return this
}

/*
 * -----------------------------------------------------------------------------
 *  Private fields
 * -----------------------------------------------------------------------------
 */
private var SharedPreferences.bulkEditor: SharedPreferences.Editor?
    get() = this.bulkEditor
    set(editor: SharedPreferences.Editor?) {
        this.bulkEditor = editor
    }

/*
 * -----------------------------------------------------------------------------
 *  Private methods
 * -----------------------------------------------------------------------------
 */
private fun SharedPreferences.getEditor(): SharedPreferences.Editor {
    return this.edit()
}

private fun SharedPreferences.apply(editor: SharedPreferences.Editor) {
    editor.apply()
}

fun Context.setIsLogin(has: Boolean) {
    return getPreferences.putBoolean(IS_LOGIN, has)
}

fun Context.isLogin(): Boolean {
    return getPreferences.getBoolean(IS_LOGIN, false)
}

fun Context.clearUserData() {
    getPreferences.remove(IS_LOGIN)
}
