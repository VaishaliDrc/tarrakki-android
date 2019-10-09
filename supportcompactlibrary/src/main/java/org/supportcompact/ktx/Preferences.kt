package org.supportcompact.ktx

/**
 * Created by jayeshparkariya on 27/2/18.
 */
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


const val IS_LOGIN = "is_login"
const val IS_SOACIAL_LOGIN = "is_social_login"
const val LOGIN_TOKEN = "login_token"
const val APP_LOCK = "app_lock"
const val USERID = "user_id"
const val EMAIL = "email"
const val MOBILE = "mobile"
const val ISFIRSTTIME = "first_time_installed"
const val EMAIL_VERIFIED = "email_verified"
const val MOBILE_VERIFIED = "mobile_verified"
const val KYC_VERIFIED = "kyc_verified"
const val COMPLETED_REGISTRATION = "completed_registration"
const val ASK_FOR_SECURITY_LOCK = "askForSecurityLock"
const val PUSH_TOKEN = "push_token"
const val IS_READY_TO_INVEST = "is_ready_to_invest"

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

fun Context.setSocialLogin(has: Boolean) {
    return getPreferences.putBoolean(IS_SOACIAL_LOGIN, has)
}

fun Context.isSocialLogin(): Boolean {
    return getPreferences.getBoolean(IS_SOACIAL_LOGIN, false)
}

fun Context.setIsLogin(has: Boolean) {
    return getPreferences.putBoolean(IS_LOGIN, has)
}

fun Context.isLogin(): Boolean {
    return getPreferences.getBoolean(IS_LOGIN, false)
}

fun Context.setLoginToken(token: String) {
    return getPreferences.putString(LOGIN_TOKEN, token)
}

fun Context.getLoginToken(): String? {
    return getPreferences.getString(LOGIN_TOKEN, "")
}

fun Context.setUserId(userid: String) {
    return getPreferences.putString(USERID, userid)
}

fun Context.getUserId(): String? {
    return getPreferences.getString(USERID, "`")
}

fun Context.setEmail(emai: String) {
    return getPreferences.putString(EMAIL, emai)
}

fun Context.getEmail(): String? {
    return getPreferences.getString(EMAIL, "`")
}

fun Context.setMobile(mobile: String) {
    return getPreferences.putString(MOBILE, mobile)
}

fun Context.getMobile(): String? {
    return getPreferences.getString(MOBILE, "`")
}

fun Context.setAppIsLock(has: Boolean) {
    return getPreferences.putBoolean(APP_LOCK, has)
}

fun Context.hasAppLock(): Boolean {
    return getPreferences.getBoolean(APP_LOCK, false)
}

fun Context.setFirsttimeInstalled(isFirstTime: Boolean) {
    return getPreferences.putBoolean(ISFIRSTTIME, isFirstTime)
}

fun Context.isFirsttimeInstalled(): Boolean {
    return getPreferences.getBoolean(ISFIRSTTIME, true)
}

fun Context.setMobileVerified(isMobileVerified: Boolean) {
    return getPreferences.putBoolean(MOBILE_VERIFIED, isMobileVerified)
}

fun Context.isMobileVerified(): Boolean {
    return getPreferences.getBoolean(MOBILE_VERIFIED, false)
}

fun Context.setEmailVerified(isEmailVerified: Boolean) {
    return getPreferences.putBoolean(EMAIL_VERIFIED, isEmailVerified)
}

fun Context.isEmailVerified(): Boolean {
    return getPreferences.getBoolean(EMAIL_VERIFIED, false)
}

fun Context.setKYClVarified(isKYCVerified: Boolean) {
    return getPreferences.putBoolean(KYC_VERIFIED, isKYCVerified)
}

fun Context.isKYCVerified(): Boolean {
    return getPreferences.getBoolean(KYC_VERIFIED, false)
}

fun Context.setCompletedRegistration(isCompletedRegistration: Boolean) {
    return getPreferences.putBoolean(COMPLETED_REGISTRATION, isCompletedRegistration)
}

fun Context.isCompletedRegistration(): Boolean {
    return getPreferences.getBoolean(COMPLETED_REGISTRATION, false)
}

fun Context.setAskForSecureLock(isAskForSecureLock: Boolean) {
    return getPreferences.putBoolean(ASK_FOR_SECURITY_LOCK, isAskForSecureLock)
}

fun Context.isAskForSecureLock(): Boolean {
    return getPreferences.getBoolean(ASK_FOR_SECURITY_LOCK, false)
}

fun Context.setPushToken(pushToken: String) {
    return getPreferences.putString(PUSH_TOKEN, pushToken)
}

fun Context.getPushToken(): String? {
    return getPreferences.getString(PUSH_TOKEN, "")
}

fun Context.setReadyToInvest(isReadyToInvest: Boolean) {
    return getPreferences.putBoolean(IS_READY_TO_INVEST, isReadyToInvest)
}

fun Context.isReadyToInvest(): Boolean {
    return getPreferences.getBoolean(IS_READY_TO_INVEST, false)
}

fun Context.clearUserData() {
    getPreferences.remove(IS_LOGIN)
    getPreferences.remove(IS_SOACIAL_LOGIN)
    getPreferences.remove(LOGIN_TOKEN)
    getPreferences.remove(APP_LOCK)
    getPreferences.remove(USERID)
    getPreferences.remove(MOBILE_VERIFIED)
    getPreferences.remove(EMAIL_VERIFIED)
    getPreferences.remove(KYC_VERIFIED)
    getPreferences.remove(COMPLETED_REGISTRATION)
    getPreferences.remove(ASK_FOR_SECURITY_LOCK)
}
