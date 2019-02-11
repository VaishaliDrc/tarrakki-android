package com.tarrakki.module.ekyc

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.tarrakki.BR
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