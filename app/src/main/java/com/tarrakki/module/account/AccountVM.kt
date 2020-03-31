package com.tarrakki.module.account

import android.view.View
import androidx.annotation.DrawableRes
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.tarrakki.App
import com.tarrakki.BuildConfig
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.KYCStatusApiResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import com.tarrakki.getVisibility
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
    var kycRemark: String? = null
    var needToCheckStatus = false

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

    fun getKYCStatus(): MutableLiveData<ApiResponse> {
        showProgress()
        val apiResponse = MutableLiveData<ApiResponse>()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getKYCStatus(App.INSTANCE.getUserId()), object : SingleCallback1<ApiResponse> {
            override fun onSingleSuccess(o: ApiResponse) {
                dismissProgress()
                o.printResponse()
                if (o.status?.code == 1) {
                    val data = o.data?.parseTo<KYCStatusApiResponse>()
                    data?.data?.let {
                        it.isKycVerified?.let { it1 -> App.INSTANCE.setKYClVarified(it1) }
                        it.completeRegistration?.let { it1 -> App.INSTANCE.setCompletedRegistration(it1) }
                        it.readyToInvest?.let { it1 -> App.INSTANCE.setReadyToInvest(it1) }
                        it.kycStatus?.let { App.INSTANCE.setKYCStatus(it) }
                        it.isRemainingFields?.let { App.INSTANCE.setRemainingFields(it) }
                        kycRemark = it.kycRemark
                        apiResponse.value = o
                    }
                } else {
                    EventBus.getDefault().post(ShowError("${o.status?.message}"))
                }
            }

            override fun onFailure(throwable: Throwable) {
                dismissProgress()
                throwable.postError()
            }
        })
        return apiResponse
    }
}

data class AccountMenu(var title: String, @DrawableRes var imgRes: Int)

const val KYC_STATUS_APPROVED = 1
const val KYC_STATUS_REJECTED = 2
const val KYC_STATUS_UNDER_PROCESS = 4
const val KYC_STATUS_INCOMPLETE = 3

data class VideoKYCStatus(val status: Int = 0, private val kycRemark: String? = null) : WidgetsViewModel {

    val btnCompleteRegistrationVisibility: Int
        get() = (App.INSTANCE.getRemainingFields().toIntOrNull() == 1 || App.INSTANCE.getRemainingFields().toIntOrNull() == 2).getVisibility()
    val remark: String
        get() = App.INSTANCE.getString(R.string.your_kyc_application_was_rejected_due_to_xxx, kycRemark)

    override fun layoutId(): Int {
        return when (status) {
            KYC_STATUS_REJECTED -> R.layout.row_video_kyc_status_rejected
            KYC_STATUS_INCOMPLETE -> R.layout.row_video_kyc_status_incomplete
            KYC_STATUS_APPROVED -> R.layout.row_video_kyc_status_approved
            else -> R.layout.row_video_kyc_status_pending
        }
    }
}