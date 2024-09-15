package com.asp.fliptimer

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.asp.fliptimer.databinding.ActivityMainBinding
import com.asp.fliptimerviewlibrary.CountDownClock

class MainActivity : Activity() {

    private var isRunning: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)


        val typeface = ResourcesCompat.getFont(this, R.font.lbc_bold)
        binding.timerProgramCountdown.setCustomTypeface(typeface!!)
        binding.timerProgramCountdown.startCountDown(99999999)
        binding.timerProgramCountdown.setCountdownListener(object :
            CountDownClock.CountdownCallBack {
            override fun countdownAboutToFinish() {
                //TODO Add your code here
            }

            override fun countdownFinished() {
                Toast.makeText(this@MainActivity, "Finished", Toast.LENGTH_SHORT).show()
                binding.timerProgramCountdown.resetCountdownTimer()
                isRunning = false
                binding.btnPause.isEnabled = false
            }
        })


        binding.btnPause.setOnClickListener {
            if (isRunning) {
                isRunning = false
                binding.timerProgramCountdown.pauseCountDownTimer()
                binding.btnPause.text = getString(R.string.resume_timer)
            } else {
                isRunning = true
                binding.timerProgramCountdown.resumeCountDownTimer()
                binding.btnPause.text = getString(R.string.pause_timer)
            }
        }
    }
}
