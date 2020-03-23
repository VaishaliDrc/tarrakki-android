package com.tarrakki.module.account

import android.view.View
import androidx.annotation.DrawableRes
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.tarrakki.App
import com.tarrakki.BuildConfig
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.printResponse
import com.tarrakki.api.subscribeToSingle
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*

class AccountVM : FragmentViewModel() {

    val appLock = ObservableField(false)
    val accountMenus = arrayListOf<AccountMenu>()
    val bankVisibility = ObservableField(View.VISIBLE)
    val btnComleteRegion = ObservableField(false)
    var isAppLockClick = false
    val appVersion = ObservableField("V ${BuildConfig.VERSION_NAME}")
    val docStatus = arrayListOf<WidgetsViewModel>()
    val readyToInvest = object : WidgetsViewModel {
        override fun layoutId(): Int {
            return R.layout.row_ready_to_invest
        }
    }

    init {
        docStatus.add(readyToInvest)
        appLock.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                appLock.get()?.let {
                    App.INSTANCE.setAppIsLock(it)
                }
            }
        })
        setAccountMenu()
    }

    fun doLogout(): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).logout(App.INSTANCE.getUserId()),
                apiNames = WebserviceBuilder.ApiNames.onLogin,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                apiResponse.value = o
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {

                    }
                }
        )
        return apiResponse
    }

    fun setAccountMenu() {
        accountMenus.clear()
        if (App.INSTANCE.isCompletedRegistration()) {
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_profile), R.drawable.ic_my_profile))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.transactions), R.drawable.ic_transactions))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_portfolio), R.drawable.ic_my_portfolio))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_sip), R.drawable.ic_my_sip))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.saved_goal), R.drawable.ic_saved_goals))
            if (!App.INSTANCE.isSocialLogin()) {
                accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.change_password), R.drawable.ic_change_password))
            }
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.support), R.drawable.ic_support))
            //accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.notifications), R.drawable.ic_notifications))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.privacy_policy), R.drawable.ic_privacy_policy))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.terms_and_condditions), R.drawable.ic_terms_conditions))
        } else {
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.transactions), R.drawable.ic_transactions))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_portfolio), R.drawable.ic_my_portfolio))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.my_sip), R.drawable.ic_my_sip))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.saved_goal), R.drawable.ic_saved_goals))
            if (!App.INSTANCE.isSocialLogin()) {
                accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.change_password), R.drawable.ic_change_password))
            }
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.support), R.drawable.ic_support))
            //accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.notifications), R.drawable.ic_notifications))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.privacy_policy), R.drawable.ic_privacy_policy))
            accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.terms_and_condditions), R.drawable.ic_terms_conditions))
        }
        //accountMenus.add(AccountMenu(App.INSTANCE.getString(R.string.apply_for_debit_cart), R.drawable.ic_debit_cart))
    }
}

data class AccountMenu(var title: String, @DrawableRes var imgRes: Int)

const val KYC_STATUS_APPROVED = 1
const val KYC_STATUS_REJECTED = 2
const val KYC_STATUS_UNDER_PROCESS = 4
const val KYC_STATUS_INCOMPLETE = 3

data class VideoKYCStatus(val status: Int = 0) : WidgetsViewModel {
    override fun layoutId(): Int {
        return when (status) {
            KYC_STATUS_REJECTED -> R.layout.row_video_kyc_status_rejected
            KYC_STATUS_INCOMPLETE -> R.layout.row_video_kyc_status_incomplete
            KYC_STATUS_APPROVED -> R.layout.row_video_kyc_status_approved
            else -> R.layout.row_video_kyc_status_pending
        }
    }
}