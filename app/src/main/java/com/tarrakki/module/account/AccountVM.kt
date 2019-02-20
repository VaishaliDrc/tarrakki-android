package com.tarrakki.module.account

import android.databinding.Observable
import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.hasAppLock
import org.supportcompact.ktx.setAppIsLock

class AccountVM : FragmentViewModel() {

    val appLock = ObservableField(App.INSTANCE.hasAppLock())
    val accountMenus = arrayListOf<AccountMenu>()
    val logoutVisibility = ObservableField(View.VISIBLE/*if (App.INSTANCE.isLoggedIn.value != null && App.INSTANCE.isLoggedIn.value!!) View.VISIBLE else View.GONE*/)
    val btnComleteRegion = ObservableField(false)

    init {
        appLock.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                appLock.get()?.let {
                    App.INSTANCE.setAppIsLock(it)
                }
            }
        })
        setAccountMenu()
    }

    private fun setAccountMenu() {
        accountMenus.clear()
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_profile), R.drawable.ic_my_profile))
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.transactions), R.drawable.ic_transactions))
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_portfolio), R.drawable.ic_my_portfolio))
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.saved_goal), R.drawable.ic_saved_goals))
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.change_password), R.drawable.ic_change_password))
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.support), R.drawable.ic_support))
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.notifications), R.drawable.ic_notifications))
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.privacy_policy), R.drawable.ic_privacy_policy))
        accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.terms_and_condditions), R.drawable.ic_terms_conditions))
        /*if (App.INSTANCE.isLoggedIn.value!!) {
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_profile), R.drawable.ic_my_profile))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.transactions), R.drawable.ic_transactions))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_portfolio), R.drawable.ic_my_portfolio))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.saved_goal), R.drawable.ic_saved_goals))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.change_password), R.drawable.ic_change_password))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.support), R.drawable.ic_support))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.notifications), R.drawable.ic_notifications))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.privacy_policy), R.drawable.ic_privacy_policy))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.terms_and_condditions), R.drawable.ic_terms_conditions))
        } else {
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.login), R.drawable.ic_my_profile))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.support), R.drawable.ic_support))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.notifications), R.drawable.ic_notifications))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.privacy_policy), R.drawable.ic_privacy_policy))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.terms_and_condditions), R.drawable.ic_terms_conditions))
        }*/
    }
}

data class AccountMenu(var title: String, @DrawableRes var imgRes: Int)