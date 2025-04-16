package edu.temple.myapplication

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log

class TimerService : Service() {

    private var remainingTime = 0
    private var isRunning = false
    private var paused = false
    private var timerHandler: Handler? = null
    private var t: TimerThread? = null

    inner class TimerBinder : Binder() {
        val isRunning: Boolean
            get() = this@TimerService.isRunning

        val paused: Boolean
            get() = this@TimerService.paused

        fun start(startValue: Int) {
            if (!paused) {
                if (!isRunning) {
                    t?.interrupt()
                    startTimer(startValue)
                }
            } else {
                pause()
            }
        }

        fun setHandler(handler: Handler) {
            timerHandler = handler
        }

        fun stop() {
            t?.interrupt()
        }

        fun pause() {
            this@TimerService.pause()
        }

        fun getRemainingTime(): Int = this@TimerService.remainingTime
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TimerService status", "Created")
    }

    override fun onBind(intent: Intent): IBinder {
        return TimerBinder()
    }

    private fun startTimer(startValue: Int) {
        t?.interrupt()
        t = TimerThread(startValue)
        t?.start()
    }

    private fun pause() {
        paused = !paused
        isRunning = !paused
    }

    inner class TimerThread(private var startTime: Int) : Thread() {
        override fun run() {
            remainingTime = startTime
            isRunning = true
            paused = false
            try {
                while (remainingTime > 0) {
                    timerHandler?.sendEmptyMessage(remainingTime)
                    var pauseCheck = 0
                    while (paused) {
                        sleep(100)
                        pauseCheck++
                        if (pauseCheck > 10) break
                    }
                    if (paused) continue
                    sleep(1000)
                    remainingTime--
                }
                timerHandler?.sendEmptyMessage(0)
                isRunning = false
                paused = false
            } catch (e: InterruptedException) {
                Log.d("TimerService", "Timer interrupted")
                isRunning = false
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        t?.interrupt()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        t?.interrupt()
        super.onDestroy()
        Log.d("TimerService status", "Destroyed")
    }
}