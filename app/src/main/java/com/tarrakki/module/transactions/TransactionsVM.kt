package com.tarrakki.module.transactions

import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import com.tarrakki.module.transactionConfirm.TransactionConfirmVM
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import kotlin.concurrent.thread

class TransactionsVM : FragmentViewModel() {

    val hasOptionMenu = MutableLiveData<Boolean>()
    val onBack = MutableLiveData<Boolean>()
    val onRefresh = MutableLiveData<Boolean>()

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
                                if (TransactionApiResponse.ALL == transactionType)
                                    apiResponse.value = null
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

    fun deleteTransactions(jsonArray: JsonArray): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        showProgress()
        val json = JsonObject()
        json.add("transaction_ids", jsonArray)
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).deleteUnpaidTransactions(App.INSTANCE.getUserId(), data),
                apiNames = WebserviceBuilder.ApiNames.transactions,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                apiResponse.value = o
                            } else {
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

    fun setData(statuslist: ArrayList<TransactionConfirmVM.TranscationStatuss>, payment: String, orderPlaced: String, unitsAllocated: String, paymentType: String) {
        statuslist.add(TransactionConfirmVM.TranscationStatuss("Mutual Fund Payment", paymentType, payment))
        statuslist.add(TransactionConfirmVM.TranscationStatuss("Order Placed with AMC", "", orderPlaced))
        statuslist.add(TransactionConfirmVM.TranscationStatuss("Units Alloted", "", unitsAllocated))
    }

    fun setRedeemData(statuslist: ArrayList<TransactionConfirmVM.TranscationStatuss>, withdrawalSent: String, withdrawalConfirm: String, amountCreadited: String, isRelianceRedemption: Boolean) {
        statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Sent to AMC", "", withdrawalSent))
        statuslist.add(TransactionConfirmVM.TranscationStatuss("Withdrawal Confirmation", "", withdrawalConfirm))
        if (isRelianceRedemption)
            statuslist.add(TransactionConfirmVM.TranscationStatuss("Amount Credited", "", amountCreadited))
    }

}