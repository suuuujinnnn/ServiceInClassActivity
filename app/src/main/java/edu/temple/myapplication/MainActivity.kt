package edu.temple.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.startButton).setOnClickListener {

        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_start -> {}
            R.id.action_stop -> {}
            else -> return false
        }
        return true
    }

    private fun saveTimerState(remainingTime: Int, isPaused: Boolean) {
        getSharedPreferences("TimerPrefs", MODE_PRIVATE).edit().apply {
            putInt("remainingTime", remainingTime)
            putBoolean("isPaused", isPaused)
            apply()
        }
    }

    private fun loadTimerState(): Pair<Int, Boolean> {
        val prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE)
        return Pair(
            prefs.getInt("remainingTime", 100), // Default 100 seconds
            prefs.getBoolean("isPaused", false)
        )
    }

    private fun clearTimerState() {
        getSharedPreferences("TimerPrefs", MODE_PRIVATE).edit().apply {
            remove("remainingTime")
            remove("isPaused")
            apply()
        }
    }
}