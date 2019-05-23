package com.tarrakki.module.support

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.view.View
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.SupportApis
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import kotlin.concurrent.thread

class SupportVM : FragmentViewModel() {

    val supportQueryListResponse = MutableLiveData<SupportQueryListResponse>()
    val allTicket = MutableLiveData<SupportViewTicketResponse>()
    val openTicket = MutableLiveData<SupportViewTicketResponse>()
    val closeTicket = MutableLiveData<SupportViewTicketResponse>()
    val tvNoDataFoundVisibility = ObservableField(View.GONE)

    fun getTicketsList(offset: Int = 0, state: String = "all"): MutableLiveData<SupportViewTicketResponse> {
        if (offset == 0) showProgress()
        val json = JsonObject()
        json.addProperty("limit", 5)
        json.addProperty("offset", offset)
        json.addProperty("state", state)
        json.addProperty("user_id", App.INSTANCE.getUserId())
        val data = json.toString().toEncrypt()
        json.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(SupportApis::class.java).getTickets(data),
                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val response = o.data?.parseTo<SupportViewTicketResponse>()
                                if (offset == 0) {
                                    allTicket.postValue(response)
                                } else {
                                    response?.data?.let { data ->
                                        data.conversation?.let {
                                            allTicket.value?.data?.conversation?.addAll(it)
                                        }
                                        data.offset?.let { allTicket.value?.data?.offset = it }
                                    }
                                    allTicket.postValue(allTicket.value)
                                }
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
        return allTicket
    }

    fun getOpenTicketsList(offset: Int = 0, state: String = "open"): MutableLiveData<SupportViewTicketResponse> {
        if (offset == 0) showProgress()
        val json = JsonObject()
        json.addProperty("limit", 5)
        json.addProperty("offset", offset)
        json.addProperty("state", state)
        json.addProperty("user_id", App.INSTANCE.getUserId())
        val data = json.toString().toEncrypt()
        json.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(SupportApis::class.java).getTickets(data),
                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val response = o.data?.parseTo<SupportViewTicketResponse>()
                                if (offset == 0) {
                                    openTicket.postValue(response)
                                } else {
                                    response?.data?.let { data ->
                                        data.conversation?.let {
                                            openTicket.value?.data?.conversation?.addAll(it)
                                        }
                                        data.offset?.let { openTicket.value?.data?.offset = it }
                                    }
                                    openTicket.postValue(openTicket.value)
                                }
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
        return openTicket
    }

    fun getClosedTicketsList(offset: Int = 0, state: String = "closed"): MutableLiveData<SupportViewTicketResponse> {
        if (offset == 0) showProgress()
        val json = JsonObject()
        json.addProperty("limit", 5)
        json.addProperty("offset", offset)
        json.addProperty("state", state)
        json.addProperty("user_id", App.INSTANCE.getUserId())
        val data = json.toString().toEncrypt()
        json.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(SupportApis::class.java).getTickets(data),
                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val response = o.data?.parseTo<SupportViewTicketResponse>()
                                if (offset == 0) {
                                    closeTicket.postValue(response)
                                } else {
                                    response?.data?.let { data ->
                                        data.conversation?.let {
                                            closeTicket.value?.data?.conversation?.addAll(it)
                                        }
                                        data.offset?.let { closeTicket.value?.data?.offset = it }
                                    }
                                    closeTicket.postValue(closeTicket.value)
                                }
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
        return closeTicket
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
