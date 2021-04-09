package com.dicoding.githublistapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.dicoding.githublistapplication.R

class SplashActivity : AppCompatActivity() {
    private val splashTimeOut:Long = 3000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }, splashTimeOut)
    }

}