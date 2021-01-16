package com.app.kgs_user.activity

import android.content.Intent
import android.content.IntentSender
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.kgs_user.R
import com.app.kgs_user.utils.LoadingDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import kotlinx.android.synthetic.main.activity_test.*

class GPSActivity : AppCompatActivity(), View.OnClickListener {

    private var googleApiClient: GoogleApiClient? = null

    private val apiClientInstance: GoogleApiClient
        get() = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        googleApiClient = apiClientInstance
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }

        turn_location.setOnClickListener(this)

        requestGPSSettings()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.turn_location -> nextActivity()
            else -> {
            }
        }
    }

    private fun requestGPSSettings() {

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 500
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(
            googleApiClient,
            builder.build()
        )
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    //Log.i("", "All location settings are satisfied.")
                    //Toast.makeText(application, "GPS is already enable", Toast.LENGTH_SHORT).show()
                    nextActivity()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "",
                        "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings "
                    )
                    try {
                        status.startResolutionForResult(
                            this@GPSActivity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("Applicationsett", e.toString())
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Log.i(
                        "",
                        "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created."
                    )
                    Toast.makeText(
                        application,
                        "Location settings are inadequate, and cannot be fixed here",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun nextActivity() {
        val loadingDialog = LoadingDialog(this@GPSActivity)
        loadingDialog.showDialog()
        turn_location.text = "Searching GPS....."
        Handler().postDelayed({
            loadingDialog.hideDialog()
            startActivity(Intent(this, WelcomeActivity::class.java))
            overridePendingTransition(
                R.anim.left_in,
                R.anim.right_out
            )
            finish()
        }, 3000)
    }

    companion object {
        protected val REQUEST_CHECK_SETTINGS = 0x1
    }
}