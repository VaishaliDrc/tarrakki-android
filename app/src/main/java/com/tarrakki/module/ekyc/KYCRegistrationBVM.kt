package com.tarrakki.module.ekyc

import android.arch.lifecycle.MutableLiveData
import android.databinding.Observable
import android.databinding.ObservableField
import android.view.View
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.printResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.io.File


class KYCRegistrationBVM : FragmentViewModel() {

    val PANName = ObservableField("1ABCDE1234F")
    val occupationType = ObservableField("")
    val countryOfBirth = ObservableField("")
    val placeOfBirth = ObservableField("")
    val sourceOfIncome = ObservableField("")
    val iCertify = ObservableField(true)
    val TAXSlab = ObservableField("")
    val TINVisibility = ObservableField(View.GONE)
    val TINNumberA = ObservableField("")
    val issueByA = ObservableField("")
    val TINNumberB = ObservableField("")
    val issueByB = ObservableField("")
    val TINNumberC = ObservableField("")
    val issueByC = ObservableField("")

    init {

        iCertify.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                TINVisibility.set(if (iCertify.get()!!) View.GONE else View.VISIBLE)
            }
        })

    }
}