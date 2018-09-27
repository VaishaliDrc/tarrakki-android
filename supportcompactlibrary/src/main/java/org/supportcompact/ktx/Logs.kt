package org.supportcompact.ktx

import android.util.Log
import org.supportcompact.networking.ApiClient.BUILD_TYPE_DEBUG


fun Any.e(e: Any) = if (BUILD_TYPE_DEBUG) Log.e(this.javaClass.name, "$e") else null

fun Any.e(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.e(tag, "$e") else null

fun Any.i(e: Any) = if (BUILD_TYPE_DEBUG) Log.i(this.javaClass.name, "$e") else null

fun Any.i(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.i(tag, "$e") else null

fun Any.d(e: Any) = if (BUILD_TYPE_DEBUG) Log.d(this.javaClass.name, "$e") else null

fun Any.d(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.d(tag, "$e") else null

fun Any.v(e: Any) = if (BUILD_TYPE_DEBUG) Log.v(this.javaClass.name, "$e") else null

fun Any.v(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.v(tag, "$e") else null

fun Any.w(e: Any) = if (BUILD_TYPE_DEBUG) Log.w(this.javaClass.name, "$e") else null

fun Any.w(tag: String, e: Any) = if (BUILD_TYPE_DEBUG) Log.w(tag, "$e") else null