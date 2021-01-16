package com.app.kgs_user.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.app.kgs_user.R
import com.app.kgs_user.adapter.KitchenListAdapter
import com.app.kgs_user.callBacks.CallbackAllKitchenList
import com.app.kgs_user.model.KitchenData
import com.app.kgs_user.utils.LoadingDialog
import com.app.kgs_user.utils.LocationAddress
import com.app.kgs_user.utils.SessionManager
import com.app.kgs_user.utils.snackbar
import com.app.kgs_user.viewmodel.KitchenListViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_welcome.*
import java.io.IOException
import java.util.*

open class WelcomeActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, CallbackAllKitchenList {

    lateinit var loadingDialog: LoadingDialog
    lateinit var sessionManager: SessionManager
    lateinit var allList: ArrayList<KitchenData>
    lateinit var kitchenListAdapter: KitchenListAdapter

    lateinit var mGoogleApiClient: GoogleApiClient
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null

    private var mLocation: Location? = null
    private var mLocationManager: LocationManager? = null
    private var locationManager: LocationManager? = null

    private var errorMessage = ""
    private var latitude_str = ""
    private var longitude_str = ""
    private var myAddress = ""
    lateinit var viewModel: KitchenListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        getPermission()

        user_profile.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, AccountDetailsActivity::class.java))
        }
        tvBtn_buttomSheetBtnContinue.setOnClickListener {
            //Load animation
            val slide_down = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.slide_bottom
            )
            bottomCard.startAnimation(slide_down)
            bottomCard.visibility = View.GONE
        }
    }

    private val isLocationEnabled: Boolean
        get() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(KitchenListViewModel::class.java)
        viewModel.setCallback(this)
    }

    private fun requestAllPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this@WelcomeActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this@WelcomeActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            val builder1 = AlertDialog.Builder(this@WelcomeActivity)
            builder1.setMessage("This app cannot work without the Location Permission")
            builder1.setCancelable(false)
            builder1.setPositiveButton("Grant permission"
            ) { dialog, id ->
                dialog.dismiss()
                initValue()
            }
            val alert11 = builder1.create()
            if (!this@WelcomeActivity.isFinishing) {
                alert11.show()
            }
            initValue()
        } else {
            ActivityCompat.requestPermissions(this@WelcomeActivity,
                INITIAL_PERMS,
                INITIAL_REQUEST
            )
            initValue()
        }
    }

    private fun getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this@WelcomeActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@WelcomeActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val builder1 = AlertDialog.Builder(this@WelcomeActivity)
                builder1.setMessage("This app can't work without Location Permissions")
                builder1.setCancelable(false)
                builder1.setPositiveButton("Grant permission"
                ) { dialog, id ->
                    requestAllPermission()
                    dialog.dismiss()
                }
                val alert11 = builder1.create()
                if (!this@WelcomeActivity.isFinishing) {
                    alert11.show()
                }
            } else {
                initValue()
            }
        } else {
            initValue()
        }
    }

    private fun initValue() {

        sessionManager = SessionManager(this)
        loadingDialog = LoadingDialog(this)

        mLocationRequest = LocationRequest()
        mSettingsClient = LocationServices.getSettingsClient(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        mGoogleApiClient.connect()

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkLocation() //check whether location service is enable or not in your  phone

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                // location is received
                mCurrentLocation = locationResult!!.lastLocation
                updateLocationUI()
            }
        }
        if (mCurrentLocation != null) {
            nextActivity()
        }
    }

    override fun onBackPressed() {
        showBackPress()
    }

    /*private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this@WelcomeActivity)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(
                    this@WelcomeActivity, result,
                    101
                ).show()
            }

            return false
        }

        return true
    }

    @SuppressLint("RestrictedApi")
    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = Constants.INTERVAL
        mLocationRequest!!.fastestInterval = Constants.FIRST_INTERVAL
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }
*/

    private inner class GeocoderHandler : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            when (message.what) {
                1 -> {
                    val bundle = message.data
                    locationAddress = bundle.getString("address")
                }
                else -> locationAddress = null
            }
        }
    }

    private fun showBackPress() {
        MaterialDialog.Builder(this)
            .title(resources.getString(R.string.dialogTitle_exit))
            .content(resources.getString(R.string.dialogMessage_exit))
            .positiveText(resources.getString(R.string.dialogPositiveButtonText_logout))
            .positiveColor(ContextCompat.getColor(this, R.color.button_and_vespac_red_color))
            .negativeText(resources.getString(R.string.dialogPositiveButtonText_cancel))
            .negativeColor(ContextCompat.getColor(this, R.color.colorTranslucentButton))
            .onPositive { dialog, which ->
                finishAffinity()
            }
            .cancelable(false)
            .onNegative { dialog, which -> dialog.dismiss() }.show()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        startLocationUpdates()

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLocation == null) {
            startLocationUpdates()
        }
    }

    private fun updateLocationUI() {
        if (mCurrentLocation != null) {

            val latitude = mCurrentLocation!!.latitude
            val longitude = mCurrentLocation!!.longitude
            val locationAddress = LocationAddress
            LocationAddress.getAddressFromLocation(
                latitude,
                longitude,
                applicationContext,
                GeocoderHandler()
            )

            latitude_str = mCurrentLocation!!.latitude.toString()
            longitude_str = mCurrentLocation!!.longitude.toString()
            nextActivity()
        }
    }

    override fun onConnectionSuspended(i: Int) {
        //Log.i(WelcomeActivity.TAG, "Connection Suspended")
        mGoogleApiClient.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        //Log.i(MainActivity.TAG, "Connection failed. Error: " + connectionResult.errorCode)
    }

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        mSettingsClient!!
            .checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(this) {
                //Log.i("", "All location settings are satisfied.")

                mFusedLocationClient!!.requestLocationUpdates(mLocationRequest,
                    mLocationCallback!!, Looper.myLooper())

                updateLocationUI()
            }
            .addOnFailureListener(this) { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i("", "Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(this@WelcomeActivity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i("", "PendingIntent unable to execute request.")
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        Log.e("", errorMessage)

                        Toast.makeText(this@WelcomeActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                updateLocationUI()
            }
    }

    override fun onLocationChanged(location: Location) {

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                // In this sample, we get just a single address.
                1
            )

            val address: Address = addresses.get(0)
            var sb: StringBuilder = StringBuilder()

            if (addresses.isNotEmpty()) {
                sb = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }
                sb.append(address.getAddressLine(0))
            }
            myAddress = address.getAddressLine(0)

            nextActivity()
            //tvAddress.text = sb.toString()
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = "Service Unavailable"
            //Log.e(MainActivity.TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid_lat_long_Used"
            /*Log.e(
                    MainActivity.TAG, "$errorMessage. Latitude = $location.latitude , " +
                    "Longitude =  $location.longitude", illegalArgumentException
            )*/
        }
    }

    private fun checkLocation(): Boolean {
        return if (!isLocationEnabled) {
            //showAlert()
            false
        } else
            isLocationEnabled
    }

    companion object {

        private val INITIAL_PERMS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        private val INITIAL_REQUEST = 1514
        private val REQUEST_CHECK_SETTINGS = 100
    }

    @SuppressLint("SetTextI18n")
    private fun nextActivity() {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(
                latitude_str.toDouble(), longitude_str.toDouble(), 1
            )

            val address: Address = addresses[0]
            val sb: StringBuilder

            if (addresses.isNotEmpty()) {
                sb = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }
                sb.append(address.getAddressLine(0))
            }
            myAddress = address.getAddressLine(0)

        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = "Service Unavailable"
            //Log.e(MainActivity.TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid_lat_long_Used"
        }

        //toast("$latitude_str $longitude_str")
        Log.d("LOCATION", "$latitude_str $longitude_str $myAddress")
        sessionManager.setAddress(myAddress, latitude_str, longitude_str)
        location.text = "\uD83C\uDFE0 $myAddress"

        initViewModel()
        loadingDialog.showDialog()
        viewModel.getRestrurentList(latitude_str, longitude_str)
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccess(getRestaurant: ArrayList<KitchenData>?) {

        loadingDialog.hideDialog()
        allList = getRestaurant!!
        if (allList.size != 0) {
            bottomCard.visibility = View.VISIBLE
            listView.visibility = View.VISIBLE
            no_kitchen.visibility = View.GONE
            tv_item_count.text = allList.size.toString() + " Stores found near you"
            //Load animation
            val slide_up = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.slide_up
            )
            bottomCard.startAnimation(slide_up)
            initList()
        } else {
            listView.visibility = View.GONE
            no_kitchen.visibility = View.VISIBLE
        }
    }
    override fun onError(message: String?) {
        loadingDialog.hideDialog()
        contentLayout.snackbar(message!!)
        listView.visibility = View.GONE
        no_kitchen.visibility = View.VISIBLE
    }

    override fun onNetworkError(message: String?) {
        loadingDialog.hideDialog()
        //contentLayout.snackbar(message!!)
        listView.visibility = View.GONE
        no_kitchen.visibility = View.VISIBLE
        val snackbar = Snackbar
            .make(contentLayout, "Unable to fetch data", 6000)
            .setAction("Try Again") {
                    loadingDialog.showDialog()
                    viewModel.getRestrurentList(latitude_str, longitude_str)

            }
        snackbar.show()
    }

    private fun initList() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        listView.layoutManager = layoutManager
        kitchenListAdapter = KitchenListAdapter(this)
        try {
            kitchenListAdapter.clearRestaurantList()
            kitchenListAdapter.addRestaurantList(allList)
            kitchenListAdapter.notifyDataSetChanged()
            listView.adapter = kitchenListAdapter
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message.toString())
        }
    }
}