package com.aya.merchant_socket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.aya.merchant_socket.databinding.ActivityMainBinding
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val mSocket: Socket by lazy {
        IO.socket("http://chat.socket.io")
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mSocket.on("new message", onNewMessage);

        mSocket.connect()

        binding.sendResult.setOnClickListener {
            attemptSend()
        }
    }

    private fun attemptSend() {
       val message: String = binding.result.getText().toString().trim()
        if (TextUtils.isEmpty(message)) {
            return
        }
        mSocket.emit("order message", message)
    }

    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            val message: String
            try {
                username = data.getString("username")
                message = data.getString("message")
            } catch (e: JSONException) {
                return@Runnable
            }

            // add the message to view
            binding.result.text = username.plus("----$message")

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
        mSocket.off("new message", onNewMessage)
        mSocket.off("order message", onNewMessage)
    }
}