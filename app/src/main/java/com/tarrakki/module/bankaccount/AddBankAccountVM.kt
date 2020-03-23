package com.tarrakki.module.bankaccount

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import com.tarrakki.module.ekyc.KYCData
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.showProgress
import kotlin.concurrent.thread

class AddBankAccountVM : FragmentViewModel() {

    val name = ObservableField<String>("")
    val accountNo = ObservableField<String>("")
    val reenterAccountNo = ObservableField<String>("")
    val accountType = ObservableField<String>("Saving")
    val IFSCCode = ObservableField<String>("")
    val response = MutableLiveData<BankResponse>()
    val responseAddBankDetails = MutableLiveData<UserBanksResponse>()
    var labelButton = ObservableField<String>("")


    fun getAllBanks(): MutableLiveData<BankResponse> {
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getAllBanks(),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            thread {
                                if (o.status?.code == 1) {
                                    val data = o.data?.parseTo<BankResponse>()
                                    data?.banks
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


    fun addBankDetails(bankId: String): MutableLiveData<UserBanksResponse> {
        val response = MutableLiveData<UserBanksResponse>()
        showProgress()
        val json = JsonObject()
        json.addProperty("account_number", accountNo.get())
        json.addProperty("ifsc_code", "${IFSCCode.get()}".toUpperCase())
        json.addProperty("account_type", if (accountType.get() == "Saving") "SB" else "CB")
        json.addProperty("bank_id", bankId)
        json.addProperty("user_id", App.INSTANCE.getUserId())
        //json.addProperty("holding_mode", "Single")
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).addBankDetails(data),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                response.value = o.data?.parseTo<UserBanksResponse>()
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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

    fun updateBankDetails(bankId: String, bankUserId: String): MutableLiveData<UserBanksResponse> {
        val response = MutableLiveData<UserBanksResponse>()
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).updateUserBank(
                        accountNo.get().toString(),
                        IFSCCode.get().toString(),
                        if (accountType.get() == "Saving") "SB" else "CB",
                        App.INSTANCE.getUserId(),
                        bankId,
                        bankUserId),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                response.value = o.data?.parseTo<UserBanksResponse>()
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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