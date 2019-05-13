package org.supportcompact.ktx

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity


fun AppCompatActivity.startFragment(fragment: Fragment, @IdRes container: Int) {
    val fm = supportFragmentManager
    val transaction = fm?.beginTransaction()
    try {
        transaction?.replace(container, fragment, fragment::class.java.name)
                ?.addToBackStack(fragment::class.java.name)
                ?.commit()
    } catch (e: IllegalStateException) {
        transaction?.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.startFragment(fragment: Fragment, targetFragment: Fragment, requestCode: Int, @IdRes container: Int) {
    fragment.setTargetFragment(targetFragment, requestCode)
    val fm = supportFragmentManager
    val transaction = fm?.beginTransaction()
    try {
        transaction?.replace(container, fragment, fragment::class.java.name)
                ?.addToBackStack(fragment::class.java.name)
                ?.commit()
    } catch (e: IllegalStateException) {
        transaction?.commitAllowingStateLoss()
    }
}

fun Fragment.startFragment(fragment: Fragment, @IdRes container: Int) {
    val fm = activity?.supportFragmentManager
    val transaction = fm?.beginTransaction()
    try {
        transaction?.replace(container, fragment, fragment::class.java.name)
                ?.addToBackStack(fragment::class.java.name)
                ?.commit()
    } catch (e: IllegalStateException) {
        transaction?.commitAllowingStateLoss()
    }
}

fun FragmentActivity.startFragment(fragment: Fragment, @IdRes container: Int) {
    val fm = this.supportFragmentManager
    val transaction = fm?.beginTransaction()
    try {
        transaction?.replace(container, fragment, fragment::class.java.name)
                ?.addToBackStack(fragment::class.java.name)
                ?.commit()
    } catch (e: IllegalStateException) {
        transaction?.commitAllowingStateLoss()
    }
}

fun Fragment.startFragmentWithoutBackStack(fragment: Fragment, @IdRes container: Int) {
    val fm = activity?.supportFragmentManager
    val transaction = fm?.beginTransaction()
    try {
        transaction?.replace(container, fragment, fragment::class.java.name)
                ?.addToBackStack(fragment::class.java.name)
                ?.commit()
    } catch (e: IllegalStateException) {
        transaction?.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.startFragment(fragment: Fragment, backStrackFlag: Boolean, @IdRes container: Int) {
    val fm = supportFragmentManager
    if (backStrackFlag) {
        val transaction = fm?.beginTransaction()
        try {
            transaction?.replace(container, fragment, fragment::class.java.name)
                    ?.addToBackStack(fragment::class.java.name)
                    ?.commit()
        } catch (e: IllegalStateException) {
            transaction?.commitAllowingStateLoss()
        }
    } else {
        val transaction = fm?.beginTransaction()
        try {
            transaction?.replace(container, fragment, fragment::class.java.name)
                    ?.commit()
        } catch (e: IllegalStateException) {
            transaction?.commitAllowingStateLoss()
        }
    }
}
