package com.dicoding.intermediate.service.myservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.dicoding.intermediate.service.myservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Akses notifikasi telah diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Akses notifikasi telah ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= 33) {
            setupNotification()
        }
        setupBackgroundService()
        setupForegroundService()
        setupBoundService()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupNotification() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Already granted
            }
            shouldShowRequestPermissionRationale(POST_NOTIFICATION) -> {
                // Show rationale message
            }
            else -> {
                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissionLauncher.launch(POST_NOTIFICATION)
                }
            }
        }
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

    /**
     * Bound service settings
     */
    private var boundStatus = false
    private lateinit var boundService: MyBoundService

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myBinder = service as MyBoundService.MyBinder
            boundService = myBinder.getService
            boundStatus = true
            getNumberFromService()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            boundStatus = false
        }
    }

    private fun getNumberFromService() {
        boundService.numberLiveData.observe(this) { number ->
            binding.tvBoundServiceNumber.text = number.toString()
        }
    }

    private fun setupBoundService() {
        val boundServiceIntent = Intent(this, MyBoundService::class.java)
        binding.btnStartBoundService.setOnClickListener {
            bindService(boundServiceIntent, connection, BIND_AUTO_CREATE)
        }
        binding.btnStopBoundService.setOnClickListener {
            unbindService(connection)
        }
    }

    override fun onStop() {
        super.onStop()
        if (boundStatus) {
            unbindService(connection)
            boundStatus = false
        }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val POST_NOTIFICATION = android.Manifest.permission.POST_NOTIFICATIONS
    }

}