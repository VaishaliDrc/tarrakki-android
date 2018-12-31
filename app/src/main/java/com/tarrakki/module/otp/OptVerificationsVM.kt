package com.tarrakki.module.otp

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.SignUpresponse
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class OptVerificationsVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    val otp = ObservableField("")
    val getOTP = MutableLiveData<ApiResponse>()
    private val verifyOTP = MutableLiveData<Boolean>()


    fun getOTP(mobile: String, email: String, type: String = "signup"): MutableLiveData<ApiResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("mobile", mobile)
        json.addProperty("email", email)
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

    fun verifyOTP(data: String): MutableLiveData<Boolean> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).verifyOTP(data),
                apiNames = WebserviceBuilder.ApiNames.verifyOTP,
                singleCallback = this@OptVerificationsVM
        )
        return verifyOTP
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
                        if (o is SignUpresponse) {
                            if (o.status?.code == 0) {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            } else {
                                onSignUp.value = o
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

}