package com.tarrakki.module.confirmorder

import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.ConfirmOrderResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class ConfirmOrderVM : FragmentViewModel() {

    val apiResponse = MutableLiveData<ConfirmOrderResponse>()

    fun getConfirmOrder(): MutableLiveData<ConfirmOrderResponse> {
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getConfirmOrder(App.INSTANCE.getUserId()),
                apiNames = WebserviceBuilder.ApiNames.deleteCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val data = o.data?.parseTo<ConfirmOrderResponse>()
                                apiResponse.value = data
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        } else {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        EventBus.getDefault().post(ShowError("${throwable.message}"))
                    }
                }
        )
        return apiResponse
    }


    fun updateFirstSIPFlag(data: ConfirmOrderResponse.Data.OrderLine, firstSIP: Boolean): MutableLiveData<ApiResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val apiResponse = MutableLiveData<ApiResponse>()
        val json = JsonObject()
        json.addProperty("fund_id_id", "${data.fundIdId}")
        json.addProperty("lumpsum_amount", "${data.lumpsumAmount?.toCurrencyBigInt()}")
        json.addProperty("sip_amount", "${data.sipAmount?.toCurrencyBigInt()}")
        json.addProperty("first_order_today", firstSIP)
        e("Plain Data=>", json.toString())
        val authData = AES.encrypt(json.toString())
        e("Encrypted Data=>", authData)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .updateFirstSIPFlag("${data.id}", authData),
                apiNames = WebserviceBuilder.ApiNames.updateCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                apiResponse.value = o
                                EventBus.getDefault().post(ShowError(o.status.message))
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
        return apiResponse
    }


}

class OrderTotal : WidgetsViewModel {

    var total: Double = 0.0
    var bank: String = ""

    override fun layoutId(): Int {
        return R.layout.row_order_total
    }
}

