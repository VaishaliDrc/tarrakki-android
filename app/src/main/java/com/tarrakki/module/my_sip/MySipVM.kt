package com.tarrakki.module.my_sip

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.*
import com.tarrakki.api.model.*
import com.tarrakki.getFileDownloadDir
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.showProgress
import java.io.File
import kotlin.concurrent.thread

class MySipVM : FragmentViewModel() {

    var mySipResponse = MutableLiveData<MySipApiResponse>()
    val onBack = MutableLiveData<Boolean>()
    val onRefresh = MutableLiveData<Boolean>()

    fun getMySipRecords(offset: Int = 0, mRefresh: Boolean = false): MutableLiveData<MySipApiResponse> {
        if (offset == 0 && !mRefresh)
            showProgress()
        val json = JsonObject()
        json.addProperty("limit", 10)
        json.addProperty("offset", offset)
        json.printRequest()
        val data = json.toString().toEncrypt()

        subscribeToSingle(
                ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getMySip(App.INSTANCE.getUserId(), data),
                object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            if (o.status?.code == 1) {
                                o.printResponse()
                                val res = o.data?.parseTo<MySipApiResponse>()
                                if (offset == 0) {
                                    mySipResponse.postValue(res)
                                } else {
                                    res?.mySipData?.let {
                                        mySipResponse.value?.mySipData?.addAll(it)
                                    }
                                    mySipResponse.postValue(mySipResponse.value)
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
        return mySipResponse
    }
}