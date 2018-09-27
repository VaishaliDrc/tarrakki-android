package org.supportcompact.ktx

import android.app.Activity
import android.content.Intent


inline fun <reified T : Activity> Activity.startActivity() = startActivity(Intent(this, T::class.java))

inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int) = startActivityForResult(Intent(this, T::class.java), requestCode)