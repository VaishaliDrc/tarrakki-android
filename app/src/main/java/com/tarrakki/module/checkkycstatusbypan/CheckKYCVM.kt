package com.tarrakki.module.checkkycstatusbypan

import androidx.annotation.DrawableRes
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
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

class CheckKYCVM : ActivityViewModel(), SingleCallback<WebserviceBuilder.ApiNames> {

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
    val isTarrakki = BuildConfig.FLAVOR.isTarrakki()



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