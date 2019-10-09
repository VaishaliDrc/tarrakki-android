package com.tarrakki.module.verifybankaccount

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.net.Uri
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.UserBanksResponse
import com.tarrakki.api.subscribeToSingle
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import java.io.File

class VerifyBankAccountVM : FragmentViewModel() {

    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val cvPhotoName = "verifyAccountPic"
    val uploadUri = ObservableField<String>("")

    fun uploadBankDoc(userBankData: UserBanksResponse?): MutableLiveData<ApiResponse> {
        showProgress()

        //val id = mandateResponse.get()?.data?.id
        val file = File(getPath(Uri.parse(uploadUri.get().toString())))
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val accountNumber = RequestBody.create(MediaType.parse("text"), userBankData?.data?.bankDetail?.accountNumber)
        val ifscCode = RequestBody.create(MediaType.parse("text"), userBankData?.data?.bankDetail?.ifsc_code)
        val accountTypeSB = RequestBody.create(MediaType.parse("text"), userBankData?.data?.bankDetail?.accountTypeBse)
        val userId = RequestBody.create(MediaType.parse("text"), App.INSTANCE.getUserId())
        val body = MultipartBody.Part.createFormData("verification_document", file.name, requestFile)

        val response = MutableLiveData<ApiResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .updateUserBankDetails(accountNumber,
                                ifscCode,
                                accountTypeSB,
                                userId,
                                userBankData?.data?.bankDetail?.id.toString(), body),
                apiNames = WebserviceBuilder.ApiNames.updateUserBankDetails,
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

}