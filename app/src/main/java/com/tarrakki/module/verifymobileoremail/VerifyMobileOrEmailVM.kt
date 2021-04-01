package com.tarrakki.module.verifymobileoremail

import android.os.Handler
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
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

class VerifyMobileOrEmailVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    val resendOtpObserver = ObservableField(45)
    val hasMobile = ObservableField(false)
    val hasEmail = ObservableField(false)
    val isEmailVerified = ObservableField(false)
    val isMobileVerified = ObservableField(false)
    val email = ObservableField("")
    val mobile = ObservableField("")
    val otpId = ObservableField(0)
    var resendOtpTimer = 45
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()
    val getOTP = MutableLiveData<ApiResponse>()


    fun verifyEmailOrMobileOTP(otp:String): MutableLiveData<OTPVerifyResponse> {
        val onVerified = MutableLiveData<OTPVerifyResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JSONObject()
        json.put("otp", otp)
        json.put("otp_id", otpId.get())
        if(isEmailVerified.get()!!){
            json.put("mobile", mobile.get())
            json.put("type", "mobile_signup")
        }else{
            json.put("mobile", email.get())
            json.put("type", "email_signup")
        }

        val data = json.toString().toEncrypt()
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
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