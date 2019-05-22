package com.tarrakki.api.model


import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.net.Uri
import android.view.View
import com.google.android.gms.common.util.IOUtils
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.SingleCallback1
import com.tarrakki.api.SupportApis
import com.tarrakki.api.subscribeToSingle
import com.tarrakki.getFileDownloadDir
import com.tarrakki.getMimeType
import okhttp3.ResponseBody
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.toast
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread


data class SupportChatResponse(
        @SerializedName("data")
        val `data`: Data?
) {
    data class Data(
            @SerializedName("conversation")
            val conversation: ArrayList<Conversation>?,
            @SerializedName("limit")
            val limit: Int?,
            @SerializedName("offset")
            var offset: Int?,
            @SerializedName("totalCount")
            val totalCount: Int?
    ) {
        data class Conversation(
                @SerializedName("file")
                val `file`: String?,
                @SerializedName("img")
                val img: String?,
                @SerializedName("is_admin_reply")
                val isAdminReply: Boolean?,
                @SerializedName("msg")
                val msg: String?,
                @SerializedName("time")
                val time: String?,
                @SerializedName("type")
                val type: String?
        ) : BaseObservable(), WidgetsViewModel {

            val fileName: String
                get() = File("$file").name


            var txtOpen: ObservableField<Int>? = null
            //get() = if (File(getFileDownloadDir(), fileName).exists()) R.string.open else R.string.download
            /*set(value) {
                field = value
                notifyPropertyChanged(BR.txtOpen)
            }*/

            var downloadProgressVisibility: ObservableField<Boolean>? = null
            /*set(value) {
                field = value
                notifyPropertyChanged(BR.downloadProgressVisibility)
            }*/

            val onOpen: View.OnClickListener
                get() = View.OnClickListener { v ->
                    if (txtOpen?.get() == R.string.open) {
                        v.context?.let { mContext ->
                            try {
                                val file = File(getFileDownloadDir(), fileName)
                                // Get URI and MIME type of file
                                val uri = Uri.fromFile(file)
                                val mime = mContext.contentResolver.getType(uri)
                                        ?: getMimeType(uri.toString())
                                // Open file with user selected app
                                val intent = Intent()
                                intent.action = Intent.ACTION_VIEW
                                intent.setDataAndType(uri, mime)
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                mContext.startActivity(Intent.createChooser(intent, "Open With"))
                            } catch (e: Exception) {
                                e.printStackTrace()
                                mContext.toast(e.message ?: "")
                            }
                        }
                    } else if (downloadProgressVisibility == null || downloadProgressVisibility?.get() == false) {
                        downloadProgressVisibility?.set(true)
                        subscribeToSingle(
                                ApiClient.getHeaderClient().create(SupportApis::class.java).download(ApiClient.IMAGE_BASE_URL.plus(file
                                        ?: "")),
                                object : SingleCallback1<ResponseBody> {
                                    override fun onSingleSuccess(o: ResponseBody) {
                                        thread {
                                            try {
                                                val file = File(getFileDownloadDir(), fileName)
                                                val fileOutputStream = FileOutputStream(file)
                                                IOUtils.copyStream(o.byteStream(), fileOutputStream, true, DEFAULT_BUFFER_SIZE)
                                                txtOpen?.set(R.string.open)
                                            } catch (ex: Exception) {
                                                ex.printStackTrace()
                                            } finally {
                                                downloadProgressVisibility?.set(false)
                                            }
                                        }
                                    }

                                    override fun onFailure(throwable: Throwable) {
                                        throwable.printStackTrace()
                                        throwable.postError()
                                        downloadProgressVisibility?.set(false)
                                    }
                                })
                    }
                }

            override fun layoutId(): Int {
                return if (isAdminReply == true)
                    when {
                        "message".equals("$type", true) -> R.layout.row_admin_message
                        "img".equals("$type", true) -> R.layout.row_admin_image
                        "file".equals("$type", true) -> R.layout.row_admin_file
                        else -> R.layout.row_admin_message
                    }
                else
                    when {
                        "message".equals("$type", true) -> R.layout.row_sender_message
                        "img".equals("$type", true) -> R.layout.row_sender_image
                        "file".equals("$type", true) -> R.layout.row_sender_file
                        else -> R.layout.row_sender_message
                    }
            }
        }
    }
}