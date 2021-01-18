package com.app.kgs_user.helper

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log

import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

import org.json.JSONObject
import com.app.kgs_user.utils.Constants


class CGlobal private constructor() {

    private var mPrefsVersionPersistent: SharedPreferences? = null
    internal var mEditorVersionPersistent: SharedPreferences.Editor? = null

    private var mRequestQueue: RequestQueue? = null

    fun addVolleyRequest(postRequest: StringRequest, clearBeforeQuery: Boolean, context: Context) {
        try {
            postRequest.retryPolicy = DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            if (clearBeforeQuery) {
                getRequestQueue(context).cancelAll { true }
            }
            getRequestQueue(context).add(postRequest)
        } catch (e: Exception) {
            //SSLog.e(TAG, "mRequestQueue may not be initialized - ", e);
        }
    }

    fun getRequestQueue(context: Context): RequestQueue {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mVolleyRequestQueue == null) {
            // Instantiate the cache
            val cache = DiskBasedCache(context.cacheDir, 1024 * 1024) // 1MB
            // cap
            // Set up the network to use HttpURLConnection as the HTTP client.
            val network = BasicNetwork(HurlStack())
            // Instantiate the RequestQueue with the cache and network.
            mVolleyRequestQueue = RequestQueue(cache, network)
            mVolleyRequestQueue!!.start()
        }
        return mVolleyRequestQueue as RequestQueue
    }

    fun checkParams(map: MutableMap<String, String>): Map<String, String> {
        for ((key, value) in map) {
            if (value == null) {
                map[key] = ""
                Log.d(TAG, key)
            }
        }
        return map
    }

    fun getPersistentPreferenceEditor(context: Context): SharedPreferences.Editor? {
        if (mEditorVersionPersistent == null) {
            mEditorVersionPersistent = getPersistentPreference(context).edit()
        }
        return mEditorVersionPersistent
    }

    fun getPersistentPreference(context: Context): SharedPreferences {
        if (mPrefsVersionPersistent == null) {
            mPrefsVersionPersistent = context.applicationContext
                .getSharedPreferences(Constants.PREFS_VERSION_PERSISTENT, Context.MODE_PRIVATE)
        }
        return mPrefsVersionPersistent!!
    }

    fun isNullNotDefined(jo: JSONObject, jkey: String): Boolean {
        return if (!jo.has(jkey)) {
            true
        } else jo.isNull(jkey)
    }

    fun turnGPSOn1(activity: Activity, googleApiClient: GoogleApiClient) {

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true) //this is the key ingredient

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                            activity, REQUEST_LOCATION_LIB
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }// All location settings are satisfied. The client can initialize location
            // requests here.
            // Location settings are not satisfied. However, we have no way to fix the
            // settings so we won't show the dialog.
        }
    }

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected && info.isConnectedOrConnecting
    }

    fun checkConnected(context: Context): Boolean {
        for (i in 0..2) {
            try {
                if (isConnected(context)) {
                    return true
                }
                Thread.sleep(500)
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }

        }
        return false
    }

    companion object {

        val TAG = "CGlobal: "

        private var instance: CGlobal? = null
        var mVolleyRequestQueue: RequestQueue? = null
        val REQUEST_LOCATION_LIB = 1001

        @Synchronized
        fun getInstance(): CGlobal {
            if (instance == null) {
                instance = CGlobal()
            }
            return instance!!
        }
    }
}
