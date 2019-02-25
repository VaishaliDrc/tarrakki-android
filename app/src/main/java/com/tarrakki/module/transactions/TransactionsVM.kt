package com.tarrakki.module.transactions

import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

class TransactionsVM : FragmentViewModel() {

    val hasOptionMenu = MutableLiveData<Boolean>()
    val onBack = MutableLiveData<Boolean>()

    fun getTransactions(transactionType: String = TransactionApiResponse.ALL, offset: Int = 0, mRefresh: Boolean = false): MutableLiveData<TransactionApiResponse> {
        if (offset == 0 && !mRefresh)
            showProgress()
        val apiResponse = MutableLiveData<TransactionApiResponse>()
        val json = JsonObject()
        json.addProperty("limit", 10)
        json.addProperty("offset", offset)
        json.addProperty("transaction_type", transactionType)
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getTransactions(App.INSTANCE.getUserId(), data),
                apiNames = WebserviceBuilder.ApiNames.transactions,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                thread {
                                    val response = o.data?.parseTo<TransactionApiResponse>()
                                    apiResponse.postValue(response)
                                    if (!mRefresh && TransactionApiResponse.ALL == transactionType)
                                        dismissProgress()
                                }
                            } else {
                                dismissProgress()
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        } else {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        throwable.postError()
                    }
                }
        )
        return apiResponse
    }

}