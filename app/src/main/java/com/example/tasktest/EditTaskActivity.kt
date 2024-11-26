package com.example.tasktest

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class EditTaskActivity : AppCompatActivity() {

    private lateinit var title: EditText
    private lateinit var note: EditText
    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var clear: Button

    private var taskIndex: Int? = null
    private var taskList: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_task)

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish() // Go back to the previous activity
        }

        title = findViewById(R.id.etTitle)
        note = findViewById(R.id.note)
        date = findViewById(R.id.etReminderDate)
        time = findViewById(R.id.etReminderTime)
        val saveButton: Button = findViewById(R.id.btnSave)
        val clearButton: Button = findViewById(R.id.btnclear) // Initialize Clear button

        // Load existing tasks
        loadTasks()

        // Get the task index from the intent
        taskIndex = intent.getIntExtra("task_index", -1)

        // Set listeners for date and time pickers
        date.setOnClickListener { showDatePicker() }
        time.setOnClickListener { showTimePicker() }

        // Display task data if available
        if (taskIndex != -1) {
            val task = taskList[taskIndex!!]
            date.setText(task.date)
            note.setText(task.note)
            time.setText(task.time)
            title.setText(task.title)
        }

        // Save button listener
        saveButton.setOnClickListener {
            if (taskIndex != -1) {
                val updatedTask = Task(
                    title.text.toString(),
                    date.text.toString(),
                    time.text.toString(),
                    note.text.toString()
                )
                taskList[taskIndex!!] = updatedTask // Update the task
                saveTasks()
                finish() // Close the activity and return to Home_Page
            }
        }

        // Clear button listener
        clearButton.setOnClickListener { clearFields() }
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList = Gson().fromJson(json, type)
        }
    }

    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(taskList)
        editor.putString("tasks", json)
        editor.apply()
        Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedMonth = (selectedMonth + 1).toString().padStart(2, '0')
            val formattedDay = selectedDay.toString().padStart(2, '0')
            date.setText("$selectedYear-$formattedMonth-$formattedDay")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedHour = selectedHour.toString().padStart(2, '0')
            val formattedMinute = selectedMinute.toString().padStart(2, '0')
            time.setText("$formattedHour:$formattedMinute")
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun clearFields() {
        title.text.clear()
        note.text.clear()
        date.text.clear()
        time.text.clear()
        Toast.makeText(this, "Fields cleared", Toast.LENGTH_SHORT).show()
    }
}
