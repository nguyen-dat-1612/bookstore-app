package com.dat.bookstore_app.services

import android.util.Log
import com.dat.bookstore_app.presentation.features.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dat.bookstore_app.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val data = remoteMessage.data
        Log.d(TAG, "Message data payload: $data")

        if (data.isNotEmpty()) {
            val title = data["title"] ?: "Thông báo"
            val message = data["body"] ?: ""
            val orderId = data["orderId"]

            if (orderId != null) {
                showOrderDetailNotification(title, message, orderId)
            }
        }
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)

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
            .setSmallIcon(R.drawable.ic_circle_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(getColor(R.color.colorAccent))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Đảm bảo hiển thị ngay

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager, channelId)
        notificationManager.notify(orderId.hashCode(), notificationBuilder.build())
    }

    private fun showNotification(title: String, message: String) {

        val channelId = getString(R.string.default_notification_channel_id)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notification_type", "general")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_circle_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(getColor(R.color.red_light))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager, channelId)
        notificationManager.notify(GENERAL_NOTIFICATION_ID, notificationBuilder.build())

    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        channelId: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Thông báo đơn hàng",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Kênh thông báo cho đơn hàng và cập nhật"
                    enableLights(true)
                    lightColor = Color.GREEN
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 250, 250, 250)
                }
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Created notification channel: $channelId")
            }
        }
    }

    companion object {
        private const val TAG = "FCMService"
        private const val GENERAL_NOTIFICATION_ID = 1001
    }
}