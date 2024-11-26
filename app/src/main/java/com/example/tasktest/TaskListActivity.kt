package com.example.tasktest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.tasktest.AddTaskActivity
import com.example.tasktest.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private lateinit var icon_timer: ImageButton

class TaskListActivity : AppCompatActivity() {

    private lateinit var tContainer: LinearLayout
    private var taskList: MutableList<Task> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        icon_timer=findViewById(R.id.icon_timer)

        tContainer = findViewById(R.id.tContainer)
        val addTaskButton: ImageButton = findViewById(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        icon_timer.setOnClickListener {
            val intent = Intent(this, TimerFirstpage::class.java) // Change to your Timer Activity
            startActivity(intent)
        }

        loadTasks()
        displayTasks()
    }
    //icon_timer intent

    //icon_task intent


    override fun onResume() {
        super.onResume()
        loadTasks() // Reload tasks when returning to this activity
        displayTasks()
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList = Gson().fromJson(json, type)
        }
    }

    private fun displayTasks() {
        tContainer.removeAllViews()
        for (task in taskList) {
            val taskView = LayoutInflater.from(this).inflate(R.layout.task_item, tContainer, false)
            val titleTextView: TextView = taskView.findViewById(R.id.taskTitle)
            val dateTextView: TextView = taskView.findViewById(R.id.taskDate)
            val timeTextView: TextView = taskView.findViewById(R.id.taskTime)
            val editButton: ImageButton = taskView.findViewById(R.id.btnEdit)
            val deleteButton: ImageButton = taskView.findViewById(R.id.btnDelete)

            titleTextView.text = task.title
            dateTextView.text = "Date: ${task.date}"
            timeTextView.text = "Time: ${task.time}"

            // Set click listener on the entire taskView to navigate to ViewTaskActivity
            taskView.setOnClickListener {
                val intent = Intent(this, ViewTaskActivity::class.java)
                intent.putExtra("task_index", taskList.indexOf(task)) // Pass the index of the task
                startActivity(intent)
            }

            editButton.setOnClickListener {
                val intent = Intent(this, EditTaskActivity::class.java)
                intent.putExtra("task_index", taskList.indexOf(task)) // Pass the index of the task
                startActivity(intent)
            }

            // Set click listener on the delete button
            deleteButton.setOnClickListener {
                // Show confirmation dialog
                AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes") { _, _ ->
                        taskList.remove(task)
                        saveTasks()
                        displayTasks()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            // Add the taskView to the container
            tContainer.addView(taskView)
        }
    }


    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(taskList)
        editor.putString("tasks", json)
        editor.apply()

    }

}