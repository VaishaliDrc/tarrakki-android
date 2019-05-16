package com.tarrakki.api.model


import android.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel
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