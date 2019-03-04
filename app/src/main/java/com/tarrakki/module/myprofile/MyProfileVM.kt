package com.tarrakki.module.myprofile

import android.arch.lifecycle.MutableLiveData
import android.databinding.Observable
import android.databinding.ObservableField
import android.net.Uri
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
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
import java.net.URL

class MyProfileVM : FragmentViewModel() {

    val isMobileVerified = ObservableField(true)
    val isEmailVerified = ObservableField(false)
    val profileUrl = ObservableField("")
    val fName = ObservableField("Himanshu Pratap")
    val guardian = ObservableField("Ravi Pratap")
    val guardianPANNumber = ObservableField("1ABCDE1234F")
    val email = ObservableField("hinnanshu.pratap@gnnail.com")
    val mobile = ObservableField("9253493800")
    val PANNumber = ObservableField("1ABCDE1234F")
    val PANName = ObservableField("Himanshu Pratap")
    val nominiName = ObservableField("Himanshu Pratap")
    val nominiRelationship = ObservableField("Father")
    val isEdit = ObservableField(false)

    val alpha = ObservableField<Float>(0.4f)
    val cvPhotoName = "profilePick"
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181

    init {
        isEdit.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isEdit.get()?.let {
                    alpha.set(if (it) 1f else 0.4f)
                }
            }
        })
    }

    fun getUserProfile(): MutableLiveData<UserProfileResponse> {
        val apiResponse = MutableLiveData<UserProfileResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient()
                        .create(WebserviceBuilder::class.java)
                        .getUserProfile(),
                apiNames = WebserviceBuilder.ApiNames.getGoals,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                o.printResponse()
                                val data = o.data?.parseTo<UserProfileResponse>()
                                apiResponse.value = data
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
        )
        return apiResponse
    }

    fun updateProfile(profileUri: Uri?, signatureUri: Uri?): MutableLiveData<ApiResponse> {
        showProgress()
        var profileImage: MultipartBody.Part? = null
        var signatureImage: MultipartBody.Part? = null

        val json = JsonObject()
        json.addProperty("full_name",fName.get())
        json.addProperty("mobile_number",mobile.get())
        json.addProperty("email", email.get())
        json.addProperty("nominee_name", nominiName.get())
        json.addProperty("nominee_relationship", nominiRelationship.get())
        val data = json.toString().toEncrypt()

        val paramData = RequestBody.create(
                MediaType.parse("text/plain"),
                data)

        val userId = App.INSTANCE.getUserId()

        if (profileUri != null) {
            val file = File(getPath(profileUri))
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            profileImage = MultipartBody.Part.createFormData("user_profile_image", file.name, requestFile)
        }
        if (signatureUri != null) {
            val file = File(getPath(signatureUri))
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            signatureImage = MultipartBody.Part.createFormData("signature_image", file.name, requestFile)
        }

        val response = MutableLiveData<ApiResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient()
                        .create(WebserviceBuilder::class.java)
                        .updateProfile(userId, paramData,profileImage,signatureImage),
        apiNames = WebserviceBuilder.ApiNames.getAllBanks,
        singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
            override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                if (o is ApiResponse) {
                    if (o.status?.code == 1) {
                        response.value = o
                        EventBus.getDefault().post(ShowError("${o.status?.message}"))
                    } else {
                        EventBus.getDefault().post(ShowError("${o.status?.message}"))
                    }
                    dismissProgress()
                } else {
                    dismissProgress()
                    EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                }
            }

            override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                EventBus.getDefault().post(DISMISS_PROGRESS)
                EventBus.getDefault().post(ShowError("${throwable.message}"))
            }
        }
        )
        return response
    }

    fun getOTP(data: String): MutableLiveData<ApiResponse> {
        val getOTP = MutableLiveData<ApiResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).getOTP(data),
                apiNames = WebserviceBuilder.ApiNames.getOTP,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                getOTP.value = o
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
        )
        return getOTP
    }
}