package com.tarrakki

import android.arch.lifecycle.MutableLiveData
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

fun addToCart(fundId: Int, sipAmount: String, lumpsumAmount: String)
        : MutableLiveData<ApiResponse> {
    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .addtocart(fundId, sipAmount, lumpsumAmount),
            apiNames = WebserviceBuilder.ApiNames.addtocart,
            singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ApiResponse) {
                        e("Api Response=>${o.data?.toDecrypt()}")
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
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    EventBus.getDefault().post(ShowError("${throwable.message}"))
                }
            }
    )
    return apiResponse
}

fun getCartItem(showProgress: Boolean): MutableLiveData<CartData> {
    val apiResponse = MutableLiveData<CartData>()
    if (showProgress) {
        EventBus.getDefault().post(SHOW_PROGRESS)
    }
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getCartItem(),
            apiNames = WebserviceBuilder.ApiNames.getCartItem,
            singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    if (showProgress) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                    }
                    if (o is ApiResponse) {
                        if ((o.status?.code == 1)) {
                            thread {
                                val data = o.data?.parseTo<CartData>()
                                apiResponse.postValue(data)
                            }
                        } else {
                            EventBus.getDefault().post(ShowError("${o.status?.message}"))
                        }
                    } else {
                        EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                    }
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    if (showProgress) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                    }
                    EventBus.getDefault().post(ShowError("${throwable.message}"))
                }
            }
    )
    return apiResponse
}

fun investmentRecommendation(thirdLevelCategoryId: Int, sipAmount: Int, lumpsumAmount: Int, addToCart: Int, isShowProgress: Boolean = true)
        : MutableLiveData<InvestmentRecommendFundResponse> {
    val apiResponse = MutableLiveData<InvestmentRecommendFundResponse>()
    if (isShowProgress) {
        EventBus.getDefault().post(SHOW_PROGRESS)
    }
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .investmentStrageyRecommeded(thirdLevelCategoryId, lumpsumAmount, addToCart, sipAmount),
            apiNames = WebserviceBuilder.ApiNames.addtocart,
            singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ApiResponse) {
                        e("Api Response=>${o.data?.toDecrypt()}")
                        if (o.status?.code == 1) {
                            thread {
                                val data = o.data?.parseTo<InvestmentRecommendFundResponse>()
                                apiResponse.postValue(data)
                            }
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

fun investmentRecommendationToCart(thirdLevelCategoryId: Int, sipAmount: Int, lumpsumAmount: Int,
                             addToCart: Int, isShowProgress: Boolean = true)
        : MutableLiveData<ApiResponse> {
    val apiResponse = MutableLiveData<ApiResponse>()
    if (isShowProgress) {
        EventBus.getDefault().post(SHOW_PROGRESS)
    }
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .investmentStrageyRecommeded(thirdLevelCategoryId, lumpsumAmount, addToCart, sipAmount),
            apiNames = WebserviceBuilder.ApiNames.addtocart,
            singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ApiResponse) {
                        e("Api Response=>${o.data?.toDecrypt()}")
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
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    EventBus.getDefault().post(ShowError("${throwable.message}"))
                }
            }
    )
    return apiResponse
}