package com.tarrakki.module.consumerloansliquilons

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

class ConsumerLoansLiquiLoanVM : FragmentViewModel(){

    var schemaList = ArrayList<Scheme>()
    var borrowers = ObservableField<String>()
    var disbursementMonth = ObservableField<String>()
    var disbursements = ObservableField<String>()
    var gross_npa = ObservableField<String>()
    var isShow = ObservableField<Boolean>()
    var lenders = ObservableField<String>()
    var totalDisbursements = ObservableField<String>()

    fun getLiquiloansSchemaAPI(): MutableLiveData<GetLiquiLoansSchemaBaseResponse> {
        val liquiloansData = MutableLiveData<GetLiquiLoansSchemaBaseResponse>()
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                        .getLiquiloansScheme(),

                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        o.printResponse()
                        if (o.status?.code == 1){
                            val data = o.data?.parseTo<GetLiquiLoansSchemaBaseResponse>()
                            data?.let {
                                liquiloansData.value = it
                                schemaList.clear()
                                schemaList.addAll(it.data.schemes)
                            }
                        }else{
                            postError("${o.status?.message}")
                        }
                        dismissProgress()
                    }

                    override fun onFailure(throwable: Throwable) {
                        throwable.postError()
                        dismissProgress()
                    }
                }
        )
        return liquiloansData
    }

}