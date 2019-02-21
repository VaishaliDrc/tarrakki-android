package com.tarrakki.module.ekyc

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.util.Log.e
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.printResponse
import com.tarrakki.api.model.toDecrypt
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.util.regex.Pattern


class EKYCVM : FragmentViewModel() {

    var kycData: KYCData? = null

}

data class KYCData(var pan: String) : BaseObservable() {

    @get:Bindable
    var nameOfPANHolder = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nameOfPANHolder)
        }

    @get:Bindable
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }
    @get:Bindable
    var mobile: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.mobile)
        }
    @get:Bindable
    var dob: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dob)
        }
    @get:Bindable
    var guardianName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.guardianName)
        }
    @get:Bindable
    var guardianPAN: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.guardianPAN)
        }
    @get:Bindable
    var addressType: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.addressType)
        }
    @get:Bindable
    var address: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.address)
        }
    @get:Bindable
    var city: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
        }
    @get:Bindable
    var pincode: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.pincode)
        }
    @get:Bindable
    var state: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.start)
        }
    @get:Bindable
    var country: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.country)
        }
    @get:Bindable
    var OCCcode: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.oCCcode)
        }
    @get:Bindable
    var nomineeName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nomineeName)
        }
    @get:Bindable
    var nomineeRelation: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nomineeRelation)
        }
    @get:Bindable
    var fullName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.fullName)
        }
    @get:Bindable
    var birthCountry: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.birthCountry)
        }
    @get:Bindable
    var birthPlace: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.birthPlace)
        }
    @get:Bindable
    var gender: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.gender)
        }
    @get:Bindable
    var sourceOfIncome: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.sourceOfIncome)
        }
    @get:Bindable
    var taxSlab = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.taxSlab)
        }
    @get:Bindable
    var tinNumber1: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.tinNumber1)
        }
    @get:Bindable
    var tinNumber2: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.tinNumber2)
        }
    @get:Bindable
    var tinNumber3: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.tinNumber3)
        }
    @get:Bindable
    var countryOfIssue1: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.countryOfIssue1)
        }
    @get:Bindable
    var countryOfIssue2: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.countryOfIssue2)
        }
    @get:Bindable
    var countryOfIssue3: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.countryOfIssue3)
        }

    constructor(pan: String, email: String, mobile: String) : this(pan) {
        this.email = email
        this.mobile = mobile
    }

}

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

fun saveKYCData(kycData: KYCData): MutableLiveData<ApiResponse> {

    val apiResponse = MutableLiveData<ApiResponse>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    val json = JsonObject()
    json.addProperty("pan_name", kycData.nameOfPANHolder)
    json.addProperty("pan_number", kycData.pan)
    json.addProperty("date_of_birth", kycData.dob)
    json.addProperty("guardian_name", kycData.guardianName)
    //json.addProperty("guardian_pan", kycData.pan)
    json.addProperty("address", kycData.address)
    json.addProperty("city", kycData.city)
    json.addProperty("pincode", kycData.pincode)
    json.addProperty("state", kycData.state)
    json.addProperty("country", kycData.country)
    json.addProperty("occ_code", kycData.OCCcode)
    json.addProperty("nominee_name", kycData.nomineeName)
    json.addProperty("nominee_relation", kycData.nomineeRelation)
    json.addProperty("email", kycData.email)
    json.addProperty("full_name", kycData.fullName)
    json.addProperty("mobile_number", kycData.mobile)
    json.addProperty("birth_country", kycData.birthCountry)
    json.addProperty("birth_place", kycData.birthPlace)
    json.addProperty("gender", kycData.gender)
    json.addProperty("tin_number1", kycData.tinNumber1)
    json.addProperty("tin_number2", kycData.tinNumber2)
    json.addProperty("tin_number3", kycData.tinNumber3)
    json.addProperty("country_of_issue1", kycData.countryOfIssue1)
    json.addProperty("country_of_issue2", kycData.countryOfIssue2)
    json.addProperty("country_of_issue3", kycData.countryOfIssue3)
    json.addProperty("address_type", kycData.addressType)
    json.addProperty("source_of_income", kycData.sourceOfIncome)
    json.addProperty("income_slab", kycData.taxSlab)
    e("Plain Data=>", json.toString())
    val data = AES.encrypt(json.toString())
    e("Encrypted Data=>", data)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).saveKYCdata(App.INSTANCE.getUserId(), data),
            apiNames = WebserviceBuilder.ApiNames.KYCData,
            singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ApiResponse) {
                        o.printResponse()
                        apiResponse.value = o
                    } else {
                        EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                    }
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                }
            }
    )
    return apiResponse
}


fun getKYCData(): MutableLiveData<KYCData> {

    val apiResponse = MutableLiveData<KYCData>()
    EventBus.getDefault().post(SHOW_PROGRESS)
    subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).gatKYCdata(App.INSTANCE.getUserId()),
            apiNames = WebserviceBuilder.ApiNames.KYCData,
            singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    if (o is ApiResponse) {
                        o.printResponse()
                        try {
                            val jsonObject = JSONObject(o.data?.toDecrypt())
                            val jsonData = JSONObject(jsonObject.optString("data"))
                            val kycData = KYCData(jsonData.optString("pan_number"))
                            kycData.nameOfPANHolder = jsonData.optString("pan_name")
                            kycData.countryOfIssue2 = jsonData.optString("country_of_issue2")
                            kycData.addressType = jsonData.optString("address_type")
                            kycData.address = jsonData.optString("address")
                            kycData.country = jsonData.optString("country")
                            kycData.OCCcode = jsonData.optString("occ_code")
                            kycData.countryOfIssue1 = jsonData.optString("country_of_issue1")
                            kycData.mobile = jsonData.optString("mobile_number")
                            kycData.tinNumber1 = jsonData.optString("tin_number1")
                            kycData.tinNumber3 = jsonData.optString("tin_number3")
                            kycData.gender = jsonData.optString("gender")
                            kycData.tinNumber2 = jsonData.optString("tin_number2")
                            kycData.email = jsonData.optString("email")
                            kycData.nomineeRelation = jsonData.optString("nominee_relation")
                            kycData.fullName = jsonData.optString("full_name")
                            kycData.birthCountry = jsonData.optString("birth_country")
                            kycData.dob = jsonData.optString("date_of_birth")
                            kycData.city = jsonData.optString("city")
                            kycData.birthPlace = jsonData.optString("birth_place")
                            kycData.guardianName = jsonData.optString("guardian_name")
                            kycData.state = jsonData.optString("state")
                            kycData.sourceOfIncome = jsonData.optString("source_of_income")
                            kycData.taxSlab = jsonData.optString("income_slab")
                            kycData.countryOfIssue3 = jsonData.optString("country_of_issue3")
                            kycData.pincode = jsonData.optString("pincode")
                            kycData.nomineeName = jsonData.optString("nominee_name")
                            apiResponse.value = kycData
                        } catch (e: Exception) {
                            e.printStackTrace()
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        }
                    } else {
                        EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                    }
                }

                override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                    EventBus.getDefault().post(DISMISS_PROGRESS)
                    throwable.postError()
                }
            }
    )
    return apiResponse
}