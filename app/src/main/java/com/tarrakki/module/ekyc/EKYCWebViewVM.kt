package com.tarrakki.module.ekyc

import android.net.Uri
import android.webkit.ValueCallback
import androidx.lifecycle.MutableLiveData
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress

class EKYCWebViewVM : FragmentViewModel() {

    var filePathCallback: ValueCallback<Array<Uri>>? = null
    val cvPhotoName = "profilePick"
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val kycData = MutableLiveData<KYCData>()

    //val redirectUrl = "https://cdc.camsonline.com/GETMethod/GetMethod.aspx"
    val redirectUrl = "http://13.235.141.219/signature/callback/"//ApiClient.IMAGE_BASE_URL + "/redirect/callback/"

    fun getEKYCPage(kycData: KYCData): MutableLiveData<String> {
        val apiResponse = MutableLiveData<String>()
        showProgress()
        subscribeToSingle(ApiClient.getApiClient("https://eiscuat1.camsonline.com/")
                .create(WebserviceBuilder::class.java)
                .eKYC(redirectUrl,
                        "",
                        "I",
                        "INVESTOR",
                        "${kycData.pan}|${kycData.email}|${kycData.mobile}|com.tarrakki.app|PLUTONOMIC_INVESTOR|AU82#bx|PA|MFKYC3|SESS_ID"),
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