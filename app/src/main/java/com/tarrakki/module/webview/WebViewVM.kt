package com.tarrakki.module.webview

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.net.Uri
import android.webkit.ValueCallback
import com.tarrakki.api.WebserviceBuilder
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class WebViewVM : FragmentViewModel() {

    var filePathCallback: ValueCallback<Array<Uri>>? = null
    val cvPhotoName = "profilePick"
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val btnContinua = ObservableField(true)

    fun getEKYCPage(): MutableLiveData<String> {
        val apiResponse = MutableLiveData<String>()
        showProgress()
        subscribeToSingle(ApiClient.getApiClient("https://eiscuat1.camsonline.com/")
                .create(WebserviceBuilder::class.java)
                .eKYC("https://cdc.camsonline.com/GETMethod/GetMethod.aspx",
                        "",
                        "I",
                        "INVESTOR",
                        "AJNPV8599B|abc@gmail.com|8460421008|com.tarrakki.app|PLUTONOMIC_INVESTOR|AU82#bx|PA|MFKYC3|SESS_ID"),
                WebserviceBuilder.ApiNames.getEKYCPage,
                object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        apiResponse.value = o.toString()
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        throwable.postError()
                    }
                })
        return apiResponse
    }

}