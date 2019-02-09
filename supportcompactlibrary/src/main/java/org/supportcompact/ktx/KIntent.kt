package org.supportcompact.ktx

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment


inline fun <reified T : Activity> Activity.startActivity() = startActivity(Intent(this, T::class.java))

inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int) = startActivityForResult(Intent(this, T::class.java), requestCode)

inline fun <reified T : Activity> Fragment.startActivity() = startActivity(Intent(activity, T::class.java))
