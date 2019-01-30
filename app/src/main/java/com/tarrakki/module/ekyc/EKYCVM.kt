package com.tarrakki.module.ekyc

import android.arch.lifecycle.MutableLiveData
import com.tarrakki.api.WebserviceBuilder
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.postError
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.util.regex.Pattern


class EKYCVM : FragmentViewModel() {

    var kycData: KYCData? = null

}

data class KYCData(var pan: String, var email: String, var mobile: String)

fun isPANCard(pan: String) = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]").matcher(pan).matches()

fun checkKYCStatus(data: KYCData): MutableLiveData<String> {
    val apiResponse = MutableLiveData<String>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(ApiClient.getApiClient("https://eiscuat1.camsonline.com/")
            .create(WebserviceBuilder::class.java)
            .eKYC("https://cdc.camsonline.com/GETMethod/GetMethod.aspx",
                    "",
                    "E",
                    "INVESTOR",
                    "${data.pan}|${data.email}|${data.mobile}|com.tarrakki.app|PLUTONOMIC_INVESTOR|AU82#bx|PA|MFKYC3|SESS_ID"),
            WebserviceBuilder.ApiNames.getEKYCPage,
            object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    apiResponse.value = o.toString()
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                }
            })
    return apiResponse
}