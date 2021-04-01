package com.tarrakki.module.verifysocialmobilenumber

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

class VerifySocialMobileVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    val resendOtpObserver = ObservableField(45)
    var resendOtpTimer = 45
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()
    val getOTP = MutableLiveData<ApiResponse>()


    fun socialSignUp(json: JSONObject): MutableLiveData<ApiResponse> {
        val getOTP = MutableLiveData<ApiResponse>()
        e("Plain Data=>", json.toString())
        val data = AES.encrypt(json.toString())
        e("Encrypted Data=>", data)
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

        val data = json.toString().toEncrypt()
        json.put("voice", true)
        e("Plain Data=>", data.toDecrypt())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = this@VerifySocialMobileVM
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