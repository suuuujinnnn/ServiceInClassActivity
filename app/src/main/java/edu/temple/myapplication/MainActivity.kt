package edu.temple.myapplication

import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var serviceBound = false
    private var serviceBinder: TimerService.TimerBinder? = null

    // Handler to receive timer updates from Service
    private val timerHandler = Handler(Looper.getMainLooper()) { msg ->
        val value = msg.what
        findViewById<TextView>(R.id.textView).text = value.toString()
        if (value == 0) {
            clearTimerState()
        }
        true
    }

    // ServiceConnection for binding to TimerService
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            serviceBinder = binder as TimerService.TimerBinder
            serviceBinder?.setHandler(timerHandler)
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            serviceBinder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind to TimerService
        val intent = Intent(this, TimerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Start Button
        findViewById<Button>(R.id.startButton).setOnClickListener {
            val (savedTime, isPaused) = loadTimerState()
            if (serviceBound) {
                if (isPaused) {
                    serviceBinder?.start(savedTime)
                    clearTimerState()
                } else {
                    serviceBinder?.start(100)
                }
            }
        }

        // Stop(Pause) Button
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (serviceBound) {
                serviceBinder?.pause()
                val remaining = serviceBinder?.getRemainingTime() ?: 100
                saveTimerState(remaining, true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
        }
    }

    private fun saveTimerState(remainingTime: Int, paused: Boolean) {
        getSharedPreferences("TimerPrefs", MODE_PRIVATE).edit().apply {
            putInt("remainingTime", remainingTime)
            putBoolean("paused", paused)
            apply()
        }
    }

    private fun loadTimerState(): Pair<Int, Boolean> {
        val prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE)
        return Pair(
            prefs.getInt("remainingTime", 100),
            prefs.getBoolean("paused", false)
        )
    }

    private fun clearTimerState() {
        getSharedPreferences("TimerPrefs", MODE_PRIVATE).edit().apply {
            remove("remainingTime")
            remove("paused")
            apply()
        }
    }
}