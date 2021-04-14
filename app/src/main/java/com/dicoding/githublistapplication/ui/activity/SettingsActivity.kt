package com.dicoding.githublistapplication.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dicoding.githublistapplication.R
import com.dicoding.githublistapplication.databinding.ActivitySettingsBinding
import com.dicoding.githublistapplication.receiver.AlarmReceiver

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var alarmReceiver: AlarmReceiver
    private var binding: ActivitySettingsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.btnAlarm?.setOnClickListener(this)
        binding?.btnAlarmDeactivate?.setOnClickListener(this)
        supportActionBar?.title = "Settings"
        //set back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        alarmReceiver = AlarmReceiver()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_alarm -> {
                val repeatMessage = "Dont miss out, comeback!"
                alarmReceiver.setRepeatingAlarm(this, repeatMessage)
            }

            R.id.btn_alarm_deactivate->{
                alarmReceiver.cancelAlarm(this)
            }


        }
    }
}