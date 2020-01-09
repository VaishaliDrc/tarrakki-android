package com.tarrakki.module.support

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
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
import kotlin.concurrent.thread

class QuestionsVM : FragmentViewModel() {

    val query = MutableLiveData<SupportQueryListResponse.Data>()
    val questionListResponse = MutableLiveData<SupportQuestionListResponse>()
    val question = ObservableField<SupportQuestionListResponse.Question>()
    val queryTitle = ObservableField<String>()


    fun getQuestionList(): MutableLiveData<SupportQuestionListResponse> {
        showProgress()
        val json = JsonObject()
        json.addProperty("query_id", query.value?.id)
        json.addProperty("sub_query_id", query.value?.subqueryId)
        val data = json.toString().toEncrypt()
        json.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(SupportApis::class.java).getQuestionList(data),
                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val response = o.data?.parseTo<SupportQuestionListResponse>()
                                questionListResponse.postValue(response)
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
        return questionListResponse
    }

}