package com.tarrakki.module.otp

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import kotlin.concurrent.thread

class OptVerificationsVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    val email = ObservableField("")
    val otp = ObservableField("")
    val otpId = ObservableField("")
    val getOTP = MutableLiveData<ApiResponse>()
    private val verifyOTP = MutableLiveData<Boolean>()

    fun getDataOTP(originalData: String?): MutableLiveData<ApiResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        e("Plain Data=>", originalData.toString())
        val data = AES.encrypt(originalData.toString())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = this@OptVerificationsVM
        )
        return getOTP
    }

    fun getOTP(mobile: String?, email: String?, type: String = "signup"): MutableLiveData<ApiResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("mobile", mobile)
        json.addProperty("email", "$email".toLowerCase())
        json.addProperty("type", type)
        e("Plain Data=>", json.toString())
        val data = AES.encrypt(json.toString())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = this@OptVerificationsVM
        )
        return getOTP
    }

    fun getNewOTP(encryptedData: String): MutableLiveData<ApiResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JSONObject(encryptedData.toDecrypt())
        json.put("otp", otp.get())
        val data = json.toString().toEncrypt()
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = this@OptVerificationsVM
        )
        return getOTP
    }

    fun verifyOTP(encryptedData: String): MutableLiveData<Boolean> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JSONObject(encryptedData.toDecrypt())
        json.put("otp", otp.get())
        val data = json.toString().toEncrypt()
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).verifyOTP(data),
                apiNames = WebserviceBuilder.ApiNames.verifyOTP,
                singleCallback = this@OptVerificationsVM
        )
        return verifyOTP
    }

    fun verifySocialOTP(encryptedData: String): MutableLiveData<SignUpresponse> {
        val onSignUp = MutableLiveData<SignUpresponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JSONObject(encryptedData.toDecrypt())
        json.put("otp", otp.get())
        val data = json.toString().toEncrypt()
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).verifyOTP(data),
                apiNames = WebserviceBuilder.ApiNames.verifyOTP,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val data = o.data?.parseTo<SignUpresponse>()
                                onSignUp.value = data
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
        return onSignUp
    }

    fun onSignUp(json: JSONObject): MutableLiveData<SignUpresponse> {
        val onSignUp = MutableLiveData<SignUpresponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        val authData = AES.encrypt(json.toString())
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).onSignUp(authData),
                apiNames = WebserviceBuilder.ApiNames.onLogin,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val data = o.data?.parseTo<SignUpresponse>()
                                onSignUp.value = data
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
        return onSignUp
    }

    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
        EventBus.getDefault().post(DISMISS_PROGRESS)
        if (o is ApiResponse) {
            if ((o.status?.code == 1)) {
                when (apiNames) {
                    WebserviceBuilder.ApiNames.getOTP -> {
                        getOTP.value = o
                    }
                    WebserviceBuilder.ApiNames.verifyOTP -> {
                        verifyOTP.value = true
                    }
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

    fun forgotPasswordSendOTP(): MutableLiveData<ForgotPasswordEmailResponse> {
        val json = JsonObject()
        json.addProperty("email", "${email.get()}".toLowerCase())
        json.addProperty("type", "forgot_password")
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        val apiResponse = MutableLiveData<ForgotPasswordEmailResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java)
                        .forgotPassword(data),
                apiNames = WebserviceBuilder.ApiNames.forgotPassword,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            e("Api Response=>${o.data?.toDecrypt()}")
                            o.printResponse()
                            if (o.status?.code == 1) {
                                thread {
                                    val data = o.data?.parseTo<ForgotPasswordEmailResponse>()
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

    fun forgotPasswordVerifyOTP(otp: String?, otpId: String?): MutableLiveData<ForgotPasswordVerifyOtpResponse> {
        val json = JsonObject()
        json.addProperty("otp", otp)
        json.addProperty("otp_id", otpId)
        val data = json.toString().toEncrypt()

        val apiResponse = MutableLiveData<ForgotPasswordVerifyOtpResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java)
                        .verifyForgotOTP(data),
                apiNames = WebserviceBuilder.ApiNames.forgotPassword,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            e("Api Response=>${o.data?.toDecrypt()}")
                            o.printResponse()
                            if (o.status?.code == 1) {
                                thread {
                                    val forgotPasswordVerifyOTP = o.data?.parseTo<ForgotPasswordVerifyOtpResponse>()
                                    apiResponse.postValue(forgotPasswordVerifyOTP)
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

}