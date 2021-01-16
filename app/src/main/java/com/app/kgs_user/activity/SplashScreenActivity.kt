package com.app.kgs_user.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.app.kgs_user.R
import com.app.kgs_user.utils.PermissionUtil
import com.app.kgs_user.utils.SessionManager

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash_screen)

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this@SplashScreenActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this@SplashScreenActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val dialog = SweetAlertDialog(this@SplashScreenActivity, SweetAlertDialog.NORMAL_TYPE)
                //dialog.titleText = getString(R.string.app_name)
                dialog.setCancelable(false)
                dialog.contentText = "Please allow permissions to Proceed"
                dialog.confirmText = "GRANT"
                dialog.setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    requestAllPermission()
                }
                dialog.show()
            } else {
                init()
            }
        } else {
            init()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == INITIAL_REQUEST) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                init()
            } else {
                Toast.makeText(this@SplashScreenActivity, "Please allow permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun init() {

        sessionManager = SessionManager(this)

        Handler().postDelayed({
            if (sessionManager.isLoggedIn) {

                /*if (sessionManager.latitude!!.isEmpty()) {
                    val mainIntent = Intent(this@SplashScreenActivity, GPSActivity::class.java)
                    startActivity(mainIntent)
                } else {
                    val mainIntent = Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
                    startActivity(mainIntent)
                }*/
                val mainIntent = Intent(this@SplashScreenActivity, GPSActivity::class.java)
                startActivity(mainIntent)
                overridePendingTransition(R.anim.left_enter, R.anim.right_out)
                finish()
            } else {
                val intent = Intent(this@SplashScreenActivity, LandingPageActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.left_enter, R.anim.right_out)
                finish()
            }
        }, 3000)
    }

    private fun requestAllPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@SplashScreenActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            || ActivityCompat.shouldShowRequestPermissionRationale(
                this@SplashScreenActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            val dialog = SweetAlertDialog(this@SplashScreenActivity, SweetAlertDialog.NORMAL_TYPE)
            //dialog.titleText = getString(R.string.app_name)
            dialog.setCancelable(false)
            dialog.contentText = "Please allow permissions to Proceed"
            dialog.confirmText = "GRANT"
            dialog.setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                finish()
            }
            dialog.show()
        } else {
            ActivityCompat.requestPermissions(this@SplashScreenActivity,
                INITIAL_PERMS,
                INITIAL_REQUEST
            )
        }
    }


    companion object {

        private val TAG = "SplashScreen"
        private val INITIAL_PERMS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private val INITIAL_REQUEST = 1514
    }
}
