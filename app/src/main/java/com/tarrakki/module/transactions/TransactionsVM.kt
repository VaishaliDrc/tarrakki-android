package com.tarrakki.module.transactions

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.IntDef
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.BR
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import kotlin.concurrent.thread

class TransactionsVM : FragmentViewModel() {

    val transactions = arrayListOf<Transactions>()
    val pendingTransactions = arrayListOf<WidgetsViewModel>()
    val hasOptionMenu = MutableLiveData<Boolean>()
    val onBack = MutableLiveData<Boolean>()


    init {

        for (count in 1..10) {
            transactions.add(Transactions(
                    "SBI Banking and Financial Services Growth Direct Plan",
                    25000.00,
                    "PIP (SIP)",
                    "Ex.Physical",
                    "Oct 10, 2018 - 10:15 AM",
                    100,
                    25.00,
                    false))
        }

        for (count in 1..10) {
            pendingTransactions.add(Transactions(
                    "SBI Banking and Financial Services Growth Direct Plan",
                    25000.00,
                    "PIP (SIP)",
                    "Ex.Physical",
                    "Oct 10, 2018 - 10:15 AM",
                    100,
                    25.00).apply {
                when (count) {
                    2 -> transactionType = Transactions.COMPLETED
                    3 -> transactionType = Transactions.UPCOMING
                    4 -> transactionType = Transactions.UNPAID
                    5 -> transactionType = Transactions.FAILED
                }
            })
        }
    }

    fun getTransactions(transactionType: String = TransactionApiResponse.ALL, offset: Int = 0): MutableLiveData<TransactionApiResponse> {
        if (offset == 0)
            showProgress()
        val apiResponse = MutableLiveData<TransactionApiResponse>()
        val json = JsonObject()
        json.addProperty("limit", 10)
        json.addProperty("offset", offset)
        json.addProperty("transaction_type", transactionType)
        val data = json.toString().toEncrypt()
        json.printRequest()
        data.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getTransactions("74"/*App.INSTANCE.getUserId()*/, data),
                apiNames = WebserviceBuilder.ApiNames.transactions,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                thread {
                                    val response = o.data?.parseTo<TransactionApiResponse>()
                                    apiResponse.postValue(response)
                                }
                                dismissProgress()
                            } else {
                                dismissProgress()
                                EventBus.getDefault().post(ShowError("${o.status?.message}"))
                            }
                        } else {
                            EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                        }
                    }

                    override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                        dismissProgress()
                        throwable.postError()
                    }
                }
        )
        return apiResponse
    }

}

data class Transactions(
        var name: String,
        var amount: Double,
        var type: String,
        var mode: String,
        var date: String,
        var units: Int,
        var NAV: Double,
        val isPending: Boolean = true
) : BaseObservable(), WidgetsViewModel {

    companion object {
        const val IN_PROGRESS = 0
        const val COMPLETED = 1
        const val UPCOMING = 2
        const val UNPAID = 3
        const val FAILED = 4

        @IntDef(IN_PROGRESS, COMPLETED, UPCOMING, UNPAID, FAILED)
        @Retention(value = AnnotationRetention.SOURCE)
        annotation class TransactionType
    }

    @TransactionType
    var transactionType = R.layout.row_inprogress_transactions

    override fun layoutId(): Int {
        return when (transactionType) {
            IN_PROGRESS -> R.layout.row_inprogress_transactions
            COMPLETED -> R.layout.row_completed_transactions
            UPCOMING -> R.layout.row_upcoming_transactions
            UNPAID -> R.layout.row_unpaid_transactions
            FAILED -> R.layout.row_failed_transactions
            else -> R.layout.row_inprogress_transactions
        }
    }

    @get:Bindable
    var isSelected = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

}