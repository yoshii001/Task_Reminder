package com.example.tasktest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.tasktest.databinding.ActivityMainBinding // Ensure this matches your layout file name

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // View binding instance

    private val progressHandler = Handler()
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflate the binding
        setContentView(binding.root) // Set the content view to the binding root

        // Simulate loading progress
        Thread {
            while (progressStatus < 100) {
                progressStatus += 10 // Increase progress by 10
                progressHandler.post {
                    binding.progressBar.progress = progressStatus // Update progress bar
                }
                Thread.sleep(500) // Simulate time delay for loading
            }
            // Navigate to another activity after loading completes
            progressHandler.post {
                val intent = Intent(this, TaskListActivity::class.java) // Change to your next activity
                startActivity(intent)
                finish() // Close the MainActivity
            }
        }.start()
    }
}
