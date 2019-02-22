package com.tarrakki.module.transactionConfirm

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.DrawableRes
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.TransactionStatusResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.showProgress
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.util.*

class TransactionConfirmVM : FragmentViewModel() {

    val list = ArrayList<TransactionConfirm>()

    init {
        val statuslist = arrayListOf<TranscationStatus>()
        statuslist.add(TranscationStatus("Mutual Fund Payment", "via Net Banking", 1))
        statuslist.add(TranscationStatus("Order Placed with AMC", "", 2))
        statuslist.add(TranscationStatus("Investment Confirmation", "", 3))
        statuslist.add(TranscationStatus("Units Alloted", "", 3))

        list.add(TransactionConfirm("HDFC GOLD Fund Direct Growth", "Lumpsump", "10000", true, statuslist))
        list.add(TransactionConfirm("HDFC GOLD Fund Direct Growth", "SIP", "20000", false, statuslist))
    }

    fun getTransactionStatus(dataRequest : String): MutableLiveData<TransactionStatusResponse> {
        val apiResponse = MutableLiveData<TransactionStatusResponse>()
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .transactionStatus(dataRequest),
                apiNames = WebserviceBuilder.ApiNames.deleteCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
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
            get() = when (status) {
                "completed" -> "Completed"
                "In progress" -> "In Progress"
                else -> "Pending"
            }

        @DrawableRes
        var actualStatusDrawable: Int = R.drawable.shape_pending_bg
            get() = when (status) {
                "completed" -> R.drawable.shape_completed_bg
                "In progress" -> R.drawable.shape_progress_bg
                else -> R.drawable.shape_pending_bg
            }

        @DrawableRes
        var actualStatusIcon: Int = R.drawable.ic_round_pending
            get() = when (status) {
                "completed"-> R.drawable.ic_round_completed
                "In progress" -> R.drawable.in_round_progress
                else -> R.drawable.ic_round_pending
            }
    }
}