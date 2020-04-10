package org.supportcompact.ktx

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AppCompatActivity


fun AppCompatActivity.startFragment(fragment: androidx.fragment.app.Fragment, @IdRes container: Int) {
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

fun AppCompatActivity.startFragment(fragment: androidx.fragment.app.Fragment, targetFragment: androidx.fragment.app.Fragment, requestCode: Int, @IdRes container: Int) {
    fragment.setTargetFragment(targetFragment, requestCode)
    val fm = supportFragmentManager
    val transaction = fm?.beginTransaction()
    try {
        transaction?.replace(container, fragment, fragment::class.java.name)
                ?.addToBackStack(fragment::class.java.name)
                ?.commit()
    } catch (e: IllegalStateException) {
        try {
            transaction?.commitAllowingStateLoss()
        } catch (e: Exception) {
        }
    }
}

fun androidx.fragment.app.Fragment.startFragment(fragment: androidx.fragment.app.Fragment, @IdRes container: Int) {
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

fun androidx.fragment.app.FragmentActivity.startFragment(fragment: androidx.fragment.app.Fragment, @IdRes container: Int) {
    val fm = this.supportFragmentManager
    val transaction = fm?.beginTransaction()
    try {
        transaction?.replace(container, fragment, fragment::class.java.name)
                ?.addToBackStack(fragment::class.java.name)
                ?.commit()
    } catch (e: IllegalStateException) {
        try {
            transaction?.commitAllowingStateLoss()
        } catch (e: Exception) {
        }
    }
}

fun androidx.fragment.app.Fragment.startFragmentWithoutBackStack(fragment: androidx.fragment.app.Fragment, @IdRes container: Int) {
    val fm = activity?.supportFragmentManager
    val transaction = fm?.beginTransaction()
    try {
        transaction?.replace(container, fragment, fragment::class.java.name)
                ?.addToBackStack(fragment::class.java.name)
                ?.commit()
    } catch (e: IllegalStateException) {
        try {
            transaction?.commitAllowingStateLoss()
        } catch (e: Exception) {
        }
    }
}

fun AppCompatActivity.startFragment(fragment: androidx.fragment.app.Fragment, backStrackFlag: Boolean, @IdRes container: Int) {
    val fm = supportFragmentManager
    if (backStrackFlag) {
        val transaction = fm?.beginTransaction()
        try {
            transaction?.replace(container, fragment, fragment::class.java.name)
                    ?.addToBackStack(fragment::class.java.name)
                    ?.commit()
        } catch (e: IllegalStateException) {
            try {
                transaction?.commitAllowingStateLoss()
            } catch (e: Exception) {
            }
        }
    } else {
        val transaction = fm?.beginTransaction()
        try {
            transaction?.replace(container, fragment, fragment::class.java.name)
                    ?.commit()
        } catch (e: IllegalStateException) {
            try {
                transaction?.commitAllowingStateLoss()
            } catch (e: Exception) {
            }
        }
    }
}
