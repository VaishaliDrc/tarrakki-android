package com.tarrakki.module.cart

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import org.supportcompact.ktx.toCurrency
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class CartVM : FragmentViewModel() {

    var funds = arrayListOf<CartData.Data.OrderLine>()
    val userGoalList = MutableLiveData<CartData>()
    var totalSip: ObservableField<String> = ObservableField("")
    var totalLumpsum: ObservableField<String> = ObservableField("")
    val cartUpdate = MutableLiveData<ApiResponse>()

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
        fund.date = ""
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).updateCartItem(id,
                        fund.fundIdId.toString(), fund.lumpsumAmount.toCurrency(), fund.sipAmount.toCurrency()),
                apiNames = WebserviceBuilder.ApiNames.updateCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            e("Api Response=>${o.data?.toDecrypt()}")
                            o.printResponse()
                            if (o.status?.code == 1) {
                                cartUpdate.value = o
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
        return cartUpdate
    }

}