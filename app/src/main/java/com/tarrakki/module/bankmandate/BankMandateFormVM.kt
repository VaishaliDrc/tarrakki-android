package com.tarrakki.module.bankmandate

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.UserMandateDownloadResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.module.bankaccount.SingleButton
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.showProgress
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.subscribeToSingle

class BankMandateFormVM : FragmentViewModel() {

    val bankMandateWays = arrayListOf<WidgetsViewModel>()
    val isIMandate = ObservableField<Boolean>(true)
    val mandateResponse = ObservableField<UserMandateDownloadResponse>()
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val cvPhotoName = "profilePick"

    fun getMandateForm(mandateId: String?) : MutableLiveData<UserMandateDownloadResponse>{
        showProgress()
        val response = MutableLiveData<UserMandateDownloadResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .downloadMandateForm(mandateId),
                apiNames = WebserviceBuilder.ApiNames.getAllBanks,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                val data = o.data?.parseTo<UserMandateDownloadResponse>()
                                response.value = data
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

    init {
        bankMandateWays.add(BankMandateWay(
                R.string.download_mandate_form,
                R.string.download_bank_form_description,
                R.drawable.ic_download,
                true))

        bankMandateWays.add(BankMandateWay(
                R.string.upload_scanned_form,
                R.string.upload_scanned_form_description,
                R.drawable.ic_upload))

        bankMandateWays.add(SingleButton(R.string.txtcontinue))
    }
}