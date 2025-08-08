package com.dat.bookstore_app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dat.bookstore_app.R
import com.dat.bookstore_app.presentation.features.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val type = data["type"]
        val title = data["title"] ?: "Thông báo"
        val message = data["body"] ?: ""
        val orderId = data["orderId"]

        if (type == "order_detail" && !orderId.isNullOrEmpty()) {
            showOrderDetailNotification(title, message, orderId)
        }
    }

    private fun showOrderDetailNotification(title: String, message: String, orderId: String) {
        val channelId = getString(R.string.default_notification_channel_id)

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("order_id", orderId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            orderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(getColor(R.color.red_light))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager, channelId)
        notificationManager.notify(orderId.hashCode(), notificationBuilder.build())
    }

    private fun createNotificationChannel(manager: NotificationManager, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            manager.getNotificationChannel(channelId) == null
        ) {
            val channel = NotificationChannel(
                channelId,
                "Thông báo đơn hàng",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo đơn hàng"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "app_notification_channel"
        const val NOTIFICATION_ID = 1001

        // Các key để truyền data
        const val EXTRA_FRAGMENT_TYPE = "fragment_type"
        const val FRAGMENT_HOME = "home"
        const val FRAGMENT_PROFILE = "profile"
        const val FRAGMENT_SETTINGS = "settings"
    }
}
