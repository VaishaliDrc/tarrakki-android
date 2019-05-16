package com.tarrakki.module.support.chat

import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.SupportApis
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import java.io.File
import kotlin.concurrent.thread

class ChatVM : FragmentViewModel() {

    val ticket = MutableLiveData<SupportViewTicketResponse.Data.Conversation>()
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val FILE_RQ_CODE = 111
    val cvPhotoName = "my_ticket_file"
    var sendFile: File? = null
    val chatData = MutableLiveData<SupportChatResponse>()

    fun getConversation(ticket: SupportViewTicketResponse.Data.Conversation): MutableLiveData<SupportChatResponse> {
        showProgress()
        val json = JsonObject()
        json.addProperty("ticket_ref", ticket.ticketRef)
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
                                chatData.postValue(res)
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
}