package com.tarrakki.module.paymentmode

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowECutOffTimeDialog
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ResourceUtils
import java.math.BigInteger

class PaymentModeVM : FragmentViewModel() {
    val utrNumber = ObservableField<String>("")

    val confirmOrder = MutableLiveData<ConfirmTransactionResponse>()
    val totalOrder = ObservableField<BigInteger>(BigInteger.ZERO)
    val isNetBanking = ObservableField<Boolean>(false)
    val isNEFTRTGS = ObservableField<Boolean>(false)
    val isUPI = ObservableField<Boolean>(true)
    val accountNumber = ObservableField<String>("")
    val branchName = ObservableField<String>("")
    val introduction = ObservableField<String>("Your transaction will now be processed by Bombay Stock Exchange - Star platform.")
    val paymentType: ArrayList<String> = arrayListOf()
    val selectedPaymentType = ObservableField<String>(ResourceUtils.getString(R.string.UPI))
    val upiName = ObservableField<String>("")
    val order_ids = arrayListOf<String>()
    val validatePaymentData = MutableLiveData<ValidationPaymentResponse>()
    var availablePaymentMethodList: ArrayList<String> = arrayListOf()
    var notAvailablePaymentMethodList: ArrayList<String> = arrayListOf()
    var totalPaymentModeList: ArrayList<String> = arrayListOf()


    init {
        paymentType.add(ResourceUtils.getString(R.string.UPI))
        paymentType.add(ResourceUtils.getString(R.string.net_banking))
        paymentType.add(ResourceUtils.getString(R.string.neft_rtgs))

        totalPaymentModeList.add(ResourceUtils.getString(R.string.UPI))
        totalPaymentModeList.add(ResourceUtils.getString(R.string.direct))
        totalPaymentModeList.add(ResourceUtils.getString(R.string.NEFT_rtgs))
    }

    fun paymentOrder(data: String): MutableLiveData<ApiResponse> {
        val apiResponse = MutableLiveData<ApiResponse>()
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .paymentOrder(data),
                apiNames = WebserviceBuilder.ApiNames.deleteCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        if (o is ApiResponse) {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                apiResponse.value = o
                            } else if (o.status?.code == 5) {
                                EventBus.getDefault().post(
                                        ShowECutOffTimeDialog(
                                                title = App.INSTANCE.getString(R.string.cut_of_time_title),
                                                error = App.INSTANCE.getString(R.string.cut_of_desc_code_5),
                                                msg = "${o.status.message}"))
                            } else if (o.status?.code == 6) {
                                EventBus.getDefault().post(
                                        ShowECutOffTimeDialog(
                                                title = App.INSTANCE.getString(R.string.cut_of_time_title),
                                                error = App.INSTANCE.getString(R.string.cut_of_msg_code_6),
                                                msg = "${o.status.message}"))
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

    fun getOrderPaymentValidation(): MutableLiveData<ValidationPaymentResponse> {
        val apiResponse = MutableLiveData<ValidationPaymentResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .validateOrderPayment(App.INSTANCE.getUserId(), JSONArray(order_ids).toString()),
                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        e("Api Response=>${o.data?.toDecrypt()}")
                        if (o.status?.code == 1) {
                            val bank = o.data?.parseTo<ValidationPaymentResponse>()
                            apiResponse.value = bank
                        } else {
                            EventBus.getDefault().post(ShowError("${o.status?.message}"))
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        EventBus.getDefault().post(ShowError("${throwable.message}"))
                    }
                }
        )
        return apiResponse
    }
}