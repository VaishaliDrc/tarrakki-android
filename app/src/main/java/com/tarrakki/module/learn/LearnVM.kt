package com.tarrakki.module.learn

import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import com.tarrakki.module.transactions.LoadMore
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import kotlin.concurrent.thread

class LearnVM : FragmentViewModel() {

    val blogResponse = MutableLiveData<BlogResponse>()
    val loadMore = LoadMore()
    val blog = MutableLiveData<Blog>()

    fun getBlogs(offset: Int = 0, isRefresh: Boolean = false): MutableLiveData<BlogResponse> {
        if (offset == 0 && !isRefresh)
            showProgress()
        val json = JsonObject()
        json.addProperty("offset", offset)
        json.addProperty("limit", 5)
        val data = json.toString().toEncrypt()
        data.printRequest()
        json.printRequest()
        subscribeToSingle(ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getBlogs(data),
                object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            if (o.status?.code == 1) {
                                o.printResponse()
                                val response = o.data?.parseTo<BlogResponse>()
                                if (offset == 0) {
                                    blogResponse.postValue(response)
                                } else if (response?.data != null) {
                                    response.data.blogs?.let {
                                        blogResponse.value?.data?.blogs?.addAll(it)
                                    }
                                    blogResponse.value?.data?.total = response.data.total
                                    blogResponse.value?.data?.offset = response.data.offset
                                    val newData = blogResponse.value
                                    blogResponse.postValue(newData)
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
        return blogResponse
    }

}