package com.app.kgs_user.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkChangeReceiver(internal var context: Context) {

    val isNetworkAvailable: Boolean
        get() {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
}