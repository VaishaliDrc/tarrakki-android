package com.tarrakki.module.bankaccount

import android.arch.lifecycle.MutableLiveData
import android.graphics.Color
import android.support.annotation.StringRes
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.module.ekyc.KYCData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.io.File
import kotlin.concurrent.thread

class BankAccountsVM : FragmentViewModel() {

    val cvPhotoName = "profilePick"
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    var kycData = MutableLiveData<KYCData>()


    fun getAllBanks(): MutableLiveData<UserBanksResponse> {
        showProgress()
        val response = MutableLiveData<UserBanksResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getUserBanks(App.INSTANCE.getUserId()),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            thread {
                                if (o.status?.code == 1) {
                                    val data = o.data?.parseTo<UserBanksResponse>()
                                    response.postValue(data)
                                } else {
                                    EventBus.getDefault().post(ShowError("${o.status?.message}"))
                                }
                                dismissProgress()
                            }
                        } else {
                            dismissProgress()
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

    fun setDefault(bankId: String): MutableLiveData<ApiResponse> {
        showProgress()
        val json = JsonObject()
        json.addProperty("bank_detail_id", bankId)
        json.addProperty("is_default", true)
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        val response = MutableLiveData<ApiResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).setDefault(App.INSTANCE.getUserId(), data),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                response.postValue(o)
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                            dismissProgress()
                        } else {
                            dismissProgress()
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

    fun completeRegistrations(signatureFile: File): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        showProgress()
        thread {
            //val mFile = signatureFile.toBitmap()?.toTransparent(Color.WHITE)?.toFile() ?: signatureFile
            val requestFile = RequestBody.create(MediaType.parse("image/*"), signatureFile)
            val multipartBody = MultipartBody.Part.createFormData("signature_image1", signatureFile.name, requestFile)
            val json = JsonObject()
            json.addProperty("pan_number", "EZIPS7324M")
            json.addProperty("date_of_birth", "01/01/2000")
            json.addProperty("guardian_name", "")
            json.addProperty("guardian_pan", "")
            json.addProperty("address", "GG")
            json.addProperty("city", "Ah")
            json.addProperty("pincode", "323223")
            json.addProperty("state", "GU")
            json.addProperty("country", "IN")
            json.addProperty("occ_code", "01")
            json.addProperty("nominee_name", "jsp")
            json.addProperty("nominee_relation", "Father")
            json.addProperty("user_id", "${App.INSTANCE.getUserId()}")
            //json.addProperty("bank_id", "")///What is used
            json.addProperty("email", "${App.INSTANCE.getEmail()}")
            json.addProperty("full_name", "JP")
            json.addProperty("mobile_number", "7896547845")
            json.addProperty("birth_country", "IN")
            json.addProperty("birth_place", "IN")
            json.addProperty("gender", "M")
            json.addProperty("tin_number1", "")
            json.addProperty("tin_number2", "")
            json.addProperty("tin_number3", "")
            json.addProperty("country_of_issue1", "")
            json.addProperty("country_of_issue2", "")
            json.addProperty("country_of_issue3", "")
            json.addProperty("address_type", "1")
            json.addProperty("source_of_income", "E")
            e("Plain Data=>", json.toString())
            val data = AES.encrypt(json.toString())
            e("Encrypted Data=>", data)
            val dataRequest = RequestBody.create(MediaType.parse("text/plain"), data)
            subscribeToSingle(
                    observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).completeRegistration(dataRequest, multipartBody),
                    apiNames = WebserviceBuilder.ApiNames.complateRegistration,
                    singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                        override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                            signatureFile.deleteOnExit()
                            dismissProgress()
                            if (o is ApiResponse) {
                                o.printResponse()
                                apiResponse.value = o
                                /*if (o.status?.code == 1) {
                                    apiResponse.value = o
                                } else {
                                    EventBus.getDefault().post(ShowError("${o.status?.message}"))
                                }*/
                            } else {
                                EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                            }
                        }

                        override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                            signatureFile.deleteOnExit()
                            dismissProgress()
                            throwable.postError()
                        }
                    }
            )
        }
        return apiResponse
    }


}

class NoBankAccount : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.row_no_bank_account_yet
    }
}

data class SingleButton(@StringRes var title: Int) : WidgetsViewModel {
    override fun layoutId(): Int {
        return R.layout.btn_add_bank_account
    }
}