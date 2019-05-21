package com.tarrakki.module.support.chat

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.SupportApis
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import java.io.File
import kotlin.concurrent.thread

class ChatVM : FragmentViewModel() {

    val ticket = MutableLiveData<SupportViewTicketResponse.Data.Conversation>()
    val reference = ObservableField("")
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val FILE_RQ_CODE = 111
    val cvPhotoName = "my_ticket_file"
    var sendFile: Pair<Int, File>? = null
    val chatData = MutableLiveData<SupportChatResponse>()

    fun getConversation(ticket: SupportViewTicketResponse.Data.Conversation, offset: Int = 0, showProcess: Boolean = true): MutableLiveData<SupportChatResponse> {
        if (offset == 0 && showProcess) showProgress()
        val json = JsonObject()
        json.addProperty("ticket_ref", ticket.ticketRef)
        json.addProperty("limit", 10)
        json.addProperty("offset", offset)
        val data = json.toString().toEncrypt()
        json.printRequest()
        subscribeToSingle(
                ApiClient.getHeaderClient().create(SupportApis::class.java).getConversation(data),
                object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            if (o.status?.code == 1) {
                                o.printResponse()
                                val res = o.data?.parseTo<SupportChatResponse>()
                                if (offset == 0) {
                                    res?.data?.conversation?.forEach {
                                        it.downloadProgressVisibility = false
                                        it.txtOpen = R.string.open
                                    }
                                    chatData.postValue(res)
                                } else {
                                    res?.data?.conversation?.let {
                                        chatData.value?.data?.conversation?.addAll(it)
                                    }
                                    chatData.postValue(chatData.value)
                                }
                            } else {
                                postError("${o.status?.message}")
                            }
                            dismissProgress()
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        throwable.postError()
                        dismissProgress()
                    }
                }
        )
        return chatData
    }

    fun sendData(message: String? = null): MutableLiveData<ApiResponse> {
        showProgress()
        var fileData: MultipartBody.Part? = null
        val userId = App.INSTANCE.getUserId()
        sendFile?.let {
            fileData = if (it.first == 0) {
                val requestFile = RequestBody.create(MediaType.parse("image/*"), it.second)
                MultipartBody.Part.createFormData("issue_image", it.second.name, requestFile)
            } else {
                val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), it.second)
                MultipartBody.Part.createFormData("ticket_file", it.second.name, requestFile)
            }
        }
        val json = JsonObject()
        ticket.value?.let {
            json.addProperty("ticket_ref", it.ticketRef)
        }
        //json.addProperty("query_id", query.id)
        json.addProperty("issue_description", message)
        //json.addProperty("sub_query_id", query.subqueryId)
        val data = json.toString().toEncrypt()
        json.printRequest()
        val dataRequest = RequestBody.create(MediaType.parse("text/plain"), data)
        val apiResponse = MutableLiveData<ApiResponse>()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(SupportApis::class.java).createTicket(userId, dataRequest, fileData),
                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                apiResponse.postValue(o)
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
        return apiResponse
    }

}