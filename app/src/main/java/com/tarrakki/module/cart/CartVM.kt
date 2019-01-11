package com.tarrakki.module.cart

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.CartData
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import com.tarrakki.module.invest.Fund
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.DISMISS_PROGRESS
import org.supportcompact.ktx.SHOW_PROGRESS
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle

class CartVM : FragmentViewModel() {

    val funds = arrayListOf<Fund>()
    val userGoalList = MutableLiveData<CartData>()
    var totalSip: ObservableField<String> = ObservableField("")
    var totalLumpsum: ObservableField<String> = ObservableField("")


    init {
        funds.add(Fund(
                "SBI Banking and Financial Services Growth Direct Plan",
                "Sectoral/Thematic",
                0.93f,
                18.2f,
                19.4f,
                13.7f,
                5.2f,
                5.2f)
        )

        funds.add(Fund(
                "DSP Blackrock Natural Resources and New Energy Growth Direct Plan",
                "Sectoral/Thematic",
                0.26f,
                18.5f,
                4.6f,
                14.8f,
                25.7f,
                6.5f).apply { hasOneTimeAmount = true }
        )
    }


    fun getCartItem(): MutableLiveData<CartData> {
        EventBus.getDefault().post(SHOW_PROGRESS)
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getCartItem(),
                apiNames = WebserviceBuilder.ApiNames.getCartItem,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if ((o.status?.code == 1)) {
                                val data = o.data?.parseTo<CartData>()
                                userGoalList.value = data
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
        return userGoalList
    }


}