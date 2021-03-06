package com.ecommerce.shopmitt.notification

import android.content.ContentValues
import android.util.Log
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.utils.LogHelper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.greenrobot.eventbus.EventBus


class FCMHandler : FirebaseMessagingService() {

    var TOPICS = arrayOf("shopmitt")

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        // Get updated InstanceID token.
        // com.shopping.alis.utils.LogHelper.printInfoLog("onNewToken : $s")
        //sendTokenToServer(s);
        subscribeTopics()
    }

    private fun subscribeTopics() {
        val pubSub = FirebaseMessaging.getInstance()
        for (topic in TOPICS) {
            pubSub.subscribeToTopic(topic)
            Log.w(ContentValues.TAG, "subscribeTopic: $topic")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        LogHelper.instance.printDebugLog("onMessageReceived")
        LogHelper.instance.printDebugLog("FCM From: $remoteMessage.from")
//        com.shopping.alis.utils.LogHelper.instance.printDebugLog("FCM Notification Message Body: "+remoteMessage.notification?.body!!)
//        com.shopping.alis.utils.LogHelper.instance.printDebugLog("FCM Data Message Body: "+ remoteMessage.data.size)


        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]



        if (remoteMessage.data.isNotEmpty()) {
            if (!title.isNullOrEmpty()) {
                handleNotification(title, message, remoteMessage)
            }
        }

    }

    private fun handleNotification(title: String, message: String?, remoteMessage: RemoteMessage) {

        AppDataManager.instance.setNotificationCount()
        //AppDataHolder.getInstance().clearNotificationCache()
        EventBus.getDefault().post(NotificationEvent())

        NotificationController.instance.displayCommonNotification(this, remoteMessage)

    }
}