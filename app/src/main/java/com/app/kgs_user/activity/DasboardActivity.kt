package com.app.kgs_user.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.kgs_user.R
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

import java.io.IOException
import java.util.Locale

class DasboardActivity : AppCompatActivity() {

    private var errorMessage = ""
    private var latitude_str = ""
    private var longitude_str = ""
    private var myAddress = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*if (DeliveryEverything.getAppSharedPreference().getCurrentLocation().equals("") || DeliveryEverything.getAppSharedPreference().getCurrentLocation().length() === 0) {
            //featchCurrentLocation();
            location()
        } else {
            location()
            tv_dashboard_address!!.setText(DeliveryEverything.getAppSharedPreference().getCurrentLocation())
        }*/

        featchCurrentLocation()
    }


    override fun onBackPressed() {

        /*if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            exitApp()
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)*/
    }

    private fun featchCurrentLocation() {
        Dexter.withActivity(this@DasboardActivity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    location()
                    //                            startActivity(new Intent(LocationOnActivity.this, DasboardActivity.class));
                    //                            finish();
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        val builder = AlertDialog.Builder(this@DasboardActivity)
                        builder.setTitle("Permission Denied")
                            .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("OK") { dialog, which ->
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                intent.data = Uri.fromParts("package", getPackageName(), null)
                            }
                            .show()
                    } else {
                        Toast.makeText(this@DasboardActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }


    private fun location() {
        val provider = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
        if (!provider.contains("gps")) {
            Toast.makeText(this, "Please enable GPS...", Toast.LENGTH_SHORT).show()
        } else {
            // Toast.makeText(this, "Please wait while fetching address!", Toast.LENGTH_SHORT).show();
            getCurrentLocation("1")
        }
    }

    private fun getCurrentLocation(type: String) {

        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Define a listener that responds to location updates
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Called when a new location is found by the network location provider.
                val lat = java.lang.Double.toString(location.latitude)
                val lon = java.lang.Double.toString(location.longitude)
                latitude_str = lat
                longitude_str = lon
                //Log.d("LATLONG", lat + " " + lon + "");
                try {
                    if (type == "1") {
                        GetAddress(lat, lon)
                    } else {

                    }
                } catch (ignored: Exception) {
                    //new CustomToast().Show_Toast(getActivity(), myview, "Can't fetch proper location!");
                }

            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
    }


    fun GetAddress(lat: String, lon: String) {
        val geocoder = Geocoder(this, Locale.ENGLISH)
        var ret: String
        try {
            val addresses = geocoder.getFromLocation(
                java.lang.Double.parseDouble(lat),
                java.lang.Double.parseDouble(lon), 1
            )
            if (addresses != null) {
                val returnedAddress = addresses[0]
                /*StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }*/
                //ret = strReturnedAddress.toString();
                ret = returnedAddress.getAddressLine(0)
                /*tv_dashboard_address!!.text = ret
                DeliveryEverything.getAppSharedPreference().saveCurrentLocation(ret)

                DeliveryEverything.getAppSharedPreference().saveCurrentLocation(ret)
                DeliveryEverything.getAppSharedPreference().saveLatitude(returnedAddress.latitude.toString())
                DeliveryEverything.getAppSharedPreference().saveLongitude(returnedAddress.latitude.toString())*/

            } else {
                ret = "No Address returned!"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ret = "Can't get Address!"

            val snackbar = Snackbar.make(
                this.findViewById(android.R.id.content),
                "Please turn on GPS", Snackbar.LENGTH_LONG
            )
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(Color.WHITE)
            snackbar.show()
        }

    }
}


