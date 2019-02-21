package com.tarrakki.module.paymentmode

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.api.model.ConfirmOrderResponse
import com.tarrakki.api.model.ConfirmTransactionResponse
import org.supportcompact.FragmentViewModel

class PaymentModeVM : FragmentViewModel() {
     val confirmOrder = MutableLiveData<ConfirmTransactionResponse>()
     val totalOrder = ObservableField<Double>(0.0)
     val isNetBanking = ObservableField<Boolean>(true)
     val isNEFTRTGS = ObservableField<Boolean>(false)
     val accountNumber = ObservableField<String>("")
     val branchName = ObservableField<String>("")

     val introduction = ObservableField<String>("Your transaction will now be processed by Bombay Stock Exchange - Star platform.")
}