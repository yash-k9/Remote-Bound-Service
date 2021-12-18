package com.yashk9.remoteboundservice.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

class DataService: Service() {
    
    companion object{
        private const val TAG = "DataService"
        const val RANDOM_NUMBER_TAG = 0
    }

    private var isGenerating = false
    private var randomNumber = 0

    inner class RandomNumberRequestHandler: Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                RANDOM_NUMBER_TAG -> {
                    Log.d(TAG, "handleMessage: Got Message From Remote -> curr num is = $randomNumber")
                    msg.replyTo.send(Message.obtain(null, 0, randomNumber, 0))
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private val randomMessenger = Messenger(RandomNumberRequestHandler())

    override fun onCreate() {
        Log.d(TAG, "onCreate: Data Service Created")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isGenerating = true
        startNumberGeneration()
        return START_STICKY
    }

    private fun startNumberGeneration() {
        val handler = Handler(Looper.getMainLooper())

        val runnable = object: Runnable{
            override fun run() {
                if(isGenerating){
                    Thread.sleep(100)
                    randomNumber = (1..7000).random()
                    Log.d(TAG, "run: $randomNumber")
                    handler.postDelayed(this, 100)
                }else{
                    handler.removeCallbacks(this)
                }
            }
        }

        handler.postDelayed(runnable, 100)
    }

    private fun stopNumberGeneration(){
        isGenerating = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return randomMessenger.binder
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNumberGeneration()
        Log.d(TAG, "onDestroy: Data Service Stopped")
    }
}