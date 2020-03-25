package com.tarrakki.module.birth_certificate

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.printResponse
import com.tarrakki.module.ekyc.KYCData
import com.tarrakki.module.ekyc.eventKYCBSEErrorDataLog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import java.io.File
import kotlin.concurrent.thread

class UploadDOBCertiVM : FragmentViewModel() {

    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val cvPhotoName = "verifyAccountPic"
    var kycData = MutableLiveData<KYCData>()
    var isDOB = false

    fun completeRegistrations(signatureFile: File, kycData: KYCData): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        showProgress()
        thread {

            val requestFile = RequestBody.create(MediaType.parse("image/*"), signatureFile)
            val multipartBody = MultipartBody.Part.createFormData("signature_image1", signatureFile.name, requestFile)
            /*if (signatureFile != null) {
                dismissProgress()
                return@thread
            }*/
            val json = JsonObject()
            var dobCertificate: MultipartBody.Part? = null
            json.addProperty("pan_name", kycData.nameOfPANHolder)
            if (kycData.guardianName.isEmpty()) {
                json.addProperty("pan_number", kycData.pan)
                json.addProperty("guardian_name", "")
                json.addProperty("guardian_pan", "")
                json.addProperty("date_of_birth", kycData.dob/*.toDate("dd MMM, yyyy").convertTo("dd/MM/yyyy")*/)
            } else {
                try {
                    dobCertificate = File(kycData.bobCirtificate).toMultipartBody("birth_certificate")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                json.addProperty("guardian_name", kycData.guardianName)
                json.addProperty("guardian_pan", kycData.pan)
                json.addProperty("date_of_birth", kycData.guardianDOB/*.toDate("dd MMM, yyyy").convertTo("dd/MM/yyyy")*/)
            }
            json.addProperty("address", kycData.address)
            json.addProperty("city", kycData.city)
            json.addProperty("pincode", kycData.pincode)
            json.addProperty("state", kycData.state)
            json.addProperty("country", kycData.country)
            json.addProperty("occ_code", kycData.OCCcode)
            json.addProperty("nominee_name", kycData.nomineeName)
            json.addProperty("nominee_relation", kycData.nomineeRelation)
            json.addProperty("user_id", "${App.INSTANCE.getUserId()}")
            json.addProperty("email", "${kycData.email}".toLowerCase().trim())
            json.addProperty("full_name", kycData.fullName)
            json.addProperty("mobile_number", kycData.mobile)
            json.addProperty("birth_country", kycData.birthCountry)
            json.addProperty("birth_place", kycData.birthPlace)
            json.addProperty("gender", kycData.gender)
            json.addProperty("tin_number1", "")
            json.addProperty("tin_number2", "")
            json.addProperty("tin_number3", "")
            json.addProperty("country_of_issue1", "")
            json.addProperty("country_of_issue2", "")
            json.addProperty("country_of_issue3", "")
            json.addProperty("address_type", kycData.addressType)
            json.addProperty("source_of_income", kycData.sourceOfIncome)
            json.addProperty("income_slab", kycData.taxSlab)
            json.addProperty("kyc_mode", kycData.kycMode)
            json.addProperty("in_person_verification", kycData.inPersonVerification)
            e("Plain Data=>", json.toString())
            val data = AES.encrypt(json.toString())
            e("Encrypted Data=>", data)
            val dataRequest = RequestBody.create(MediaType.parse("text/plain"), data)
            subscribeToSingle(
                    observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).completeRegistration(
                            dataRequest,
                            multipartBody,
                            dobCertificate),
                    apiNames = WebserviceBuilder.ApiNames.complateRegistration,
                    singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                        override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                            signatureFile.deleteOnExit()
                            dismissProgress()
                            if (o is ApiResponse) {
                                o.printResponse()
                                if (o.status?.code == 1) {
                                    App.INSTANCE.setCompletedRegistration(true)
                                    App.INSTANCE.setKYClVarified(true)
                                } else {
                                    eventKYCBSEErrorDataLog(kycData, "0")
                                }
                                apiResponse.value = o
                            } else {
                                EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                            }
                        }

                        override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                            signatureFile.deleteOnExit()
                            dismissProgress()
                            throwable.postError()
                        }
                    }
            )
        }
        return apiResponse
    }

//    fun saveRemainingData(signatureFile: File, kycData: KYCData): MutableLiveData<ApiResponse> {
//        val apiResponse = MutableLiveData<ApiResponse>()
//        showProgress()
//        thread {
//
//            val requestFile = RequestBody.create(MediaType.parse("image/*"), signatureFile)
//            val multipartBody = MultipartBody.Part.createFormData("signature", signatureFile.name, requestFile)
//            /*if (signatureFile != null) {
//                dismissProgress()
//                return@thread
//            }*/
//            val json = JsonObject()
//            var dobCertificate: MultipartBody.Part? = null
//            json.addProperty("pan_name", kycData.nameOfPANHolder)
//            if (kycData.guardianName.isEmpty()) {
//                json.addProperty("pan_number", kycData.pan)
//                json.addProperty("guardian_name", "")
//                json.addProperty("guardian_pan", "")
//                json.addProperty("date_of_birth", kycData.dob/*.toDate("dd MMM, yyyy").convertTo("dd/MM/yyyy")*/)
//            } else {
//                try {
//                    dobCertificate = File(kycData.bobCirtificate).toMultipartBody("BirthCertificate")
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                json.addProperty("guardian_name", kycData.guardianName)
//                json.addProperty("guardian_pan", kycData.pan)
//                json.addProperty("date_of_birth", kycData.guardianDOB/*.toDate("dd MMM, yyyy").convertTo("dd/MM/yyyy")*/)
//            }
//            json.addProperty("address", kycData.address)
//            json.addProperty("city", kycData.city)
//            json.addProperty("pincode", kycData.pincode)
//            json.addProperty("state", kycData.state)
//            json.addProperty("country", kycData.country)
//            json.addProperty("occ_code", kycData.OCCcode)
//            json.addProperty("nominee_name", kycData.nomineeName)
//            json.addProperty("nominee_relation", kycData.nomineeRelation)
//            json.addProperty("user_id", "${App.INSTANCE.getUserId()}")
//            json.addProperty("email", "${kycData.email}".toLowerCase().trim())
//            json.addProperty("full_name", kycData.fullName)
//            json.addProperty("mobile_number", kycData.mobile)
//            json.addProperty("birth_country", kycData.birthCountry)
//            json.addProperty("birth_place", kycData.birthPlace)
//            json.addProperty("gender", kycData.gender)
//            json.addProperty("tin_number1", "")
//            json.addProperty("tin_number2", "")
//            json.addProperty("tin_number3", "")
//            json.addProperty("country_of_issue1", "")
//            json.addProperty("country_of_issue2", "")
//            json.addProperty("country_of_issue3", "")
//            json.addProperty("address_type", kycData.addressType)
//            json.addProperty("source_of_income", kycData.sourceOfIncome)
//            json.addProperty("income_slab", kycData.taxSlab)
//            json.addProperty("kyc_mode", kycData.kycMode)
//            json.addProperty("in_person_verification", kycData.inPersonVerification)
//            e("Plain Data=>", json.toString())
//            val data = AES.encrypt(json.toString())
//            e("Encrypted Data=>", data)
//            val dataRequest = RequestBody.create(MediaType.parse("text/plain"), data)
//            subscribeToSingle(
//                    observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).saveRemainingData(
//                            App.INSTANCE.getUserId(),
//                            dataRequest,
//                            multipartBody,
//                            dobCertificate),
//                    apiNames = WebserviceBuilder.ApiNames.complateRegistration,
//                    singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
//                        override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
//                            signatureFile.deleteOnExit()
//                            dismissProgress()
//                            if (o is ApiResponse) {
//                                o.printResponse()
//                                if (o.status?.code == 1) {
//                                    App.INSTANCE.setCompletedRegistration(true)
//                                    App.INSTANCE.setKYClVarified(true)
//                                } else {
//                                    eventKYCBSEErrorDataLog(kycData, "0")
//                                }
//                                apiResponse.value = o
//                            } else {
//                                EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
//                            }
//                        }
//
//                        override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
//                            signatureFile.deleteOnExit()
//                            dismissProgress()
//                            throwable.postError()
//                        }
//                    }
//            )
//        }
//        return apiResponse
//    }

}