package com.tarrakki.module.bankaccount

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

class AddBankAccountVM : FragmentViewModel() {

    val name = ObservableField<String>("")
    val accountNo = ObservableField<String>("")
    val reenterAccountNo = ObservableField<String>("")
    val breachName = ObservableField<String>("")
    val accountType = ObservableField<String>("")
    val IFSCCode = ObservableField<String>("")
    val response = MutableLiveData<BankResponse>()

    fun getAllBanks(): MutableLiveData<BankResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getAllBanks(),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            thread {
                                if (o.status?.code == 1) {
                                    val data = o.data?.parseTo<BankResponse>()
                                    data?.banks
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

    fun addBankDetails(bankId: String): MutableLiveData<ApiResponse> {
        val response = MutableLiveData<ApiResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("account_number", accountNo.get())
        json.addProperty("ifsc_code", IFSCCode.get())
        json.addProperty("micr_code", "380240005")
        json.addProperty("account_type", if (accountType.get() == "Saving Account") "SB" else "CB")
        json.addProperty("bank_id", bankId)
        json.addProperty("holding_mode", "Single")
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).addBankDetails(data),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                response.value = o
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