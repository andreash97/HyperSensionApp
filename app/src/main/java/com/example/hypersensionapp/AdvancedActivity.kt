package com.example.hypersensionapp

import android.annotation.TargetApi
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.BatteryManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.widget.Toast
import com.example.hypersensionapp.Fragments.AgrafFragment
import com.example.hypersensionapp.Fragments.AkgyFragment
import com.example.hypersensionapp.Fragments.TestsignalFragment
import kotlinx.android.synthetic.main.activity_advanced.*
import kotlinx.android.synthetic.main.connected.*
import kotlinx.android.synthetic.main.connected.disconnectbutton
import kotlinx.android.synthetic.main.connected.testsignalbutton
import java.io.IOException
import java.util.*

class AdvancedActivity :AppCompatActivity(), TestsignalFragment.OnFragmentInteractionListener, AgrafFragment.OnFragmentInteractionListener, AkgyFragment.OnFragmentInteractionListener{

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        val textFail = "Kunne ikke koble til!"
        val textSuccess = "Koblet til!"
        val duration = Toast.LENGTH_LONG
    }


    lateinit var testsignalfragment: TestsignalFragment
    lateinit var agraffragment : AgrafFragment
    lateinit var akgyfragment : AkgyFragment
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private var ChannelID = "com.example.hypersensionapp"
    private var description = "Low battery"


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced)
        m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)
        ConnectToDevice(this).execute()


        // Notification
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, ControlActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(ChannelID, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(this, ChannelID)
                .setContentTitle("HyperSension device LOW BATTERY")
                .setContentText("Your HyperSension device is low on battery, please charge")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon((BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher)))
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
        } else {
            builder = Notification.Builder(this)
                .setContentTitle("HyperSension device LOW BATTERY")
                .setContentText("Your HyperSension device is low on battery, please charge")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon((BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher)))
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
        }


        testsignalbutton.setOnClickListener {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, testsignalfragment)
                .addToBackStack(testsignalfragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
        Akselgyro.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, akgyfragment)
                .addToBackStack(akgyfragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }


        agrafer.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, agraffragment)
                .addToBackStack(agraffragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }


        disconnectbutton.setOnClickListener {
            disconnect()
        }

        testsignalfragment = TestsignalFragment.newInstance()
        akgyfragment = AkgyFragment.newInstance()
        agraffragment = AgrafFragment.newInstance()
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        this.registerReceiver(myBroadcastReceiver, intentFilter)
    }

    // uses broadcastreciever and gets the batterystatus of the device, then displays it in activity_advanced.xml. https://www.youtube.com/watch?v=IekM_vjIiL0
    private val myBroadcastReceiver = object : BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context?, intent: Intent) {
            val stringBuilder = StringBuilder()
            val batteryPercentage = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0)
            stringBuilder.append("Batteri: $batteryPercentage %")
            batterystatus2.text = stringBuilder


            // change battery picture based on how much battery is left
            if (batteryPercentage in 0..19) {
                batim2.setImageResource(R.drawable.battery_25)
            }
            if (batteryPercentage in 20..39) {
                batim2.setImageResource(R.drawable.battery_50)
            }
            if (batteryPercentage in 40..84) {
                batim2.setImageResource(R.drawable.battery_75)
            }
            if (batteryPercentage in 85..100) {
                batim2.setImageResource(R.drawable.battery_100)
            }


            // If statements for notification on low battery
            if (batteryPercentage <= 20) {
                notificationManager.notify(1234,builder.build())
            }
            if(batteryPercentage > 20) {
                notificationManager.cancelAll()
            }
        }
    }


    // Unregister receiver on destroy and removes all notifications made by the app
    override fun onDestroy() {
        unregisterReceiver(myBroadcastReceiver)
        notificationManager.cancelAll()
        super.onDestroy()
    }


    // Back button will close fragment if open and turn orientation to portrait
    // If fragment is not open then back button will trigger disconnect
    override fun onBackPressed() {
        if(testsignalfragment.isVisible || akgyfragment.isVisible || agraffragment.isVisible) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            super.onBackPressed()
        } else
            disconnect()
    }
    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
        val intentdisconnect = Intent(this, MainActivity::class.java)
        startActivity(intentdisconnect)
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context
        init {
            this.context = c
        }
        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Kobler til...", "venligst vent")
        }
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "Kunne ikke koble til")
                val toastFail = Toast.makeText(context, textFail, duration)
                toastFail.show()
            } else {
                m_isConnected = true
                val toastSuccess = Toast.makeText(context, textSuccess, duration)
                toastSuccess.show()
            }
            m_progress.dismiss()
        }
    }


    override fun onFragmentInteraction(uri: Uri) {
        Log.d("Info button", "info is comming")
    }
}
