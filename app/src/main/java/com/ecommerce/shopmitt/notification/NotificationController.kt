package com.ecommerce.shopmitt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.RemoteMessage
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.utils.LogHelper
import com.ecommerce.shopmitt.views.activities.SplashActivity


class NotificationController private constructor() {

    private var notificationCount: Int = 0

    fun displayCommonNotification(context: Context, remoteMessage: RemoteMessage) {

        val title: String?
        val message: String?
        var imageUri: Uri? = null

        when {
            remoteMessage.data.isNotEmpty() -> {
                LogHelper.instance.printDebugLog("Message Data present ")
                title = remoteMessage.data["title"]
                message = remoteMessage.data["message"]
                if (remoteMessage.data.containsKey("image"))
                    imageUri = Uri.parse(remoteMessage.data["image"])
            }
            remoteMessage.notification != null -> {
                LogHelper.instance.printDebugLog("Message Notification payload present ")
                title = remoteMessage.notification!!.title
                message = remoteMessage.notification!!.body
                imageUri = remoteMessage.notification!!.imageUrl
            }
            else -> return
        }

        val resultIntent = Intent(context, SplashActivity::class.java)
        val resultPendingIntent: PendingIntent? =
                TaskStackBuilder.create(context).run {
                    addNextIntentWithParentStack(resultIntent)
                    getPendingIntent(notificationCount++, PendingIntent.FLAG_UPDATE_CURRENT)
                }

        createNotificationChannelIfNeeded(context)

        val builder = NotificationCompat.Builder(context, N_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

        if (imageUri != null) {

            try {
                val futureTarget = Glide.with(context)
                        .asBitmap().load(imageUri).submit()
                val bitmap = futureTarget.get()
                builder.setLargeIcon(bitmap)
                builder.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null))
                Glide.with(context).clear(futureTarget)
            } catch (e: Exception) {
                e.printStackTrace()
            }


        } else {
            builder.setStyle(NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(message))
        }
        with(NotificationManagerCompat.from(context)) {
            notify(notificationCount++, builder.build())
        }

    }

    private fun createNotificationChannelIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                    NotificationChannel(N_CHANNEL_ID, N_CHANNEL_NAME, importance).apply {
                        description = N_CHANNEL_DESC
                    }

            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Clears all notification tray messages
    fun clearAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    // Clears all notification tray messages
    fun clearNotifications(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    private object HOLDER {
        val INSTANCE = NotificationController()
    }

    companion object {

        //val instance: com.shopping.alis.notification.NotificationController = HOLDER.INSTANCE
        val instance: NotificationController by lazy { HOLDER.INSTANCE }

        private const val N_CHANNEL_ID = "Alis_channel_ID"
        private const val N_CHANNEL_NAME = "Alis Notifications"
        private const val N_CHANNEL_DESC = "Alis General Notifications"

    }


}