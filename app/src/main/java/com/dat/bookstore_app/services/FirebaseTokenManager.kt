package com.dat.bookstore_app.services

import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

object FirebaseTokenManager {
    fun fetchToken(onTokenReady: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    onTokenReady(null)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d("FCM", "FCM Token: $token")
                onTokenReady(token)
            }
    }
}