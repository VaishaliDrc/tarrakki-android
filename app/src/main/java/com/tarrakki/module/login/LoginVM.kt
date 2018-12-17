package com.tarrakki.module.login

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.LoginResponse
import org.greenrobot.eventbus.EventBus
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class LoginVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    val userName = ObservableField("admin@admin.com")
    val password = ObservableField("Drc@1234")
    val onLogin = MutableLiveData<LoginResponse>()

    fun doLogin(): MutableLiveData<LoginResponse> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("email", "${userName.get()}")
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

    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
        EventBus.getDefault().post(DISMISS_PROGRESS)
        if (o is LoginResponse) {
            onLogin.value = o
        } else {
            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
        }
    }

    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
        EventBus.getDefault().post(DISMISS_PROGRESS)
        EventBus.getDefault().post(ShowError("${throwable.message}"))
    }
}