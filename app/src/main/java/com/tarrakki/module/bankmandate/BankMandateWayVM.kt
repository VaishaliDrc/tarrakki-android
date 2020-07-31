package com.tarrakki.module.bankmandate

import androidx.lifecycle.MutableLiveData
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.BankDetail
import com.tarrakki.api.model.toEncrypt
import com.tarrakki.module.bankaccount.SingleButton
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.showProgress
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback
import com.tarrakki.api.subscribeToSingle

class BankMandateWayVM : FragmentViewModel() {

    val bankMandateWays = arrayListOf<WidgetsViewModel>()
    val bankMandate = ObservableField<BankDetail>()

    init {
        /*bankMandateWays.add(BankMandateWay(
                R.string.sip_mandate,
                R.string.selft_authorize_to_bank,
                R.drawable.icon_isip,
                true))*/


    }

    fun addBankMandateWays(amount: String) {
        if (amount.toDoubleOrNull()!! <= 1000000)
            bankMandateWays.add(BankMandateWay(
                    R.string.e_nach_mandate,
                    R.string.e_nach_mandate_desc,
                    R.drawable.icon_isip,
                    true))

        bankMandateWays.add(BankMandateWay(
                R.string.nach_mandate,
                R.string.we_provide_easiest_way_to,
                R.drawable.icon_nach,
                if (amount.toDoubleOrNull()!! <= 1000000)false else true ))

        bankMandateWays.add(SingleButton(R.string.txtcontinue))
    }

    fun addMandateBank(bankId: Int?, amount: String, type: String)
            : MutableLiveData<ApiResponse> {
        val json = JsonObject()
        json.addProperty("bank", bankId)
        json.addProperty("amount", amount)
        json.addProperty("mandate_type", type)
        json.addProperty("user_id", App.INSTANCE.getUserId().toString())
        val data = json.toString().toEncrypt()


        showProgress()
        val response = MutableLiveData<ApiResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .addMandateBank(data),
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
}

data class BankMandateWay(
        @StringRes
        var title: Int,
        @StringRes
        var description: Int,
        @DrawableRes var
        imgRes: Int,
        var _isSelected: Boolean = false
) : BaseObservable(), WidgetsViewModel {

    override fun layoutId(): Int {
        return R.layout.row_bank_mandate_way
    }

    @get:Bindable
    var isSelected: Boolean = _isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}