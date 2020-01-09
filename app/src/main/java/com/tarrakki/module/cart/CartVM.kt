package com.tarrakki.module.cart

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.Event
import org.supportcompact.events.EventData
import org.supportcompact.events.ShowECutOffTimeDialog
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import java.math.BigInteger

class CartVM : FragmentViewModel() {

    var funds = arrayListOf<CartData.Data.OrderLine>()
    val userGoalList = MutableLiveData<CartData>()
    var totalSip: ObservableField<String> = ObservableField("")
    var totalLumpsum: ObservableField<String> = ObservableField("")
    val cartUpdate = MutableLiveData<ApiResponse>()
    val isEmptyCart = ObservableField<Boolean>(true)


    fun getConfirmOrder(): MutableLiveData<ConfirmOrderResponse> {
        showProgress()
        val apiResponse = MutableLiveData<ConfirmOrderResponse>()
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
                            } else if (o.status?.code == 5) {
                                EventBus.getDefault().post(
                                        ShowECutOffTimeDialog(
                                                title = App.INSTANCE.getString(R.string.cut_of_time_title),
                                                error = App.INSTANCE.getString(R.string.cut_of_desc_code_5),
                                                msg = "${o.status.message}"))
                            } else if (o.status?.code == 6) {
                                EventBus.getDefault().post(
                                        ShowECutOffTimeDialog(
                                                title = App.INSTANCE.getString(R.string.cut_of_time_title),
                                                error = App.INSTANCE.getString(R.string.cut_of_msg_code_6),
                                                msg = "${o.status.message}"))
                            } else if (o.status?.code == 7) {
                                /**There is no mandate find with any bank need to redirect to bank mandate form*/
                                EventBus.getDefault().post(EventData(Event.REDIRECT_TO_BANK_MANDATE, "${o.status?.message}"))
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

    fun getCartItem(): MutableLiveData<CartData> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getCartItem(),
                apiNames = WebserviceBuilder.ApiNames.getCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                val data = o.data?.parseTo<CartData>()
                                userGoalList.value = data
                            } else {
                                //isEmptyCart.value = o.status
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
        return userGoalList
    }

    fun deleteGoalFromCart(id: String): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).deleteCartItem(id),
                apiNames = WebserviceBuilder.ApiNames.deleteCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            e("Api Response=>${o.data?.toDecrypt()}")
                            o.printResponse()
                            if (o.status?.code == 1) {
                                apiResponse.value = o
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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

    fun updateGoalFromCart(id: String, fund: CartData.Data.OrderLine): MutableLiveData<ApiResponse> {
        if (fund.sipAmount.toCurrencyBigInt() == BigInteger.ZERO) {
            fund.day = ""
        }
        if (fund.day == null || fund.day == "") {
            fund.day = "0"
        }
        val lumpsump = fund.lumpsumAmount.toCurrencyBigInt().toString()
        val sip = fund.sipAmount.toCurrencyBigInt().toString()

        val json = JsonObject()
        json.addProperty("fund_id_id", fund.fundIdId.toString())
        json.addProperty("lumpsum_amount", lumpsump)
        json.addProperty("day", fund.day)
        json.addProperty("sip_amount", sip)
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .updateCartItem(id, data),
                apiNames = WebserviceBuilder.ApiNames.updateCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            e("Api Response=>${o.data?.toDecrypt()}")
                            o.printResponse()
                            if (o.status?.code == 1) {
                                cartUpdate.value = o
                                //EventBus.getDefault().post(ShowError("${o.status?.message}"))
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
        return cartUpdate
    }

}