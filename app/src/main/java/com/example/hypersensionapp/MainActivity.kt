package com.example.hypersensionapp

import android.content.Intent
import android.icu.text.IDNA
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.hypersensionapp.Fragments.InfoFragment

import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), InfoFragment.OnFragmentInteractionListener {

    lateinit var infofragment:InfoFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Infobutton is a image button that opens up info fragment when pushed
        infobutton.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, infofragment)
                .addToBackStack(infofragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
        // connectbutton is used to go from mainactivity to selectdeviceactivity on click
        connectbutton.setOnClickListener {
            val intentconnect = Intent(this, SelectDeviceActivity::class.java)
            startActivity(intentconnect)

        }


        infofragment = InfoFragment.newInstance()

    }

// //  closes application when pressing backbutton from MainActivity
    override fun onBackPressed() {
        if(infofragment.isVisible) {
            super.onBackPressed()
        } else {
            ActivityCompat.finishAffinity(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        Log.d("Info button", "info is comming")
    }
}
