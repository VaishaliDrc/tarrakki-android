package com.tarrakki.module.verifysocialmobilenumber

import android.os.Handler
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.BuildConfig
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import com.tarrakki.isTarrakki
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import kotlin.concurrent.thread

class VerifySocialMobileVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    val resendOtpObserver = ObservableField(45)
    var resendOtpTimer = 45
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()
    val getOTP = MutableLiveData<ApiResponse>()
    val otpId = ObservableField("")
    val email = ObservableField("")


    fun socialSignUp(json: JSONObject): MutableLiveData<ApiResponse> {
        val getOTP = MutableLiveData<ApiResponse>()
        e("Plain Data=>", json.toString())
        val data = AES.encrypt(json.toString())
        e("Encrypted Data=>", data)
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).socialSignUp(data),
                apiNames = WebserviceBuilder.ApiNames.onSignUp,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 2) {
                                getOTP.value = o
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
        return getOTP
    }

    fun verifySocialOTP(otp:String,encryptedData: String): MutableLiveData<SignUpresponse> {
        val onSignUp = MutableLiveData<SignUpresponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JSONObject(encryptedData.toDecrypt())
        json.put("otp", otp)
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

    fun startTimer(timeLimit : Int){
        resendOtpTimer = timeLimit
        val handler = Handler()
        val r: Runnable = object : Runnable {
            override fun run() {
                resendOtpTimer--
                resendOtpObserver.set(resendOtpTimer)
                if (resendOtpTimer <= 0){
                    handler.removeCallbacks(this)
                }else {
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.postDelayed(r, 1000)
    }

    fun getNewOTP(encryptedData: String): MutableLiveData<ApiResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JSONObject(encryptedData.toDecrypt())
        val data = json.toString().toEncrypt()
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = this@VerifySocialMobileVM
        )
        return getOTP
    }

    fun getCallOTP(encryptedData: String): MutableLiveData<ApiResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JSONObject(encryptedData.toDecrypt())
        json.put("voice", "true")
        val data = json.toString().toEncrypt()
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = this@VerifySocialMobileVM
        )
        return getOTP
    }

    fun forgotPasswordSendOTP(isCall : Boolean): MutableLiveData<ForgotPasswordEmailResponse> {
        val json = JsonObject()
        json.addProperty("email", "${email.get()}".toLowerCase().trim())
        json.addProperty("type", "forgot_password")
        if(isCall){
            json.addProperty("voice", "true")
        }
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




    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
        EventBus.getDefault().post(DISMISS_PROGRESS)
        if (o is ApiResponse) {
            if ((o.status?.code == 1)) {
                when (apiNames) {
                    WebserviceBuilder.ApiNames.getOTP -> {
                        getOTP.value = o
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



}