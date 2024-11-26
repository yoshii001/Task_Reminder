package com.example.tasktest

import android.Manifest
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
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimerSecondpage : AppCompatActivity() {

    private lateinit var tvTimeLeft: TextView
    private lateinit var tvCurrentTime: TextView // Add reference for tvCurrentTime
    private lateinit var btnLap: Button
    private lateinit var btnStartStopReset: Button
    private lateinit var pbTimer: ProgressBar
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var timerRunning: Boolean = false
    private var totalDuration: Long = 0  // Store total duration

    private lateinit var icon_task: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timer_secondpage)

        icon_task = findViewById(R.id.icon_task)

        // Initialize UI elements
        tvTimeLeft = findViewById(R.id.tvTimeLeft)
        tvCurrentTime = findViewById(R.id.tvCurrentTime) // Initialize tvCurrentTime
        btnLap = findViewById(R.id.btnLap)
        btnStartStopReset = findViewById(R.id.btnStartStopReset)
        pbTimer = findViewById(R.id.pbTimer)

        // Get current time and set it to tvCurrentTime
        updateCurrentTime()

        // Get the duration passed from MainActivity
        totalDuration = intent.getLongExtra("DURATION", 0)
        timeLeftInMillis = totalDuration // Set initial time left

        // Set the max progress to the total duration in seconds
        pbTimer.max = (totalDuration / 1000).toInt()

        // Start the countdown timer
        startTimer()

        // Button click listeners
        btnStartStopReset.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
            } else {
                resumeTimer()
            }
        }

        btnLap.setOnClickListener {
            // Clear the timer and navigate back to MainActivity
            resetTimer()
            navigateToMainActivity()
        }

        // Task icon click to navigate to task list
        icon_task.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java) // Change to your Task List Activity
            startActivity(intent)
        }
    }

    // Function to display the current time
    private fun updateCurrentTime() {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format for current time
        val currentTime = sdf.format(calendar.time)
        tvCurrentTime.text = currentTime // Set current time to tvCurrentTime
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()

                // Update ProgressBar to reflect the time left
                pbTimer.progress = (timeLeftInMillis / 1000).toInt() // Update progress
            }

            override fun onFinish() {
                timerRunning = false
                tvTimeLeft.text = "00:00:00"
                pbTimer.progress = 0 // Reset ProgressBar to 0 when timer finishes
                sendNotification()
                vibratePhone()
                playSound()

                navigateToMainActivity()
            }
        }.start()
        timerRunning = true
        btnStartStopReset.text = "Pause"
        btnStartStopReset.setBackgroundColor(resources.getColor(R.color.red))
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
        btnStartStopReset.text = "Resume"
        btnStartStopReset.setBackgroundColor(resources.getColor(R.color.purple_500))
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timerRunning = false
        timeLeftInMillis = totalDuration // Reset to original duration
        updateTimer()
        pbTimer.progress = pbTimer.max // Reset ProgressBar to max value
        btnStartStopReset.text = "Start"
        btnStartStopReset.setBackgroundColor(resources.getColor(R.color.purple_500))
    }

    private fun updateTimer() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        tvTimeLeft.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Function to display the notification
    private fun sendNotification() {
        val notificationManager = NotificationManagerCompat.from(this)

        // Intent for when the notification is clicked
        val intent = Intent(this, TimerFirstpage::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Timer")
            .setContentText("Your timer has ended.")
            .setSmallIcon(R.drawable.reminder)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Check for notification permission (if necessary)
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationID, notification)
        }
    }

    // Vibrate the phone when the timer finishes
    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }

    // Play a sound when the timer finishes
    private fun playSound() {
        val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(applicationContext, notificationSound)
        ringtone.play()
    }

    // Function to navigate back to MainActivity
    private fun navigateToMainActivity() {
        val intent = Intent(this, TimerFirstpage::class.java)
        startActivity(intent)
        finish() // Optional: Call finish() if you want to remove this activity from the back stack
    }
}
