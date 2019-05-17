package com.tarrakki.api.model


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.net.Uri
import android.view.View
import com.google.gson.annotations.SerializedName
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.getFileDownloadDir
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.ktx.toast
import java.io.File

data class SupportChatResponse(
        @SerializedName("data")
        val `data`: Data?
) {
    data class Data(
            @SerializedName("conversation")
            val conversation: ArrayList<Conversation>?
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

            @get:Bindable
            var txtOpen: Int = R.string.open
                get() = if (File(getFileDownloadDir(), fileName).exists()) R.string.open else R.string.download
                set(value) {
                    field = value
                    notifyPropertyChanged(BR.txtOpen)
                }

            var downloadReference = -1L

            val onOpen: View.OnClickListener
                get() = View.OnClickListener { v ->
                    if (txtOpen == R.string.open) {
                        v.context?.let { mContext ->
                            try {
                                val file = File(getFileDownloadDir(), fileName)
                                // Get URI and MIME type of file
                                val uri = Uri.fromFile(file)
                                val mime = mContext.contentResolver.getType(uri)
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
                    } else {
                        val downloadManager = App.INSTANCE.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val download_Uri = Uri.parse(ApiClient.IMAGE_BASE_URL.plus(file ?: ""))
                        val request = DownloadManager.Request(download_Uri)
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                        request.setAllowedOverRoaming(false)
                        request.setTitle("Tarrakki Downloading " + download_Uri.lastPathSegment)
                        request.setDescription("Downloading " + download_Uri.lastPathSegment)
                        request.setVisibleInDownloadsUi(true)
                        request.setDestinationInExternalPublicDir("/${App.INSTANCE.getString(R.string.app_name)}/Download", download_Uri.lastPathSegment)
                        downloadReference = downloadManager.enqueue(request)
                    }
                }

            override fun layoutId(): Int {
                return if (isAdminReply == true)
                    when {
                        "message".equals("$type", true) -> R.layout.row_admin_message
                        "img".equals("$type", true) -> R.layout.row_admin_image
                        "file".equals("$type", true) -> R.layout.row_admin_message
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