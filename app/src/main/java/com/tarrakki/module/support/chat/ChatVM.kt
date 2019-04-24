package com.tarrakki.module.support.chat

import com.tarrakki.R
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class ChatVM : FragmentViewModel() {

    val chats = arrayListOf<ChatMessage>()

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
    }

}

data class ChatMessage(val message: String, val dateTime: String, var isSender: Boolean = true) : WidgetsViewModel {
    override fun layoutId(): Int {
        return if (isSender) R.layout.row_sender_message else R.layout.row_receiver_message
    }
}