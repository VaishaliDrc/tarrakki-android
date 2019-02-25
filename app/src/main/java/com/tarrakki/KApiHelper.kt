package com.tarrakki

import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.soapmodel.*
import com.tarrakki.module.ekyc.KYCData
import org.greenrobot.eventbus.EventBus
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.ApiClient.CAMS_PASSWORD
import org.supportcompact.networking.ApiClient.CAMS_USER_ID
import org.supportcompact.networking.ApiClient.PASSKEY
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.math.BigInteger
import java.util.*
import kotlin.concurrent.thread

fun addToCart(fundId: Int, sipAmount: String, lumpsumAmount: String)
        : MutableLiveData<ApiResponse> {
    val json = JsonObject()
    json.addProperty("fund_id", fundId)
    if (sipAmount != BigInteger.ZERO.toString()) {
        json.addProperty("sip_amount", sipAmount)
    }
    if (lumpsumAmount != BigInteger.ZERO.toString()) {
        json.addProperty("lumpsum_amount", lumpsumAmount)
    }
    json.printRequest()
    val data = json.toString().toEncrypt()

    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .addtocart(data),
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

fun addToCartPortfolio(fundId: Int, sipAmount: String, lumpsumAmount: String, folioNo : String)
        : MutableLiveData<ApiResponse> {
    val json = JsonObject()
    json.addProperty("fund_id", fundId)
    if (sipAmount != BigInteger.ZERO.toString()) {
        json.addProperty("sip_amount", sipAmount)
    }
    if (lumpsumAmount != BigInteger.ZERO.toString()) {
        json.addProperty("lumpsum_amount", lumpsumAmount)
    }
    if (folioNo.isNotEmpty()){
        json.addProperty("folio_number", folioNo)
    }
    json.printRequest()
    val data = json.toString().toEncrypt()

    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .addtocart(data),
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

fun investmentRecommendation(thirdLevelCategoryId: Int, sipAmount: BigInteger,
                             lumpsumAmount: BigInteger, addToCart: Int, isShowProgress: Boolean = true)
        : MutableLiveData<InvestmentRecommendFundResponse> {
    val apiResponse = MutableLiveData<InvestmentRecommendFundResponse>()
    if (isShowProgress) {
        EventBus.getDefault().post(SHOW_PROGRESS)
    }
    val sip = if (sipAmount == BigInteger.ZERO) {
        ""
    } else {
        sipAmount.toString()
    }
    val lumpsum = if (lumpsumAmount == BigInteger.ZERO) {
        ""
    } else {
        lumpsumAmount.toString()
    }

    val json = JsonObject()
    json.addProperty("third_level_category_id", thirdLevelCategoryId)
    json.addProperty("lumpsum_amount", lumpsum)
    json.addProperty("add_to_cart", addToCart)
    json.addProperty("sip_amount", sip)
    val data = json.toString().toEncrypt()

    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .investmentStrageyRecommeded(data),
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

fun investmentRecommendationToCart(thirdLevelCategoryId: Int, sipAmount: String, lumpsumAmount: String,
                                   addToCart: Int, isShowProgress: Boolean = true)
        : MutableLiveData<ApiResponse> {
    val sip = if (sipAmount == "0") {
        ""
    } else {
        sipAmount
    }
    val lumpsum = if (lumpsumAmount == "0") {
        ""
    } else {
        lumpsumAmount
    }
    val apiResponse = MutableLiveData<ApiResponse>()
    if (isShowProgress) {
        EventBus.getDefault().post(SHOW_PROGRESS)
    }
    val json = JsonObject()
    json.addProperty("third_level_category_id", thirdLevelCategoryId)
    json.addProperty("lumpsum_amount", lumpsum)
    json.addProperty("add_to_cart", addToCart)
    json.addProperty("sip_amount", sip)
    val data = json.toString().toEncrypt()
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .investmentStrageyRecommeded(data),
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

//const val PASSWORD = "kra\$36369"
//const val PASSKEY = "SajanGandhi"

fun getEncryptedPasswordForCAMPSApi(): MutableLiveData<String> {
    EventBus.getDefault().post(SHOW_PROGRESS)
    val apiResponse = MutableLiveData<String>()
    val body = PasswordRequest(PasswordRequest.GetPassword(CAMS_PASSWORD, PASSKEY))
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
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                }
            })
    return apiResponse
}

fun getPANeKYCStatus(password: String, pan: String): MutableLiveData<List<String>> {

    EventBus.getDefault().post(SHOW_PROGRESS)
    val apiResponse = MutableLiveData<List<String>>()
    val reqEnvelope = VerifyPANDetails()
    val reqData = VerifyPANDetails.RequestBody.PANDetailsEKYC()
    val input = VerifyPANDetails.RequestBody.PANDetailsEKYC.APPREQROOTBean()
    val apppaninqBean = VerifyPANDetails.RequestBody.PANDetailsEKYC.APPREQROOTBean.APPPANINQBean()
    apppaninqBean.apppanno = pan//"BAMPM9343K"
    apppaninqBean.panDOB = ""
    apppaninqBean.appiopflg = "RE"
    apppaninqBean.appposcode = "infibeam\$10"

    input.apppaninq = apppaninqBean
    val appsummrec = VerifyPANDetails.RequestBody.PANDetailsEKYC.APPREQROOTBean.APPSUMMRECBean()
    appsummrec.appothkracode = "PLUTOWS"
    appsummrec.appothkrabatch = "SAU_468"
    appsummrec.appreqdate = "${Date().convertTo("dd-MM-yyyy HH:mm:ss")}"
    appsummrec.apptotalrec = "1"
    input.appsummrec = appsummrec
    reqData.input = VerifyPANDetails.RequestBody.PANDetailsEKYC.InputXML(input)
    reqData.userName = CAMS_USER_ID
    reqData.passKey = PASSKEY
    reqData.posCode = "PA"
    reqData.password = password
    val reqBody = VerifyPANDetails.RequestBody()
    reqBody.ekyc = reqData
    reqEnvelope.requestBody = reqBody
    subscribeToSingle(ApiClient.getSOAPClient().create(WebserviceBuilder::class.java).getPANeKYCStates(reqEnvelope),
            WebserviceBuilder.ApiNames.getEKYCPage,
            object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ResponseKYCStates) {
                        val data = o.body?.verifyPANDetailsEKYCResponse?.verifyPANDetailsEKYCResult?.appresroot?.apppaninq;
                        val kycStates: List<String> = arrayListOf(
                                "${data?.camskra}",
                                "${data?.cvlkra}",
                                "${data?.ndmlkra}",
                                "${data?.dotexkra}",
                                "${data?.karvykra}")
                        apiResponse.value = kycStates
                    }
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                    throwable.printStackTrace()
                }
            })
    return apiResponse
}


fun getEKYCData(password: String, kycData: KYCData): MutableLiveData<KYCData> {

    EventBus.getDefault().post(SHOW_PROGRESS)
    val apiResponse = MutableLiveData<KYCData>()
    val reqEnvelope = RequestEnvelopeDownloadPANDetailsEKYC()
    val reqData = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC()
    val input = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC.APPREQROOTBean()
    val apppaninqBean = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC.APPREQROOTBean.APPPANINQBean()
    apppaninqBean.apppanno = kycData.pan
    apppaninqBean.appiopflg = "RE"
    apppaninqBean.appposcode = "infibeam\$10"
    apppaninqBean.panDOB = ""
    input.apppaninq = apppaninqBean
    val appsummrec = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC.APPREQROOTBean.APPSUMMRECBean()
    appsummrec.appothkracode = "PLUTOWS"
    appsummrec.appothkrabatch = "SAU_468"
    appsummrec.appreqdate = "${Date().convertTo("dd-MM-yyyy HH:mm:ss")}"
    appsummrec.apptotalrec = "1"
    input.appsummrec = appsummrec
    reqData.input = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody.DownloadPANDetailsEKYC.InputXML(input)
    reqData.userName = CAMS_USER_ID
    reqData.passKey = PASSKEY
    reqData.posCode = "PA"
    reqData.password = password
    val reqBody = RequestEnvelopeDownloadPANDetailsEKYC.RequestBody()
    reqBody.ekyc = reqData
    reqEnvelope.requestBody = reqBody
    subscribeToSingle(ApiClient.getSOAPClient().create(WebserviceBuilder::class.java).getEKYCData(reqEnvelope),
            WebserviceBuilder.ApiNames.getEKYCPage,
            object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ResponseKYCData) {
                        val data = o.body?.downloadPANDetailsEKYCResponse?.downloadPANDetailsEKYCResult?.appresroot?.apppaninq
                        if (data != null && "R".equals(data.appresstatus, true)) {
                            //kyc.mobile = data.appmobno
                            //kyc.email = data.appemail
                            kycData.nameOfPANHolder = data.appname
                            kycData.fullName = data.appname
                            kycData.OCCcode = data.appocc
                            kycData.dob = data.appdobdt.toDate("dd-MM-yyyy").convertTo("dd MMM, yyyy")
                                    ?: ""
                            kycData.gender = data.appgen
                            kycData.address = "${data.appperadD1}, ${data.appperadD2}, ${data.appperadD3}"
                            kycData.pincode = data.appperpincd
                            kycData.city = data.apppercity
                            kycData.state = data.appperstate
                            kycData.country = data.appperctry
                            kycData.addressType =   "01"
                            apiResponse.value = kycData
                        } else if (data == null) {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        } else {
                            EventBus.getDefault().post(ShowError("We allowing only resident of India"))
                        }
                    }
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                }
            })
    return apiResponse
}

fun redeemPortfolio(data: String)
        : MutableLiveData<ApiResponse> {
    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .redeemPortfolio(data),
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

fun stopPortfolio(transactionId: Int)
        : MutableLiveData<ApiResponse> {
    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .stopPortfolio(transactionId),
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
