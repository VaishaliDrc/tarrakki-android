package com.tarrakki.module.paymentmode

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.showProgress
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import java.math.BigInteger

class PaymentModeVM : FragmentViewModel() {
     val utrNumber = ObservableField<String>("")

     val confirmOrder = MutableLiveData<ConfirmTransactionResponse>()
     val totalOrder = ObservableField<BigInteger>(BigInteger.ZERO)
     val isNetBanking = ObservableField<Boolean>(true)
     val isNEFTRTGS = ObservableField<Boolean>(false)
     val accountNumber = ObservableField<String>("")
     val branchName = ObservableField<String>("")

     val introduction = ObservableField<String>("Your transaction will now be processed by Bombay Stock Exchange - Star platform.")

     fun paymentOrder(data : String): MutableLiveData<ApiResponse> {
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
}