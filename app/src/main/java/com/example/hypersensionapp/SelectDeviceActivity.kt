package com.example.hypersensionapp

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Switch
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.select_device_layout.*
import org.jetbrains.anko.toast

class SelectDeviceActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1
    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(m_bluetoothAdapter == null) {
            toast("Enheten støtter ikke Bluetooth")
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        select_device_refresh.setOnClickListener{ pairedDeviceList() }



    }

    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList() //
        val listOfNames : ArrayList<String> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device)
                listOfNames.add(device.name)
                Log.i("device", ""+device.name)
            }
        } else {
            toast("Ingen parrede enheter funnet")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfNames)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            // checks if advanced mode is selected or not,
            if (advanced.isChecked) {
                val intent = Intent(this, AdvancedActivity::class.java)
                intent.putExtra(EXTRA_ADDRESS, address)
                startActivity(intent)
            } else {
                val intent = Intent(this, ControlActivity::class.java)
                intent.putExtra(EXTRA_ADDRESS, address)
                startActivity(intent)
            }


            //  if (advanced.isChecked) {
            //                val intent = Intent(this, ControlActivity::class.java)
            //                intent.putExtra(EXTRA_ADDRESS, address)
            //                startActivity(intent)
            //            } else {
            //                val intent = Intent(this, MainActivity::class.java)
            //                intent.putExtra(EXTRA_ADDRESS, address)
            //                startActivity(intent)
            //            }

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth er aktivert")
                } else {
                    toast("Bluetooth er deaktivert")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Bluetooth aktivering har blitt avbrutt")
            }
        }
    }
}
