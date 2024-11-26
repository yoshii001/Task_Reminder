package com.example.tasktest

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ViewTaskActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var noteTextView: TextView

    private var taskIndex: Int? = null
    private var taskList: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_task)

        titleTextView = findViewById(R.id.tvTitle)
        dateTextView = findViewById(R.id.tvReminderdate)
        timeTextView = findViewById(R.id.tvRemindertime)
        noteTextView = findViewById(R.id.tvnote)

        // Load existing tasks
        loadTasks()

        // Get the task index from the intent
        taskIndex = intent.getIntExtra("task_index", -1)

        // Display task data if available
        if (taskIndex != -1) {
            val task = taskList[taskIndex!!]
            titleTextView.text = task.title
            dateTextView.text = task.date
            timeTextView.text = task.time
            noteTextView.text = task.note
        }

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish() // Go back to the previous activity
        }
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList = Gson().fromJson(json, type)
        }
    }
}
