package com.tarrakki.module.account

import android.databinding.Observable
import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.hasAppLock
import org.supportcompact.ktx.isLogin
import org.supportcompact.ktx.setAppIsLock

class AccountVM : FragmentViewModel() {

    val appLock = ObservableField(App.INSTANCE.hasAppLock())
    val accountMenus = arrayListOf<AccountMenu>()
    val logoutVisibility = ObservableField(if (App.INSTANCE.isLogin()) View.VISIBLE else View.GONE)

    init {
        appLock.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                appLock.get()?.let {
                    App.INSTANCE.setAppIsLock(it)
                }
            }
        })
        accountMenus.add(AccountMenu("My Profile", R.mipmap.ic_launcher_round))
        accountMenus.add(AccountMenu("Transactions", R.mipmap.ic_launcher_round))
        accountMenus.add(AccountMenu("Future Order", R.mipmap.ic_launcher_round))
        accountMenus.add(AccountMenu("My Portfolio", R.mipmap.ic_launcher_round))
        accountMenus.add(AccountMenu("Saved Goal", R.mipmap.ic_launcher_round))
        accountMenus.add(AccountMenu("Support", R.mipmap.ic_launcher_round))
        accountMenus.add(AccountMenu("Notification", R.mipmap.ic_launcher_round))
        accountMenus.add(AccountMenu("Privacy Policy", R.mipmap.ic_launcher_round))
        accountMenus.add(AccountMenu("Terms & Conditions", R.mipmap.ic_launcher_round))

    }

}

data class AccountMenu(var title: String, @DrawableRes var imgRes: Int)