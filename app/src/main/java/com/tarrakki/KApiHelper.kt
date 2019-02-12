package com.tarrakki

import android.arch.lifecycle.MutableLiveData
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.soapmodel.PasswordRequest
import com.tarrakki.api.soapmodel.RequestBody
import com.tarrakki.api.soapmodel.RequestEnvelopeDownloadPANDetailsEKYC
import com.tarrakki.api.soapmodel.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import org.supportcompact.ktx.postError
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

fun addToCart(fundId: Int, sipAmount: String, lumpsumAmount: String)
        : MutableLiveData<ApiResponse> {
    val sip = if (sipAmount == "0.0") {
        null
    } else {
        sipAmount
    }
    val lumpsum = if (lumpsumAmount == "0.0") {
        null
    } else {
        lumpsumAmount
    }
    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .addtocart(fundId, sip, lumpsum),
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
    val sip = if (sipAmount == 0) {
        ""
    } else {
        sipAmount.toString()
    }
    val lumpsum = if (lumpsumAmount == 0) {
        ""
    } else {
        lumpsumAmount.toString()
    }

    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .investmentStrageyRecommeded(thirdLevelCategoryId,
                            lumpsum, addToCart, sip),
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
    val sip = if (sipAmount == 0) {
        ""
    } else {
        sipAmount.toString()
    }
    val lumpsum = if (lumpsumAmount == 0) {
        ""
    } else {
        lumpsumAmount.toString()
    }
    val apiResponse = MutableLiveData<ApiResponse>()
    if (isShowProgress) {
        EventBus.getDefault().post(SHOW_PROGRESS)
    }
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .investmentStrageyRecommeded(thirdLevelCategoryId, lumpsum, addToCart, sip),
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

const val PASSKEY = "S1DSS#q76S458G9h6u5DF7pk5T7Lpart"

fun getEncryptedPasswordForCAMPSApi(password: String = "AU82#bx"): MutableLiveData<String> {
    EventBus.getDefault().post(SHOW_PROGRESS)
    val apiResponse = MutableLiveData<String>()
    val body = PasswordRequest(PasswordRequest.GetPassword(password, PASSKEY))
    subscribeToSingle(ApiClient.getSOAPClient().create(WebserviceBuilder::class.java).requestPassword(RequestBody(body)),
            WebserviceBuilder.ApiNames.getEKYCPage,
            object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ResponseBody) {
                        apiResponse.value = o.resBody?.response?.getPasswordResult
                    } else {
                        postError(R.string.try_again_to)
                    }
                    /*try {
                        val data = o.toString()
                        val xmlJSON = XmlToJson.Builder(data).build()
                        val json = xmlJSON.toJson()
                        e("ApiResponse=>$json")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        e.postError(R.string.try_again_to)
                    }
                    EventBus.getDefault().post(DISMISS_PROGRESS)*/
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                }
            })
    return apiResponse
}

fun getEKYCData(password: String): MutableLiveData<String> {

    EventBus.getDefault().post(SHOW_PROGRESS)
    val apiResponse = MutableLiveData<String>()
    val reqEnvelope = RequestEnvelopeDownloadPANDetailsEKYC()
    val reqData = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC()
    val input = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC.APPREQROOTBean()
    val apppaninqBean = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC.APPREQROOTBean.APPPANINQBean()
    apppaninqBean.apppanno = "BAMPM9343K"
    apppaninqBean.appiopflg = "RS"
    apppaninqBean.appposcode = "infibeam\$10"
    input.apppaninq = apppaninqBean
    val appsummrec = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC.APPREQROOTBean.APPSUMMRECBean()
    appsummrec.appothkracode = "PLUTOWS"
    appsummrec.appothkrabatch = "SAU_468"
    appsummrec.appreqdate = "11-02-2019 12:25:00"
    appsummrec.apptotalrec = "1"
    input.appsummrec = appsummrec
    reqData.input = input
    reqData.userName = "PLUTOWS"
    reqData.passKey = PASSKEY
    reqData.posCode = "PA"
    reqData.password = password
    val reqBody = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody()
    reqBody.ekyc = reqData
    reqEnvelope.requestBody = reqBody
    subscribeToSingle(ApiClient.getSOAPClient().create(WebserviceBuilder::class.java).getEKYCDetails(reqEnvelope),
            WebserviceBuilder.ApiNames.getEKYCPage,
            object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ResponseBody) {
                        e("Data=>$o")
                    }
                    /*try {
                        val data = o.toString()
                        val xmlJSON = XmlToJson.Builder(data).build()
                        val json = xmlJSON.toJson()
                        e("ApiResponse=>$json")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        e.postError(R.string.try_again_to)
                    }
                    EventBus.getDefault().post(DISMISS_PROGRESS)*/
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                }
            })
    return apiResponse
}