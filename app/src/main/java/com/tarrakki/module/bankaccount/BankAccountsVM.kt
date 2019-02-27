package com.tarrakki.module.bankaccount

import android.arch.lifecycle.MutableLiveData
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
    val SIGNPAD_RQ_CODE = 182
    var kycData = MutableLiveData<KYCData>()
    var imageFrom = 0

    fun getAllBanks(isRefreshing: Boolean = false): MutableLiveData<UserBanksResponse> {
        if (!isRefreshing)
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

    fun completeRegistrations(signatureFile: File, kycData: KYCData): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        showProgress()
        thread {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), signatureFile)
            val multipartBody = MultipartBody.Part.createFormData("signature_image1", signatureFile.name, requestFile)
            val json = JsonObject()
            json.addProperty("pan_name", kycData.nameOfPANHolder)
            if (kycData.guardianName.isEmpty()) {
                json.addProperty("pan_number", kycData.pan)
                json.addProperty("guardian_name", "")
                json.addProperty("guardian_pan", "")
            } else {
                json.addProperty("guardian_name", kycData.guardianName)
                json.addProperty("guardian_pan", kycData.pan)
            }
            json.addProperty("date_of_birth", kycData.dob.toDate("dd MMM, yyyy").convertTo("dd/MM/yyyy"))
            json.addProperty("address", kycData.address)
            json.addProperty("city", kycData.city)
            json.addProperty("pincode", kycData.pincode)
            json.addProperty("state", kycData.state)
            json.addProperty("country", kycData.country)
            json.addProperty("occ_code", kycData.OCCcode)
            json.addProperty("nominee_name", kycData.nomineeName)
            json.addProperty("nominee_relation", kycData.nomineeRelation)
            json.addProperty("user_id", "${App.INSTANCE.getUserId()}")
            json.addProperty("email", kycData.email)
            json.addProperty("full_name", kycData.fullName)
            json.addProperty("mobile_number", kycData.mobile)
            json.addProperty("birth_country", kycData.birthCountry)
            json.addProperty("birth_place", kycData.birthPlace)
            json.addProperty("gender", kycData.gender)
            json.addProperty("tin_number1", "")
            json.addProperty("tin_number2", "")
            json.addProperty("tin_number3", "")
            json.addProperty("country_of_issue1", "")
            json.addProperty("country_of_issue2", "")
            json.addProperty("country_of_issue3", "")
            json.addProperty("address_type", kycData.addressType)
            json.addProperty("source_of_income", kycData.sourceOfIncome)
            json.addProperty("income_slab", kycData.taxSlab)
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
                                if (o.status?.code == 1) {
                                    App.INSTANCE.setCompletedRegistration(true)
                                    App.INSTANCE.setKYClVarified(true)
                                }
                                apiResponse.value = o
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