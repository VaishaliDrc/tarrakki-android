package com.tarrakki.module.prime_investor

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.tarrakki.App
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress

class PrimeInvestorMutualFundListVM : FragmentViewModel() {

    val schemaList = ArrayList<Fundd?>()
    val selectedSchemeList = ArrayList<Fundd?>()
    val limit = 20
    var offset = 0
    var isGrowthScheme = true
    var isASC = true
    var search: ObservableField<String> = ObservableField("")
    val loadMore = ObservableField(false)
    val tvNoDataFoundVisibility = ObservableField(View.GONE)
    var totalFund: Int = 0

    var isHeaderVisible: ObservableField<Int> = ObservableField(View.GONE)
    var headerText: ObservableField<String> = ObservableField("")

    fun getMutualFundsAPI(): MutableLiveData<PrimeInvestorMutualFundsListResponse> {
        val Data = MutableLiveData<PrimeInvestorMutualFundsListResponse>()
        if (!loadMore.get()!!)
            showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getMutualFunds(App.INSTANCE.getUserId(), search.get(), getSort(), !isGrowthScheme, isGrowthScheme, offset, limit),

                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        o.printResponse()
                        if (o.status?.code == 1) {
                            val data = o.data?.parseTo<PrimeInvestorMutualFundsListResponse>()
                            data?.let {

                                if (it.userInfo.isNullOrEmpty()) {
                                    isHeaderVisible.set(View.GONE)
                                } else {
                                    isHeaderVisible.set(View.VISIBLE)
                                }
                                headerText.set(it.userInfo?:"")
                                totalFund = it.totalFunds
                            }
                            if (loadMore.get()!!) {
                                if (data != null) {
                                    schemaList.addAll(data.funds)
                                    offset = data.offset
//                                    Data.value = data
                                }
                                loadMore.set(false)
                            } else {
                                dismissProgress()
                                data?.let {
                                    tvNoDataFoundVisibility.set(if (it.funds.isEmpty()) View.VISIBLE else View.GONE)
                                    schemaList.addAll(data.funds)
                                    offset = data.offset
                                    Data.value = data
                                }
                            }
                        } else {
                            postError("${o.status?.message}")
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        throwable.postError()
                        dismissProgress()
                    }
                }
        )
        return Data
    }

    private fun getSort(): String? {

        return if (isASC) "asc" else "desc"

    }

}