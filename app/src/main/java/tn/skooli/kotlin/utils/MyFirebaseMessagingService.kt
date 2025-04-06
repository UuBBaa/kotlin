package tn.skooli.kotlin.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tn.skooli.kotlin.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        // Send this token to your Spring Boot backend
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message received: ${remoteMessage.notification}")

        // Extract title and body from the notification payload (if available)
        val title = remoteMessage.notification?.title ?: "Notification"
        val messageBody = remoteMessage.notification?.body ?: "You have a new message."

        // Display the notification
        sendNotification(title, messageBody)
    }

    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "default_channel_id"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Build the notification using NotificationCompat
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_notification_overlay) // Replace with your own icon
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
        // You can add pending intents here if you want the user to open an activity

        // Get the NotificationManager service
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel if necessary (Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Default Channel"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification with a unique ID (you can use 0 if only one notification is expected)
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendRegistrationToServer(token: String) {
        // Example using Retrofit: create an API call to send the token to your backend
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Suppose you add a new endpoint in your ApiService: registerToken
                val response = RetrofitInstance.createApi(applicationContext as Application)
                    .registerToken(mapOf("token" to token))
                if (response.isSuccessful) {
                    Log.d("FCM", "Token registered successfully")
                } else {
                    Log.e("FCM", "Token registration failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error sending token", e)
            }
        }
    }
}
