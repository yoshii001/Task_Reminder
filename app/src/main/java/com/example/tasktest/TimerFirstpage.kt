package com.example.tasktest

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// Notification constants
const val notificationID = 1
const val channelID = "timer_channel"
const val channelName = "Timer Notifications"
const val notificationPermissionRequestCode = 101

class TimerFirstpage : AppCompatActivity() {

    private lateinit var npHours: NumberPicker
    private lateinit var npMinutes: NumberPicker
    private lateinit var npSeconds: NumberPicker
    private lateinit var btnStartTimer: Button
    private var countDownTimer: CountDownTimer? = null

    private lateinit var icon_task: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_firstpage)

        icon_task = findViewById(R.id.icon_task)

        // Initialize NumberPickers and Button
        npHours = findViewById(R.id.npHours)
        npMinutes = findViewById(R.id.npMinutes)
        npSeconds = findViewById(R.id.npSeconds)
        btnStartTimer = findViewById(R.id.btnStartTimer)

        // Set min and max values for hours, minutes, and seconds
        npHours.minValue = 0
        npHours.maxValue = 23
        npMinutes.minValue = 0
        npMinutes.maxValue = 59
        npSeconds.minValue = 0
        npSeconds.maxValue = 59

        // Formatter to show values with two digits
        val formatter = NumberPicker.Formatter { i -> String.format("%02d", i) }
        npHours.setFormatter(formatter)
        npMinutes.setFormatter(formatter)
        npSeconds.setFormatter(formatter)

        // Create notification channel
        createNotificationChannel()

        // Check and request notification permission for Android 13+
        checkNotificationPermission()

        // Start the timer when the button is clicked
        btnStartTimer.setOnClickListener {
            val hours = npHours.value
            val minutes = npMinutes.value
            val seconds = npSeconds.value
            val totalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L

            // Navigate to TimerSecondpage with the countdown duration
            val intent = Intent(this, TimerSecondpage::class.java).apply {
                putExtra("DURATION", totalMillis) // Pass the totalMillis to the next activity
            }
            startActivity(intent)
        }

        // Task icon click to navigate to task list
        icon_task.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java) // Change to your Task List Activity
            startActivity(intent)
        }
    }

    // Create a notification channel for timer notifications
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Check for notification permission
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    notificationPermissionRequestCode
                )
            }
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == notificationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}
