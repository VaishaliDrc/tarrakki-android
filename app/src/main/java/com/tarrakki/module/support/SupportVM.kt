package com.tarrakki.module.support

import android.arch.lifecycle.MutableLiveData
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.SupportApis
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.SupportQueryListResponse
import com.tarrakki.api.model.parseTo
import com.tarrakki.api.model.printResponse
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import kotlin.concurrent.thread

class SupportVM : FragmentViewModel() {

    val supportQueryListResponse = MutableLiveData<SupportQueryListResponse>()
    val tickets = arrayListOf<Ticket>()

    init {
        tickets.add(Ticket("Others", "123456789", "October 10, 2018 - 10:15 AM", true))
        tickets.add(Ticket("My Question is not Listed here", "123456789", "October 10, 2018 - 10:15 AM", false))
        tickets.add(Ticket("What is an Auto Pay ?", "123456789", "October 10, 2018 - 10:15 AM", true))
    }

    fun getQueryList(): MutableLiveData<SupportQueryListResponse> {
        showProgress()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(SupportApis::class.java).getQueryList(),
                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val res = o.data?.parseTo<SupportQueryListResponse>()
                                supportQueryListResponse.postValue(res)
                            } else {
                                postError("${o.status?.message}")
                            }
                            dismissProgress()
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        dismissProgress()
                        throwable.postError()
                    }
                }
        )
        return supportQueryListResponse
    }

}

data class Ticket(val query: String, val referenceNo: String, val dateTime: String, var isOpen: Boolean)