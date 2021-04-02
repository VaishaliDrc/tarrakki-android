package com.tarrakki.module.setpassword

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
import kotlin.concurrent.thread

class SetPasswordVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    /*val userName = ObservableField("saumya.shah@gmail.com")
    val password = ObservableField("Drc@1234")*/
    val userName = ObservableField("")
    val password = ObservableField("")
    val email = ObservableField("")
    val mobile = ObservableField("")
    val token = ObservableField("")
    val onLogin = MutableLiveData<LoginResponse>()
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()
    val isPasswordShow = ObservableField(false)
    val onSignUp = MutableLiveData<SignUpresponse>()


    fun doLogin(): MutableLiveData<LoginResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("username", "${userName.get()}")
        json.addProperty("password", "${password.get()}")
        json.addProperty("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())

        e("Plain Data=>", json.toString())
        val authData = AES.encrypt(json.toString())
        e("Encrypted Data=>", authData)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).onLogin(authData),
                apiNames = WebserviceBuilder.ApiNames.onLogin,
                singleCallback = this@SetPasswordVM
        )
        return onLogin
    }



    fun onSignUp(): MutableLiveData<SignUpresponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("email", "${email.get()}".toLowerCase().trim())
        json.addProperty("mobile", "${mobile.get()}")
        json.addProperty("password", "${password.get()}")
        json.addProperty("promocode", "")
        json.addProperty("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())
        e("Plain Data=>", json.toString())
        val authData = AES.encrypt(json.toString())
        e("Encrypted Data=>", authData)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).onSignUp(authData),
                apiNames = WebserviceBuilder.ApiNames.onSignUp,
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


    fun forgotPassword(): MutableLiveData<ForgotPasswordEmailResponse> {
        val json = JsonObject()
        json.addProperty("email", "${userName.get()}".toLowerCase().trim())
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

    fun resetPassword(): MutableLiveData<ApiResponse> {
        val json = JsonObject()
        json.addProperty("token",token.get().toString())
        json.addProperty("password", password.get().toString())
        val data = json.toString().toEncrypt()

        val apiResponse = MutableLiveData<ApiResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java)
                        .resetPassword(data),
                apiNames = WebserviceBuilder.ApiNames.resetPassword,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            e("Api Response=>${o.data?.toDecrypt()}")
                            o.printResponse()
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



    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
        EventBus.getDefault().post(DISMISS_PROGRESS)
        if (o is ApiResponse) {
            o.printResponse()
            if (o.status?.code == 1) {
                val data = o.data?.parseTo<LoginResponse>()
                onLogin.value = data
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