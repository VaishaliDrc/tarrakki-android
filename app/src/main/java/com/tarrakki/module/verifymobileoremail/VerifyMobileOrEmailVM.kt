package com.tarrakki.module.verifymobileoremail

import android.os.Handler
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*

class VerifyMobileOrEmailVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    val resendOtpObserver = ObservableField(45)
    val hasMobile = ObservableField(false)
    val hasEmail = ObservableField(false)
    val isEmailVerified = ObservableField(false)
    val isMobileVerified = ObservableField(false)
    val email = ObservableField("")
    val mobile = ObservableField("")
    val firstName = ObservableField("")
    val lastName = ObservableField("")
    val otpId = ObservableField(0)
    var resendOtpTimer = 45
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()
    val getOTP = MutableLiveData<ApiResponse>()


    fun verifyEmailOrMobileOTP(otp: String): MutableLiveData<OTPVerifyResponse> {
        val onVerified = MutableLiveData<OTPVerifyResponse>()

        val json = JSONObject()
        json.put("otp", otp)
        json.put("otp_id", otpId.get())

        if (!isEmailVerified.get()!! && !isMobileVerified.get()!!) {
            if (hasEmail.get()!!) {
                json.put("email", email.get())
                json.put("type", "email_signup")
            } else {
                json.put("mobile", mobile.get())
                json.put("type", "mobile_signup")
            }
        } else if (isEmailVerified.get()!!) {
            json.put("email", email.get())
            json.put("mobile", mobile.get())
            json.put("type", "mobile_signup")
        } else {
            json.put("mobile", mobile.get())
            json.put("email", email.get())
            json.put("type", "email_signup")
        }

        val data = json.toString().toEncrypt()
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).verifyMobileOrEmailOTP(data),
                apiNames = WebserviceBuilder.ApiNames.verifyOTP,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val data = o.data?.parseTo<EmailOrMobileOTPVerifyResponse>()!!.verificationData
                                onVerified.value = data
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
        return onVerified
    }


    fun resendOTP(): MutableLiveData<ApiResponse> {
        /***
        call API to resend otp to mobile or email
         * */
        showProgress()
        val json = JsonObject()
        var emailOrMobile = ""
        if (!isEmailVerified.get()!! && !isMobileVerified.get()!!) {
            if (hasEmail.get()!!) {
                emailOrMobile = email.get()!!
            } else {
                emailOrMobile = mobile.get()!!
            }
        } else if (isEmailVerified.get()!!) {
            emailOrMobile = mobile.get()!!
        } else {
            emailOrMobile = email.get()!!
        }
        json.addProperty("email_or_mobile", emailOrMobile)
        // json.addProperty("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())

        e("Plain Data=>", json.toString())
        val authData = AES.encrypt(json.toString())
        e("Encrypted Data=>", authData)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).simpleSingupSignin(authData),
                apiNames = WebserviceBuilder.ApiNames.onLogin,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                                val response = o.data?.parseTo<NormalLoginResponse>()!!.loginData
                                otpId.set(response?.otpId)
                                startTimer(46)
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        this@VerifyMobileOrEmailVM.onFailure(throwable, apiNames)
                    }
                }
        )
        return getOTP
    }


    fun reSendEmailOrMobileOTP(isCall:Boolean): MutableLiveData<NormalLoginData> {
        val onOTPSend = MutableLiveData<NormalLoginData>()

        val json = JSONObject()
        json.put("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())
        json.put("mobile", mobile.get())
        json.put("email", email.get())

        if(isCall) {
            json.put("voice", "true")
        }
        if(isEmailVerified.get()!!){
            json.put("type", "mobile_signup")
        }else{
            json.put("type", "email_signup")
        }
        json.put("first_name", firstName.get())
        json.put("last_name", lastName.get())

        val data = json.toString().toEncrypt()
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).sendOTPWithUserData(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val data = o.data?.parseTo<NormalLoginData>()
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                                otpId.set(data?.otpId)
                                startTimer(46)
                                onOTPSend.value = data
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
        return onOTPSend
    }


    fun sendOTpViaCall(): MutableLiveData<ApiResponse> {
        /***
        call API to resend otp to mobile or email
         * */
        showProgress()
        val json = JsonObject()
        json.addProperty("email_or_mobile", mobile.get())
        json.addProperty("voice", "true")

        // json.addProperty("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())

        e("Plain Data=>", json.toString())
        val authData = AES.encrypt(json.toString())
        e("Encrypted Data=>", authData)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).simpleSingupSignin(authData),
                apiNames = WebserviceBuilder.ApiNames.onLogin,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                                val response = o.data?.parseTo<NormalLoginResponse>()!!.loginData
                                otpId.set(response?.otpId)
                               startTimer(46)
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        this@VerifyMobileOrEmailVM.onFailure(throwable, apiNames)
                    }
                }
        )
        return getOTP
    }


    fun startTimer(timeLimit: Int) {
        resendOtpTimer = timeLimit
        val handler = Handler()
        val r: Runnable = object : Runnable {
            override fun run() {
                resendOtpTimer--
                resendOtpObserver.set(resendOtpTimer)
                if (resendOtpTimer <= 0) {
                    handler.removeCallbacks(this)
                } else {
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
                singleCallback = this@VerifyMobileOrEmailVM
        )
        return getOTP
    }

    fun getCallOTP(encryptedData: String): MutableLiveData<ApiResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JSONObject(encryptedData.toDecrypt())

        val data = json.toString().toEncrypt()
        json.put("voice", true)
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = this@VerifyMobileOrEmailVM
        )
        return getOTP
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