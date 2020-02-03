package com.tarrakki.module.register

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.*
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.printResponse
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e

class RegisterVM : ActivityViewModel() {

    val email = ObservableField("")
    val mobile = ObservableField("")
    val password = ObservableField("")
    val confirmPassword = ObservableField("")
    val promocode = ObservableField("")
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()

    fun getSignUpData(): JsonObject {
        val json = JsonObject()
        json.addProperty("email", "${email.get()}".toLowerCase().trim())
        json.addProperty("mobile", "${mobile.get()}")
        json.addProperty("password", "${password.get()}")
        json.addProperty("promocode", (promocode.get() ?: "").trim())
        json.addProperty("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())
        return json
    }

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

    fun getOTP(mobile: String?, email: String?, type: String = "signup"): MutableLiveData<ApiResponse> {
        val getOTP = MutableLiveData<ApiResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("mobile", mobile)
        json.addProperty("email", "$email".toLowerCase().trim())
        json.addProperty("type", type)
        json.addProperty("promocode", (promocode.get() ?: "").trim())
        json.addProperty("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())
        e("Plain Data=>", json.toString())
        val data = AES.encrypt(json.toString())
        e("Encrypted Data=>", data)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
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
}