package com.tarrakki.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.SupportViewTicketResponse
import com.tarrakki.closeTicketApi
import com.tarrakki.module.account.AccountActivity
import com.tarrakki.module.home.HomeActivity
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.events.Event
import org.supportcompact.ktx.*

const val ACTION_CLOSE_TICKET = "com.tarrakki.ACTION_CLOSE_TICKET"
const val ACTION_CANCEL_CLOSE_TICKET = "com.tarrakki.ACTION_CANCEL_CLOSE_TICKET"
const val IS_FROM_NOTIFICATION = "is_from_notifications"
const val IS_BANK_ACCOUNT = "is_bank_account"
const val IS_VIDEO_KYC = "is_video_kyc"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        if (data.contains("data") && getUserId()?.isNotBlank() == true) {
            data["data"]?.let {
                val messageBody = JSONObject(it)
                if (("Support Ticket".equals(messageBody.optString("type"), true) || "Close Ticket".equals(messageBody.optString("type"), true)) && App.INSTANCE.openChat?.second == messageBody.optString("reference")) {
                    val tiket = SupportViewTicketResponse.Data.Conversation(
                            null,
                            "open",
                            messageBody.optString("reference"),
                            null,
                            null
                    )
                    EventBus.getDefault().postSticky(tiket)
                } else if ("payment_success".equals(messageBody.optString("type"), true)) {
                    EventBus.getDefault().post(Event.ON_PAYMENT_REDIRECTED)
                } else {
                    sendNotification(messageBody)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        e("Toke=>$token")
        token?.let { setPushToken(it) }
    }

    private fun fireCloseTicketNotification(messageBody: JSONObject) {

        val intentCloseTicket = Intent(ACTION_CLOSE_TICKET)
        intentCloseTicket.putExtra("ticketId", messageBody.optString("reference"))
        val pendingIntentCloseTicket = PendingIntent.getBroadcast(this, 0 /* Request code */, intentCloseTicket, PendingIntent.FLAG_ONE_SHOT)

        val intentCancelCloseTicket = Intent(ACTION_CANCEL_CLOSE_TICKET)
        val pendingIntentCancelCloseTicket = PendingIntent.getBroadcast(this, 0 /* Request code */, intentCancelCloseTicket, PendingIntent.FLAG_ONE_SHOT)

        var channelId = getString(R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel()
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(messageBody.optString("title"))
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody.optString("detail")))
                .setSound(defaultSoundUri)
                .addAction(0, getString(R.string.close), pendingIntentCloseTicket)
                .addAction(0, getString(R.string.cancel), pendingIntentCancelCloseTicket)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())
    }

    class TicketNotification : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_CLOSE_TICKET) {
                intent.getStringExtra("ticketId")?.let {
                    closeTicketApi(it)
                }
            }
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.cancel(1)
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: JSONObject) {

        val intent: Intent = if ("Support Ticket".equals(messageBody.optString("type"), true)
                || "Close Ticket".equals(messageBody.optString("type"), true)
                || "video_kyc".equals(messageBody.optString("type"), true)
                || "bank_account".equals(messageBody.optString("type"), true)) {
            Intent(this, AccountActivity::class.java).apply {
                putExtra("reference", messageBody.optString("reference"))
                if ("bank_account".equals(messageBody.optString("type"), true)) {
                    putExtra(IS_BANK_ACCOUNT, true)
                    setReadyToInvest(messageBody.optBoolean("ready_to_invest"))
                    EventBus.getDefault().post(Event.REFRESH)
                } else if ("video_kyc".equals(messageBody.optString("type"), true)) {
                    setKYCStatus(messageBody.optString("kyc_status"))
                    //setRemainingFields(messageBody.optString("is_remaining_fields"))
                    putExtra(IS_VIDEO_KYC, true)
                }
                putExtra(IS_FROM_NOTIFICATION, true)
            }
        } else {
            Intent(this, HomeActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)
        var channelId = getString(R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel()
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(messageBody.optString("title"))
                .setContentText(messageBody.optString("detail"))
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody.optString("detail")))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = getString(R.string.app_name)
        val channelName = "Sun Keeper Push"
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

}