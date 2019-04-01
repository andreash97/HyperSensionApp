package com.example.hypersensionapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.connected.*

class ConnectedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connected)
        disconnectbutton.setOnClickListener {
            val intentdisconnect = Intent(this, MainActivity::class.java)
            startActivity(intentdisconnect)
        }
    }
}

