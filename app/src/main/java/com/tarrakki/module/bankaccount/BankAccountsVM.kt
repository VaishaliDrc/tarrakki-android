package com.tarrakki.module.bankaccount

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.StringRes
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.UserBanksResponse
import com.tarrakki.api.model.parseTo
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

class BankAccountsVM : FragmentViewModel() {

    fun getAllBanks(): MutableLiveData<UserBanksResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val response = MutableLiveData<UserBanksResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getUserBanks(),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            thread {
                                if (o.status?.code == 1) {
                                    val data = o.data?.parseTo<UserBanksResponse>()
                                    response.postValue(data)
                                } else {
                                    EventBus.getDefault().post(ShowError("${o.status?.message}"))
                                }
                            }
                        } else {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        EventBus.getDefault().post(ShowError("${throwable.message}"))
                    }
                }
        )
        return response
    }
}

class NoBankAccount : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_no_bank_account_yet
    }
}

data class SingleButton(@StringRes var title: Int) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.btn_add_bank_account
    }
}

data class Bank(
        var name: String,
        var accountNumber: String,
        var breachName: String,
        var IFSCCode: String,
        var isDefault: Boolean = false
) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_bank_account_list_item
    }
}