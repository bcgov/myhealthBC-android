package ca.bc.gov.bchealth

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import ca.bc.gov.common.R
import ca.bc.gov.repository.utils.NotificationHelper
import com.google.firebase.messaging.RemoteMessage
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener

class CustomNotificationListener : NotificationListener {
    override fun onPushNotificationReceived(context: Context?, message: RemoteMessage?) {

        context ?: return

        /* The following notification properties are available. */
        val notification = message?.notification
        val title = notification?.title.orEmpty()
        val body = notification?.body.orEmpty()
        val data = message?.data

        if (message != null) {
            Log.d("svn", "Message Notification Title: $title")
            Log.d("svn", "Message Notification Body: $body")

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
             val pi =  PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            NotificationHelper(context, pi).showNotification(title)

        }

        data?.entries?.forEach { entry ->
            Log.d("svn", "key, " + entry.key + " value " + entry.value)
        }
    }
}