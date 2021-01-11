package com.tarrakki.module.prime_investor

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.FragmentViewModel
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*


class PrimeInvestorMutualFundListReviewVM : FragmentViewModel() {

//    val schemaList = ArrayList<Fundd?>()
    var isASC = true
    var isLimitExceed : MutableLiveData<String> = MutableLiveData("")

    val tvNoDataFoundVisibility = ObservableField(View.GONE)

    fun getFundsReview() : MutableLiveData<PrimeInvestorMutualFundRatingListResponse>{
        val Data = MutableLiveData<PrimeInvestorMutualFundRatingListResponse>()
        EventBus.getDefault().post(SHOW_PROGRESS)
        val json = JsonArray()

        for (i in 0 until App.INSTANCE.primeInvestorList.size) {
            json.add(App.INSTANCE.primeInvestorList.get(i)?.id)
        }

        val req = JsonObject()
        req.addProperty("user_id", App.INSTANCE.getUserId())
        req.add("fund_ids",json)

        val data = req.toString().toEncrypt()
        e("Request Data=>$json")
//        e("Request Encrypted Data=>$data")
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getFundsReviews(data),
                apiNames = WebserviceBuilder.ApiNames.getFundsReview,
                singleCallback = object : SingleCallback<WebserviceBuilder.ApiNames> {
                    override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                        EventBus.getDefault().post(DISMISS_PROGRESS)
                        if (o is ApiResponse) {
                            if (o.status?.code == 1) {
                                val data = o.data?.parseTo<PrimeInvestorMutualFundRatingListResponse>()
                                Data.value = data
                            }
                            else if (o.status?.code == 8) {
                                isLimitExceed.value = o?.status.message
                            }
                            else {
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
        return Data
    }
}