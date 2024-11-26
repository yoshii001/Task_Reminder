package com.example.tasktest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("taskTitle") ?: "Task Reminder"
        val note = intent.getStringExtra("taskNote") ?: "You have a task due!"

        sendNotification(context, title, note)
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val channelId = "task_reminders"

        // Check for permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED) {

            // Create a notification channel for Android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Task Reminders", NotificationManager.IMPORTANCE_HIGH)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // Use NotificationCompat.Builder for compatibility with lower API levels
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.reminder) // Replace with your notification icon
                .setAutoCancel(true)

            // Show the notification
            with(NotificationManagerCompat.from(context)) {
                notify(0, notificationBuilder.build())
            }
        } else {
            // Handle the case where permission is not granted
            // You can show a toast or log a message
            // For example, show a log message
            Log.e("AlarmReceiver", "Notification permission not granted")
            // Optionally, notify the user or handle the situation
        }
    }
}
