package com.tarrakki.module.transactionConfirm

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.support.annotation.DrawableRes
import android.view.View
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.TransactionStatusResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import com.tarrakki.api.subscribeToSingle
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.showProgress
import java.util.*

class TransactionConfirmVM : FragmentViewModel() {

    val list = ArrayList<TransactionConfirm>()
    val isFailed = ObservableField(true)
    val isFromNEFT_RTGS = ObservableField(View.GONE)
    val hasPending = ObservableField(View.GONE)

    fun getTransactionStatus(dataRequest: String): MutableLiveData<TransactionStatusResponse> {
        val apiResponse = MutableLiveData<TransactionStatusResponse>()
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .transactionStatus(dataRequest),
                apiNames = WebserviceBuilder.ApiNames.deleteCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val data = o.data?.parseTo<TransactionStatusResponse>()
                                apiResponse.value = data
                            } else {
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        } else {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        }
                        dismissProgress()
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        EventBus.getDefault().post(ShowError("${throwable.message}"))
                    }
                }
        )
        return apiResponse
    }

    data class TransactionConfirm(val name: String, val type: String, val amount: String,
                                  val isSuccess: Boolean, val status: ArrayList<TranscationStatus>)

    data class TranscationStatus(val name: String, val description: String, val status: Int) {

        var actualStatus: String = ""
            get() = when (status) {
                1 -> "Completed"
                2 -> "In Progress"
                else -> "Pending"
            }

        @DrawableRes
        var actualStatusDrawable: Int = R.drawable.shape_pending_bg
            get() = when (status) {
                1 -> R.drawable.shape_completed_bg
                2 -> R.drawable.shape_progress_bg
                else -> R.drawable.shape_pending_bg
            }

        @DrawableRes
        var actualStatusIcon: Int = R.drawable.ic_round_pending
            get() = when (status) {
                1 -> R.drawable.ic_round_completed
                2 -> R.drawable.in_round_progress
                else -> R.drawable.ic_round_pending
            }
    }

    data class TranscationStatuss(val name: String, val description: String, val status: String) {

        var actualStatus: String = ""
            get() = when {
                "completed".equals(status, true) -> "Completed"
                "In progress".equals(status, true) -> "In Progress"
                "Failed".equals(status, true) -> "Failed"
                else -> "Pending"
            }

        @DrawableRes
        var actualStatusDrawable: Int = R.drawable.shape_pending_bg
            get() = when {
                "completed".equals(status, true) -> R.drawable.shape_completed_bg
                "In progress".equals(status, true) -> R.drawable.shape_progress_bg
                "Failed".equals(status, true) -> R.drawable.shape_failed_transn_bg
                else -> R.drawable.shape_pending_bg
            }

        @DrawableRes
        var actualStatusIcon: Int = R.drawable.ic_round_pending
            get() = when {
                "completed".equals(status, true) -> R.drawable.ic_round_completed
                "In progress".equals(status, true) -> R.drawable.in_round_progress
                "Failed".equals(status, true) -> R.drawable.ic_round_failed
                else -> R.drawable.ic_round_pending
            }
    }
}