package com.tarrakki.module.login

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.LoginResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import org.greenrobot.eventbus.EventBus
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*

class LoginVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    /*val userName = ObservableField("saumya.shah@gmail.com")
    val password = ObservableField("Drc@1234")*/
    val userName = ObservableField("")
    val password = ObservableField("")
    val onLogin = MutableLiveData<LoginResponse>()
    val onSocialLogin = MutableLiveData<ApiResponse>()
    val socialId = ObservableField("")
    val socialEmail = ObservableField("")
    val socialFName = ObservableField("")
    val socialLName = ObservableField("")


    fun doLogin(): MutableLiveData<LoginResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("username", "${userName.get()}")
        json.addProperty("password", "${password.get()}")
        e("Plain Data=>", json.toString())
        val authData = AES.encrypt(json.toString())
        e("Encrypted Data=>", authData)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).onLogin(authData),
                apiNames = WebserviceBuilder.ApiNames.onLogin,
                singleCallback = this@LoginVM
        )
        return onLogin
    }

    fun doSocialLogin(loginWith: String = "facebook"): MutableLiveData<ApiResponse> {
        /***
        3 - You have to request for social signup
        2 - you need to show popup for otp verification
        1 - successfully login with email and return login response
         * */
        showProgress()
        val json = JsonObject()
        json.addProperty("email", "${socialEmail.get()}".toLowerCase())
        json.addProperty("access_token", socialId.get())
        json.addProperty("social_auth", loginWith)
        e("Plain Data=>", json.toString())
        val authData = AES.encrypt(json.toString())
        e("Encrypted Data=>", authData)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).socialLogin(authData),
                apiNames = WebserviceBuilder.ApiNames.onLogin,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 2 || o.status?.code == 3) {
                                o.status.message = loginWith
                                onSocialLogin.value = o
                            } else if (o.status?.code == 1) {
                                val data = o.data?.parseTo<LoginResponse>()
                                onLogin.value = data
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        this@LoginVM.onFailure(throwable, apiNames)
                    }
                }
        )
        return onSocialLogin
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