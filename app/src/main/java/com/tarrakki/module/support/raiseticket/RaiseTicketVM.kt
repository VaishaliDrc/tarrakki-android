package com.tarrakki.module.support.raiseticket

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.net.Uri
import android.view.View
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.SupportApis
import com.tarrakki.api.model.*
import com.tarrakki.api.subscribeToSingle
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.supportcompact.FragmentViewModel
import org.supportcompact.ktx.*
import java.io.File
import kotlin.concurrent.thread

class RaiseTicketVM : FragmentViewModel() {

    val query = MutableLiveData<SupportQueryListResponse.Data>()

    val transactionVisibility = ObservableField(View.GONE)
    val transaction = ObservableField("")
    val description = ObservableField("")
    val imgName = ObservableField("")
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val cvPhotoName = "profilePick"
    var issueImage: Uri? = null

    fun checkTransactionStatus(query: SupportQueryListResponse.Data) {
        showProgress()
        val json = JsonObject()
        json.addProperty("query_id", query.id)
        json.addProperty("sub_query_id", query.subqueryId)
        val data = json.toString().toEncrypt()
        json.printRequest()
        subscribeToSingle(
                observable = ApiClient.getHeaderClient().create(SupportApis::class.java).checkTransactionStatus(data),
                singleCallback = object : SingleCallback1<ApiResponse> {
                    override fun onSingleSuccess(o: ApiResponse) {
                        thread {
                            o.printResponse()
                            if (o.status?.code == 1) {
                                val isAllowTransaction: Boolean? = JSONObject(o.data?.toDecrypt()).optJSONObject("data")?.optBoolean("allow_transaction")
                                transactionVisibility.set(if (isAllowTransaction == true) View.VISIBLE else View.GONE)
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
    }

    fun submitTicket(query: SupportQueryListResponse.Data): MutableLiveData<ApiResponse> {
        showProgress()
        var fileData: MultipartBody.Part? = null
        val userId = App.INSTANCE.getUserId()
        issueImage?.let {
            val file = File(getPath(it))
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            fileData = MultipartBody.Part.createFormData("issue_image", file.name, requestFile)
        }
        val json = JsonObject()
        json.addProperty("query_id", query.id)
        json.addProperty("issue_description", description.get())
        json.addProperty("sub_query_id", query.subqueryId)
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