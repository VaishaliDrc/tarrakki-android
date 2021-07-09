package com.tarrakki.module.login

import androidx.annotation.DrawableRes
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.*
import com.tarrakki.api.*
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.LoginResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import org.greenrobot.eventbus.EventBus
import org.supportcompact.ActivityViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*

class NewLoginVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

    /*val userName = ObservableField("saumya.shah@gmail.com")
    val password = ObservableField("Drc@1234")*/

    val onLogin = MutableLiveData<LoginResponse>()
    val onSocialLogin = MutableLiveData<ApiResponse>()
    val onNormalLogin = MutableLiveData<ApiResponse>()
    val socialId = ObservableField("")
    val socialEmail = ObservableField("")
    val socialFName = ObservableField("")
    val socialLName = ObservableField("")
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()



    fun doSocialLogin(loginWith: String = "facebook"): MutableLiveData<ApiResponse> {
        /***
        3 - You have to request for social signup
        2 - you need to show popup for otp verification
        1 - successfully login with email and return login response
         * */
        showProgress()
        val json = JsonObject()
        json.addProperty("email", "${socialEmail.get()}".toLowerCase().trim())
        json.addProperty("access_token", socialId.get())
        json.addProperty("social_auth", loginWith)
        json.addProperty("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())

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
                                App.INSTANCE.setSocialLogin(data != null)
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        this@NewLoginVM.onFailure(throwable, apiNames)
                    }
                }
        )
        return onSocialLogin
    }

    fun doNormalLogin(emailOrMobile: String): MutableLiveData<ApiResponse> {
        /***
        3 - You have to request for social signup
        2 - you need to show popup for otp verification
        1 - successfully login with email and return login response
         * */
        showProgress()
        val json = JsonObject()
        json.addProperty("email_or_mobile", emailOrMobile)
        json.addProperty("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())

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
                            if (o.status?.code == 1 || o.status?.code == 2) {
                                onNormalLogin.value = o
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        this@NewLoginVM.onFailure(throwable, apiNames)
                    }
                }
        )
        return onNormalLogin
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


    fun getLoginIntroductionList() : ArrayList<NewLoginVM.LoginIntroduction>{
        val introductionList = arrayListOf<NewLoginVM.LoginIntroduction>()
        introductionList.add(LoginIntroduction("Get a FREE mutual fund investment linked VISA debit card", R.drawable.tarrakki_zyaada_card))
        introductionList.add(LoginIntroduction("Get BUY-SELL-HOLD recommendations on over 2500 Mutual Funds", R.drawable.buy_sell_hold_intro))
        introductionList.add(LoginIntroduction("Invest in various asset classes", R.drawable.invest_in_various_intro))
         return introductionList
    }


    class LoginIntroduction(val title: String ,@DrawableRes val image: Int) : BaseObservable()
}