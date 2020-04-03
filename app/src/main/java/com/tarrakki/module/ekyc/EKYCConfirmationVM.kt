package com.tarrakki.module.ekyc

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.printResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import kotlin.concurrent.thread

class EKYCConfirmationVM : FragmentViewModel() {

    var kycData: KYCData? = null
    val sourceOfIncome = ObservableField("")
    val TAXSlab = ObservableField("")

    val incomeSlabs = arrayListOf<Pair<String, String>>()
    val sourcesOfIncomes = arrayListOf<Pair<String, String>>()

    init {
        incomeSlabs.setIncomeSlabs()
        sourcesOfIncomes.setSourceOfIncome()
    }

    fun saveRemainingData(/*signatureFile: File, */kycData: KYCData): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        showProgress()
        thread {

//            val requestFile = RequestBody.create(MediaType.parse("image/*"), signatureFile)
//            val multipartBody = MultipartBody.Part.createFormData("signature", signatureFile.name, requestFile)
            /*if (signatureFile != null) {
                dismissProgress()
                return@thread
            }*/
            val json = JsonObject()
//            var dobCertificate: MultipartBody.Part? = null
            json.addProperty("source_of_income", kycData.sourceOfIncome)
            json.addProperty("income_slab", kycData.taxSlab)
            json.addProperty("nominee_name", kycData.nomineeName)
            json.addProperty("nominee_relation", kycData.nomineeRelation)

            e("Plain Data=>", json.toString())
            val data = AES.encrypt(json.toString())
            e("Encrypted Data=>", data)
            val dataRequest = RequestBody.create(MediaType.parse("text/plain"), data)
            subscribeToSingle(
                    observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).saveRemainingData(
                            App.INSTANCE.getUserId(),
                            dataRequest,
                            null,
                            null),
                    apiNames = WebserviceBuilder.ApiNames.complateRegistration,
                    singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                        override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                            //signatureFile.deleteOnExit()
                            dismissProgress()
                            if (o is ApiResponse) {
                                o.printResponse()
                                if (o.status?.code == 1) {
                                    //App.INSTANCE.setCompletedRegistration(true)
                                    //App.INSTANCE.setKYClVarified(true)
                                    apiResponse.value = o
                                } else {
                                    eventKYCBSEErrorDataLog(kycData, "0")
                                    o.status?.message?.let { postError(it) }
                                }
                            } else {
                                EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                            }
                        }

                        override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                            //signatureFile.deleteOnExit()
                            dismissProgress()
                            throwable.postError()
                        }
                    }
            )
        }
        return apiResponse
    }

}