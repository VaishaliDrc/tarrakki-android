package com.tarrakki.module.verifymobileoremail

import android.os.Handler
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.tarrakki.*
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.e

class UserMobileEmailInputVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {



    val isEmailVerified = ObservableField(false)
    val isMobileVerified = ObservableField(false)
    val email = ObservableField("")
    val mobile = ObservableField("")
    val firstName = ObservableField("")
    val lastName = ObservableField("")
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()



    fun sendEmailOrMobileOTP(): MutableLiveData<NormalLoginData> {
        val onOTPSend = MutableLiveData<NormalLoginData>()

        val json = JSONObject()
        json.put("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())
        json.put("mobile", mobile.get())
        json.put("email", email.get())

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





    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
        EventBus.getDefault().post(DISMISS_PROGRESS)
        if (o is ApiResponse) {
            if ((o.status?.code == 1)) {

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