package com.dicoding.intermediate.service.myservice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.intermediate.service.myservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackgroundService()
        setupForegroundService()
    }

    private fun setupBackgroundService() {
        val serviceIntent = Intent(this, MyBackgroundService::class.java)
        binding.btnStartBackgroundService.setOnClickListener {
            startService(serviceIntent)
        }
        binding.btnStopBackgroundService.setOnClickListener {
            stopService(serviceIntent)
        }
    }

    private fun setupForegroundService() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        binding.btnStartForegroundService.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
        binding.btnStopForegroundService.setOnClickListener {
            stopService(serviceIntent)
        }
    }

}