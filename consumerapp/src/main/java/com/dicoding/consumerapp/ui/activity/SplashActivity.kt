package com.dicoding.consumerapp.ui.activity

import android.content.Intent

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.consumerapp.R

class SplashActivity : AppCompatActivity() {
    private val splashTimeOut:Long = 3000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this,
                MainActivity::class.java))
            finish()
        }, splashTimeOut)
    }

}