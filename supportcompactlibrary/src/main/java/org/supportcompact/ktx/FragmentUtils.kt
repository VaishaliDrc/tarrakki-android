package org.supportcompact.ktx

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity


fun AppCompatActivity.startFragment(fragment: Fragment, @IdRes container: Int) {
    val fm = supportFragmentManager
    fm.beginTransaction()
            .replace(container, fragment)
            .addToBackStack(fragment::class.java.name)
            .commit()
}

fun AppCompatActivity.startFragment(fragment: Fragment, targetFragment: Fragment, requestCode: Int, @IdRes container: Int) {
    fragment.setTargetFragment(targetFragment, requestCode)
    val fm = supportFragmentManager
    fm.beginTransaction()
            .replace(container, fragment)
            .addToBackStack(fragment::class.java.name)
            .commit()
}

fun Fragment.startFragment(fragment: Fragment, @IdRes container: Int) {
    val fm = activity?.supportFragmentManager
    fm?.beginTransaction()
            ?.replace(container, fragment)
            ?.addToBackStack(fragment::class.java.name)
            ?.commit()
}

fun Fragment.letShow(@IdRes container: Int) {
    val fm = activity?.supportFragmentManager
    fm?.beginTransaction()
            ?.replace(container, this)
            ?.addToBackStack(this::class.java.name)
            ?.commit()
}

fun AppCompatActivity.startFragment(fragment: Fragment, backStrackFlag: Boolean, @IdRes container: Int) {
    val fm = supportFragmentManager
    if (backStrackFlag) {
        fm.beginTransaction()
                .replace(container, fragment)
                .addToBackStack(fragment::class.java.name)
                .commit()
    } else {
        fm.beginTransaction()
                .replace(container, fragment)
                .commit()
    }
}
