package com.tarrakki.module.myprofile

import android.net.Uri
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import java.io.File

class MyProfileVM : FragmentViewModel() {

    val isMobileVerified = ObservableField(true)
    val isEmailVerified = ObservableField(false)
    val profileUrl = ObservableField("")
    val fName = ObservableField("")
    val guardian = ObservableField("")
    val guardianPANNumber = ObservableField("")
    val email = ObservableField("")
    val mobile = ObservableField("")
    val PANNumber = ObservableField("")
    val PANName = ObservableField("")
    val nominiName = ObservableField("")
    val nominiRelationship = ObservableField("")
    val address = ObservableField("")
    val city = ObservableField("")
    val pincode = ObservableField("")
    val state = ObservableField("")
    val country = ObservableField("")
    val isEdit = ObservableField(false)
    val alpha = ObservableField<Float>(0.4f)
    val cvPhotoName = "profilePick"
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val signatureBtnVisibility = ObservableField(View.VISIBLE)


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

    fun updateSignatureImage(signatureUri: Uri?): MutableLiveData<ApiResponse> {
        showProgress()
        var signatureImage: MultipartBody.Part? = null
        val userId = App.INSTANCE.getUserId()
        var file: File? = null
        if (signatureUri != null) {
            file = File(getPath(signatureUri) ?: "")
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            signatureImage = MultipartBody.Part.createFormData("signature_image", file.name, requestFile)
        }

        val response = MutableLiveData<ApiResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient()
                        .create(WebserviceBuilder::class.java)
                        .updateProfile(userId, signatureImage),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                response.value = o
                                try {
                                    file?.deleteOnExit()
                                } catch (e: Exception) {
                                }
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

    fun updateProfileImage(profileUri: Uri?): MutableLiveData<ApiResponse> {
        showProgress()
        var profileImage: MultipartBody.Part? = null
        val userId = App.INSTANCE.getUserId()
        var file: File? = null
        if (profileUri != null) {
            file = File(getPath(profileUri) ?: "")
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            profileImage = MultipartBody.Part.createFormData("user_profile_image", file.name, requestFile)
        }

        val response = MutableLiveData<ApiResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient()
                        .create(WebserviceBuilder::class.java)
                        .updateProfile(userId, profileImage),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                response.value = o
                                try {
                                    file?.deleteOnExit()
                                } catch (e: Exception) {
                                }
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

    fun updateProfile(): MutableLiveData<ApiResponse> {
        showProgress()

        val json = JsonObject()
        json.addProperty("full_name", fName.get())
        json.addProperty("mobile_number", mobile.get())
        json.addProperty("email", "${email.get()}".toLowerCase().trim())
        json.addProperty("nominee_name", nominiName.get())
        json.addProperty("nominee_relationship", nominiRelationship.get())

        json.addProperty("corr_address", address.get())
        json.addProperty("corr_city", city.get())
        json.addProperty("corr_pincode", pincode.get())
        json.addProperty("corr_state", state.get())
        json.addProperty("corr_country", country.get())

        val data = json.toString().toEncrypt()

        val userId = App.INSTANCE.getUserId()

        val response = MutableLiveData<ApiResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient()
                        .create(WebserviceBuilder::class.java)
                        .updateProfile(userId, data),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                response.value = o
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