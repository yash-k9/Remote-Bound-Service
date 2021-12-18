package com.yashk9.remoteboundservice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yashk9.remoteboundservice.databinding.ActivityMainBinding
import com.yashk9.remoteboundservice.service.DataService


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intiViews()
    }

    private fun intiViews() {
        with(binding){
            startService.setOnClickListener { startDataService() }
            stopService.setOnClickListener { stopDataService() }
            remoteService.setOnClickListener { startRemoteServiceActivity() }
        }
    }

    private fun startRemoteServiceActivity() {
        Intent(this, RemoteActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun startDataService() {
        val serviceIntent = Intent(this, DataService::class.java)
        startService(serviceIntent)
    }

    private fun stopDataService() {
        Intent(this, DataService::class.java).apply {
            stopService(this)
        }
    }
}