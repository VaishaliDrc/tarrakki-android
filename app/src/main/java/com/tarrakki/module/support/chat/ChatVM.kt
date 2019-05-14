package com.tarrakki.module.support.chat

import android.net.Uri
import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class ChatVM : FragmentViewModel() {

    val chats = arrayListOf<ChatMessage>()
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val FILE_RQ_CODE = 111
    val cvPhotoName = "my_ticket_file"

    init {
        chats.add(ChatMessage(
                "My nach mandate registration is rejected.",
                "25-03-2019 04:54PM"))
        chats.add(ChatMessage(
                "Dear Customer, You can setup your AutoPay under the User Profile section. Select AutoPay under Payments section and follow This process:"
                        .plus("- Click on \"Add new AutoPay\"\n" +
                                "- Select the Maximum Transaction Limit for your Mandate - Provide Signature\n" +
                                " \n" +
                                "You can request for a physical mandate. Wherein, you can sign and upload on image of the mandate by \n" +
                                "Write your reply here"),
                "25-03-2019 04:54PM",
                false))
        chats.add(ChatMessage(
                "Can I do ISIP mandate registration is rejected.",
                "25-03-2019 04:54PM"))
    }

}

data class ChatMessage(
        val message: String,
        val dateTime: String,
        var isSender: Boolean = true,
        var imgUri: Uri? = null) : WidgetsViewModel {
    override fun layoutId(): Int {
        return if (isSender) if (imgUri == null) R.layout.row_sender_message else R.layout.row_sender_image else R.layout.row_receiver_message
    }
}