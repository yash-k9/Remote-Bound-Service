package com.yashk9.remoteboundservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yashk9.remoteboundservice.databinding.ActivityRemoteBinding
import com.yashk9.remoteboundservice.service.DataService

class RemoteActivity : AppCompatActivity() {
    private var randomMessenger: Messenger? = null
    private var mBound = false
    private var randomNumber = 0

    private lateinit var binding: ActivityRemoteBinding

    companion object{
        const val TAG = "CLIENT_APP"
        const val RANDOM_NUMBER_FLAG = 0
    }

    inner class IncomingHandler(): Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                RANDOM_NUMBER_FLAG ->  {
                    randomNumber = msg.arg1
                    binding.textView.text = getString(
                        R.string.display_message,
                        randomNumber
                    )
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private val mMessenger = Messenger(IncomingHandler())

    private var serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            Log.d(TAG, "onServiceConnected: Service Connected")
            randomMessenger = Messenger(binder)
            mBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: Service Disconnected")
            randomMessenger = null
            mBound = false
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            bindService.setOnClickListener { startBind() }
            unbindService.setOnClickListener { unBind() }
            getRandomNumber.setOnClickListener { getRandomFromService() }
        }
    }

    private fun startBind() {
        Log.d(TAG, "startBind: Started Binding")
        Toast.makeText(applicationContext, "Started Binding", Toast.LENGTH_SHORT).show()
        Intent(this, DataService::class.java).apply {
            bindService(this, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    private fun unBind() {
        if(mBound){
            Log.d(TAG, "stopBind: Stopped Binding")
            Toast.makeText(applicationContext, "UnBinding", Toast.LENGTH_SHORT).show()
            unbindService(serviceConnection)
            mBound = false
        }
    }

    private fun getRandomFromService() {
        if(mBound){
            val msg = Message.obtain(null, 0, 0, 0)
            msg.replyTo = mMessenger
            try{
                randomMessenger?.send(msg)
            }catch (e: RemoteException){
                Log.d(TAG, "getRandomFromService: ${e.stackTrace}")
            }
        }else{
            Log.d(TAG, "getRandomFromService: DataService not Bounded")
            Toast.makeText(applicationContext, "DataService is not Bound", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Service Connection Destroyed")
    }
}