package com.tarrakki.module.bankmandate

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.showProgress
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.subscribeToSingle
import kotlin.concurrent.thread

class BankMandateVM : FragmentViewModel() {

    val bankMandate = arrayListOf<WidgetsViewModel>()
    val isNextVisible = ObservableField<Boolean>(false)
    val isAddVisible = ObservableField<Boolean>(false)
    val isMandateBankList = ObservableField<Boolean>(false)
    val isNoBankAccount = ObservableField<Boolean>(false)
    val isSelectBankVisible = ObservableField<Boolean>(false)

    init {
        bankMandate.add(BankMandate(
                "ICICI Bank",
                "1210000091052",
                "Ahmedabad",
                "IC1C00000457",
                true))
        bankMandate.add(object : WidgetsViewModel {
            override fun layoutId(): Int {
                return R.layout.btn_add_next_bank_mandate
            }
        })
    }

    fun getAllBanks(isRefreshing: Boolean = false): MutableLiveData<UserBanksResponse> {
        if (!isRefreshing)
            showProgress()
        val response = MutableLiveData<UserBanksResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getUserBanks(App.INSTANCE.getUserId()),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            thread {
                                if (o.status?.code == 1) {
                                    val data = o.data?.parseTo<UserBanksResponse>()
                                    response.postValue(data)
                                } else {
                                    EventBus.getDefault().post(ShowError("${o.status?.message}"))
                                }
                                dismissProgress()
                            }
                        } else {
                            dismissProgress()
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

    fun getAllMandateBanks(isRefreshing: Boolean = false): MutableLiveData<UserBankMandateResponse> {
        if (!isRefreshing)
        showProgress()
        val response = MutableLiveData<UserBankMandateResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getUserMandateBanks(App.INSTANCE.getUserId()),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            thread {
                                if (o.status?.code == 1) {
                                    val data = o.data?.parseTo<UserBankMandateResponse>()
                                    response.postValue(data)
                                } else {
                                    EventBus.getDefault().post(ShowError("${o.status?.message}"))
                                }
                                dismissProgress()
                            }
                        } else {
                            dismissProgress()
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

data class BankMandate(var name: String,
                       var accountNumber: String,
                       var breachName: String,
                       var IFSCCode: String,
                       var isDefault: Boolean = false
) : WidgetsViewModel, BaseObservable() {

    @get:Bindable
    var isPending: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.pending)
        }

    override fun layoutId(): Int {
        return R.layout.row_bank_mandate_list_item
    }
}