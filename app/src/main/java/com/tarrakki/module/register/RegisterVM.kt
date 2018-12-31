package com.tarrakki.module.register

import android.databinding.ObservableField
import com.google.gson.JsonObject
import org.supportcompact.ActivityViewModel

class RegisterVM : ActivityViewModel() {

    val email = ObservableField("")
    val mobile = ObservableField("")
    val password = ObservableField("")
    val confirmPassword = ObservableField("")

    fun getSignUpData(): JsonObject {
        val json = JsonObject()
        json.addProperty("email", "${email.get()}")
        json.addProperty("mobile", "${mobile.get()}")
        json.addProperty("password", "${password.get()}")
        return json
    }

    /*fun onSignUp(): MutableLiveData<SignUpresponse> {
        val onSignUp = MutableLiveData<SignUpresponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonObject()
        json.addProperty("email", "${email.get()}")
        json.addProperty("mobile", "${mobile.get()}")
        json.addProperty("password", "${password.get()}")
        val authData = AES.encrypt(json.toString())
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).onSignUp(authData),
                apiNames = WebserviceBuilder.ApiNames.onLogin,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is SignUpresponse) {
                            if ((o.status?.code == 1)) {
                                onSignUp.value = o
                            } else {
                                EventBus.getDefault().post(ShowError(o.status.message))
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
    }*/

}