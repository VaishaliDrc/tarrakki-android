package com.tarrakki.api.model


import com.google.gson.annotations.SerializedName
import com.tarrakki.R
import org.supportcompact.adapters.WidgetsViewModel

data class SupportViewTicketResponse(
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
                @SerializedName("question")
                val question: String?,
                @SerializedName("status")
                val status: String?,
                @SerializedName("ticket_ref")
                val ticketRef: String?,
                @SerializedName("time")
                val time: String?,
                @SerializedName("user_id")
                val userId: Int?
        ) : WidgetsViewModel {

            override fun layoutId(): Int {
                return R.layout.row_view_ticket_list_item
            }

            val open: Boolean
                get() = "open".equals(status, true)

        }
    }
}