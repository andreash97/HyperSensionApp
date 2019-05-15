package com.example.hypersensionapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import com.example.hypersensionapp.Fragments.InfoFragment

import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), InfoFragment.OnFragmentInteractionListener {
    lateinit var infofragment:InfoFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This will display the xml file called activity_main, which will be the first thing you see when opening the app
        setContentView(R.layout.activity_main)


        // Infobutton is a image button that opens up info fragment when pushed
        infobutton.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, infofragment)
                .addToBackStack(infofragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }


        // Connectbutton is used to go from mainactivity to selectdeviceactivity on click
        connectbutton.setOnClickListener {
            val intentconnect = Intent(this, SelectDeviceActivity::class.java)
            startActivity(intentconnect)
        }


        infofragment = InfoFragment.newInstance()
    }


// //  Closes application when pressing backbutton from MainActivity when fragment is not open
    override fun onBackPressed() {
        if(infofragment.isVisible) {
            super.onBackPressed()
        } else {
            ActivityCompat.finishAffinity(this)
        }
    }


    override fun onFragmentInteraction(uri: Uri) {
        Log.d("Info button", "info is comming")
    }
}
