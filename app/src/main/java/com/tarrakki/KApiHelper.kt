package com.tarrakki

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import com.tarrakki.module.ekyc.KYCData
import com.tarrakki.module.ekyc.eventKYCDataLog
import com.tarrakki.module.tarrakkipro.TarrakkiProBenefitsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import java.math.BigInteger
import kotlin.concurrent.thread

fun addToCart(fundId: Int, sipAmount: String, lumpsumAmount: String, folioNo: String? = null)
        : MutableLiveData<ApiResponse> {
    val json = JsonObject()
    json.addProperty("fund_id", fundId)
    if (sipAmount != BigInteger.ZERO.toString()) {
        json.addProperty("sip_amount", sipAmount)
    }
    if (lumpsumAmount != BigInteger.ZERO.toString()) {
        json.addProperty("lumpsum_amount", lumpsumAmount)
    }
    if (folioNo?.isNotEmpty() == true) {
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

fun addToCartTarrakkiZyaada(tarrakkiZyaadaId: String, sipAmount: String, lumpsumAmount: String, folioNo: String? = null)
        : MutableLiveData<ApiResponse> {
    val json = JsonObject()
    json.addProperty("user_id", App.INSTANCE.getUserId())
    if (sipAmount != BigInteger.ZERO.toString()) {
        json.addProperty("sip_amount", sipAmount)
    }
    if (lumpsumAmount != BigInteger.ZERO.toString()) {
        json.addProperty("lumpsum_amount", lumpsumAmount)
    }

    if (folioNo?.isNotEmpty() == true) {
        json.addProperty("folio_number", folioNo)
    }

    json.printRequest()
    val data = json.toString().toEncrypt()

    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .addToCartTarrakkiZyaada(tarrakkiZyaadaId, data),
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

fun addToCartGoalPortfolio(fundId: Int, sipAmount: String, lumpsumAmount: String,
                           folioNo: String, goal_id: Int?)
        : MutableLiveData<ApiResponse> {
    val json = JsonObject()
    json.addProperty("fund_id", fundId)
    if (sipAmount != BigInteger.ZERO.toString()) {
        json.addProperty("sip_amount", sipAmount)
    }
    if (lumpsumAmount != BigInteger.ZERO.toString()) {
        json.addProperty("lumpsum_amount", lumpsumAmount)
    }
    if (folioNo.isNotEmpty()) {
        json.addProperty("folio_number", folioNo)
    }
    json.addProperty("user_goal", goal_id)
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

fun addToCartPortfolio(fundId: Int, sipAmount: String, lumpsumAmount: String, folioNo: String, tzId: String? = null): MutableLiveData<ApiResponse> {
    val json = JsonObject()
    json.addProperty("fund_id", fundId)
    if (sipAmount != BigInteger.ZERO.toString()) {
        json.addProperty("sip_amount", sipAmount)
    }
    if (lumpsumAmount != BigInteger.ZERO.toString()) {
        json.addProperty("lumpsum_amount", lumpsumAmount)
    }
    if (folioNo.isNotEmpty()) {
        json.addProperty("folio_number", folioNo)
    }
    if (tzId != null) {
        json.addProperty("tz_id", tzId)
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

fun investmentRecommendation(fragment: Fragment,navigateToTPro: Boolean,thirdLevelCategoryId: Int, sipAmount: BigInteger,
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
    json.printRequest()
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
                        }else if(o.status?.code == 8){
                            if(navigateToTPro) {
                                fragment.requireContext()?.limitExceed(fragment.requireContext().getString(R.string.app_name), "${o.status?.message}", positiveButton = {
                                    fragment.startFragment(TarrakkiProBenefitsFragment.newInstance(), R.id.frmContainer)
                                }, btnTitle = fragment.requireContext().getString(R.string.tarrakki_pro))
                            }else{
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
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
    json.printRequest()
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

fun getPANeKYCStatus(pan: String): MutableLiveData<List<String>> {

    EventBus.getDefault().post(SHOW_PROGRESS)
    val apiResponse = MutableLiveData<List<String>>()
    val json = JsonObject()
    json.addProperty("pan", pan)
    val data = json.toString().toEncrypt()
    json.printRequest()
    data.printRequest()
    subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).verificationPAN(App.INSTANCE.getUserId(), data),
            object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o.status?.code == 1) {
                        o.printResponse()
                        val data = o.data?.parseTo<VerifyPANApiResponse>()
                        val kycStates: List<String> = arrayListOf(
                                data?.data?.cAMSKRA ?: "",//"${data?.camskra}",
                                data?.data?.cVLKRA ?: "",//"${data?.cvlkra}",
                                data?.data?.nDMLKRA ?: "",//"${data?.ndmlkra}",
                                data?.data?.dOTEXKRA ?: "",//"${data?.dotexkra}",
                                data?.data?.kARVYKRA ?: "")//"${data?.karvykra}")
                       /* val kycStates: List<String> = arrayListOf(
                                "05" ?: "",//"${data?.camskra}",
                                "22" ?: "",//"${data?.cvlkra}",
                                "05" ?: "",//"${data?.ndmlkra}",
                                "05" ?: "",//"${data?.dotexkra}",
                                "05" ?: "")//"${data?.karvykra}")*/
                        apiResponse.value = kycStates
                    } else {
                        EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.alert_try_later)))
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                    throwable.printStackTrace()
                }
            })
    return apiResponse
}

fun getPANDetails(kycData: KYCData): MutableLiveData<KYCData> {

    EventBus.getDefault().post(SHOW_PROGRESS)
    val apiResponse = MutableLiveData<KYCData>()
    val json = JsonObject()
    json.addProperty("pan", kycData.pan)
    val data = json.toString().toEncrypt()
    json.printRequest()
    data.printRequest()
    subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getPANDetails(App.INSTANCE.getUserId(), data),
            object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o.status?.code == 1) {
                        val pan = o.data?.parseTo<PANDetails>()
                        pan?.data?.panData?.let { data ->
                            if ("R".equals(data.aPPRESSTATUS, true)) {
                                //kyc.mobile = data.appmobno
                                //kyc.email = data.appemail
                                kycData.nameOfPANHolder = data.aPPNAME ?: ""
                                kycData.fullName = data.aPPNAME ?: ""
                                kycData.OCCcode = data.aPPOCC ?: ""
                                if ("${data.aPPDOBDT}".contains("-")) {
                                    kycData.dob = data.aPPDOBDT?.toDate("dd-MM-yyyy")?.convertTo("dd/MM/yyyy")
                                            ?: ""
                                } else {
                                    kycData.dob = data.aPPDOBDT?.toDate("dd/MM/yyyy")?.convertTo("dd/MM/yyyy")
                                            ?: ""//data.appdobdt ?: ""
                                }
                                kycData.gender = data.aPPGEN ?: ""
                                kycData.address = "${data.aPPPERADD1}, ${data.aPPPERADD2}, ${data.aPPPERADD3}"
                                kycData.pincode = data.aPPPERPINCD ?: ""
                                kycData.city = data.aPPPERCITY ?: ""
                                kycData.state = data.aPPPERSTATE ?: ""
                                kycData.country = data.aPPPERCTRY ?: ""
                                kycData.addressType = "01"
                                kycData.kycMode = data.aPPKYCMODE ?: ""
                                kycData.inPersonVerification = data.aPPIPVFLAG ?: ""
                                apiResponse.value = kycData
                            } else {
                                eventKYCDataLog(kycData, "01|02")
                                EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.alert_alert_non_resident)))
                            }
                        }
                        if (pan?.data?.panData == null) {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.alert_try_later)))
                        }
                    } else {
                        EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.alert_try_later)))
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                    throwable.printStackTrace()
                }
            })
    return apiResponse
}

fun getDefaultBank(): MutableLiveData<DefaultBankResponse> {
    val apiResponse = MutableLiveData<DefaultBankResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .getDefaultBank(App.INSTANCE.getUserId()),
            singleCallback = object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    e("Api Response=>${o.data?.toDecrypt()}")
                    if (o.status?.code == 1) {
                        val bank = o.data?.parseTo<DefaultBankResponse>()
                        apiResponse.value = bank
                    } else {
                        EventBus.getDefault().post(ShowError("${o.status?.message}"))
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    EventBus.getDefault().post(ShowError("${throwable.message}"))
                }
            }
    )
    return apiResponse
}

fun getFolioDetails(folioNo: String): MutableLiveData<SchemeDetails> {

    val response = MutableLiveData<SchemeDetails>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    val json = JsonObject()
    json.addProperty("folio_number", folioNo)
    val data = json.toString().toEncrypt()
    json.printRequest()
    data.printRequest()
    subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
            .getSchemeDetails(App.INSTANCE.getUserId(), data),
            object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    thread {
                        o.printResponse()
                        if (o.status?.code == 1) {
                            val schemeDetails = o.data?.parseTo<SchemeDetails>()
                            response.postValue(schemeDetails)
                        } else {
                            EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            response.postValue(null)
                        }
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                    response.postValue(null)
                }
            })
    return response
}

fun redeemPortfolio(json: JsonObject): MutableLiveData<RedeemedStatus> {
    val apiResponse = MutableLiveData<RedeemedStatus>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    val data = json.toString().toEncrypt()
    json.printRequest()
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).redeemPortfolio(data),
            apiNames = WebserviceBuilder.ApiNames.addtocart,
            singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ApiResponse) {
                        e("Api Response=>${o.data?.toDecrypt()}")
                        if (o.status?.code == 1) {
                            apiResponse.value = o.data?.parseTo<RedeemedStatus>()
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
    return apiResponse
}

fun instaRedeemPortfolio(json: JsonObject): MutableLiveData<RedeemedStatus> {
    val apiResponse = MutableLiveData<RedeemedStatus>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    val data = json.toString().toEncrypt()
    json.printRequest()
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .instaRedeem(App.INSTANCE.getUserId(), data),
            apiNames = WebserviceBuilder.ApiNames.addtocart,
            singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ApiResponse) {
                        o.printResponse()
                        if (o.status?.code == 1) {
                            apiResponse.value = o.data?.parseTo<RedeemedStatus>()
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

fun stopPortfolio(transactionId: Int): MutableLiveData<ApiResponse> {
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
                            //EventBus.getDefault().post(ShowError("${o.status?.message}"))
                        } else {
                            //apiResponse.value = o
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

@SuppressLint("HardwareIds")
fun sendDeviceDetails() {
    val deviceId = Settings.Secure.getString(App.INSTANCE.contentResolver, Settings.Secure.ANDROID_ID)
    val json = JsonObject()
    json.addProperty("token", App.INSTANCE.getPushToken())
    json.addProperty("device_id", deviceId)
    json.addProperty("device_type", "A")
    val data = json.toString().toEncrypt()
    json.printRequest()
    subscribeToSingle(
            ApiClient.getHeaderClient().create(SupportApis::class.java).sendDeviceDetails(App.INSTANCE.getUserId(), data),
            object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    o.printResponse()
                }

                override fun onFailure(throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
    )
}

fun closeTicketApi(ticketId: String) {
    val json = JsonObject()
    json.addProperty("ticket_ref", ticketId)
    val data = json.toString().toEncrypt()
    json.printRequest()
    subscribeToSingle(
            ApiClient.getHeaderClient().create(SupportApis::class.java).closeTicket(data),
            object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    o.printResponse()
                }

                override fun onFailure(throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
    )
}

fun checkAppUpdate(showProcess: Boolean = false): MutableLiveData<AppUpdateResponse> {
    if (showProcess)
        EventBus.getDefault().post(SHOW_PROGRESS)
    val apiResponse = MutableLiveData<AppUpdateResponse>()
    subscribeToSingle(ApiClient.getApiClient().create(WebserviceBuilder::class.java).checkAppUpdate(BuildConfig.FLAVOR.isTarrakki().getOrganizationCode()),
            object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    if (showProcess)
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o.status?.code == 1) {
                        o.printResponse()
                        val apiClient = o.data?.parseTo<AppUpdateResponse>()
                        apiResponse.postValue(apiClient)
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    if (showProcess)
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.printStackTrace()
                }
            })
    return apiResponse
}


fun getMaintenanceDetails(): MutableLiveData<ApiResponse> {
    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java)
                    .getMaintenanceDetails(),
            singleCallback = object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o.status?.code == 1) {
                        apiResponse.value = o
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    EventBus.getDefault().post(ShowError("${throwable.message}"))
                }
            }
    )
    return apiResponse
}

fun getRiskAssessmentQuestions(): MutableLiveData<RiskAssessmentQuestionsApiResponse> {
    val apiQuestionsResponse = MutableLiveData<RiskAssessmentQuestionsApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getRiskAssessmentQuestions(App.INSTANCE.getUserId()), object : SingleCallback1<ApiResponse> {
        override fun onSingleSuccess(o: ApiResponse) {
            EventBus.getDefault().post(DISMISS_PROGRESS)
            if (o.status?.code == 1) {
                GlobalScope.launch {
                    withContext(Dispatchers.Default) {
                        val data = o.data?.parseTo<RiskAssessmentQuestionsApiResponse>()
                        apiQuestionsResponse.postValue(data)
                    }
                }
            } else {
                postError("${o.status?.message}")
            }
        }

        override fun onFailure(throwable: Throwable) {
            EventBus.getDefault().post(DISMISS_PROGRESS)
            throwable.postError()
        }
    })
    return apiQuestionsResponse
}

fun getReportOfRiskProfile(): MutableLiveData<ApiResponse> {
    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getReportOfRiskProfile(App.INSTANCE.getUserId()), object : SingleCallback1<ApiResponse> {
        override fun onSingleSuccess(o: ApiResponse) {
            EventBus.getDefault().post(DISMISS_PROGRESS)
            if (o.status?.code == 1 || o.status?.code == 9) {
                apiResponse.value = o
            } else {
                postError("${o.status?.message}")
            }
        }

        override fun onFailure(throwable: Throwable) {
            EventBus.getDefault().post(DISMISS_PROGRESS)
            throwable.postError()
        }
    })
    return apiResponse
}

fun getAddressAmountAPI(): MutableLiveData<AddressAmountData> {
    val addressData = MutableLiveData<AddressAmountData>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                    .getAddressAmount(App.INSTANCE.getUserId()),

            singleCallback = object : SingleCallback1<ApiResponse> {
                override fun onSingleSuccess(o: ApiResponse) {
                    o.printResponse()
                    if (o.status?.code == 1) {
                        val data = o.data?.parseTo<AddressAmountData>()
                        data?.let {
                            addressData.value = it
                        }
                    } else {
                        postError("${o.status?.message}")
                    }
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                }

                override fun onFailure(throwable: Throwable) {
                    throwable.postError()
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                }
            }
    )
    return addressData
}