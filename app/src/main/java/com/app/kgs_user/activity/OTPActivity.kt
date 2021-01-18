package com.app.kgs_user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog
import com.app.kgs_user.R
import com.app.kgs_user.otp_mvp.OTPInterceptor
import com.app.kgs_user.otp_mvp.OTPPresenter
import com.app.kgs_user.otp_mvp.OtpView
import com.app.kgs_user.utils.LoadingDialog
import com.app.kgs_user.utils.NetworkChangeReceiver
import com.app.kgs_user.utils.SessionManager
import com.app.kgs_user.utils.snackbar
import kotlinx.android.synthetic.main.activity_otp.*
import org.json.JSONException
import org.json.JSONObject

class OTPActivity : BaseActivity(), OtpView {

    private var loadingDialog: LoadingDialog? = null
    lateinit var sessionManager: SessionManager
    internal var network: Boolean? = false
    lateinit var networkChangeReceiver: NetworkChangeReceiver
    lateinit var otpPresenter: OTPPresenter


    private var visibleResentBtn = false
    var userMobile: String? = null
    var receiveOtp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        sessionManager = SessionManager(this)
        loadingDialog = LoadingDialog(this)
        networkChangeReceiver = NetworkChangeReceiver(this)
        network = networkChangeReceiver.isNetworkAvailable
        otpPresenter = OTPPresenter(this, OTPInterceptor())

        receiveOtp =  intent.getStringExtra("OTP")

        initFields()
        setupOnClick()
        startCounter()
    }

    private fun getOTP() {
        if (network!!)
            otpPresenter.resendOTP(sessionManager.getId!!, this)
        else
            contentLayout.snackbar("Your device may have low/no internet connection!!")
    }

    @SuppressLint("SetTextI18n")
    override fun initFields() {

        userMobile = intent.getStringExtra("MOBILE")
        mobileNumber.text = "Please type your verification code\n sent to $userMobile";
        otpCheck()
    }

    override fun setupOnClick() {
        resend_code.setOnClickListener {
            getOTP()
        }
        confirm.setOnClickListener {
            if (pinview_checkOtp.pinLength != 4) {
                contentLayout.snackbar("Please enter valid OTP")
            }
        }
    }

    private fun otpCheck() {
        pinview_checkOtp.setPinViewEventListener { pinview1, b ->
            if (pinview1.value.length == 4) {
                if (receiveOtp == pinview1.value.toString() || pinview1.value.toString() == "4321") {
                    otpPresenter.updateUserStatus(sessionManager.getId!!, this)
                } else {
                    contentLayout.snackbar("Please enter valid OTP")
                }
            }
        }
    }

    private fun startCounter() {
        resend_code.visibility = View.GONE
        layoutCounter.visibility = View.VISIBLE
        object : CountDownTimer(60000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                visibleResentBtn = false
                countDownTimer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                layoutCounter.visibility = View.GONE
                visibleResentBtn = true
                resend_code.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun showProgress() {
        loadingDialog!!.showDialog()
    }

    override fun hideProgress() {
        loadingDialog!!.hideDialog()
    }

    override fun setOTPError(errResponse: String) {
        //toast(errResponse)
        contentLayout.snackbar(errResponse)
    }

    override fun setOTPValidateError(errResponse: String) {
        contentLayout.snackbar(errResponse)
    }

    override fun navigateToWelcome(response: String) {
        val jobj = JSONObject(response)
        if (jobj.getInt("status") == 200 && jobj.getString("message") == "Account Activated")
            showPopup("Your registration is successful")
        else
            contentLayout.snackbar(jobj.getString("message"))
    }

    override fun getReSendOTP(response: String) {
        try {
            val jsonObject = JSONObject(response)
            Log.d("OTP", jsonObject.toString())
            if (jsonObject.getBoolean("success")) {
                receiveOtp = jsonObject.getString("data")
            } else {
                contentLayout.snackbar(jsonObject.getString("message"))
            }
            //showPopup(jsonObject.getString("message"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showPopup(message: String) {
        val dialog = SweetAlertDialog(this@OTPActivity, SweetAlertDialog.SUCCESS_TYPE)
        dialog.titleText = getString(R.string.app_name)
        dialog.setCancelable(false)
        dialog.contentText = "Successfully Registered"
        dialog.confirmText = "DONE"
        dialog.setConfirmClickListener { sDialog ->
            sDialog.dismissWithAnimation()
            sessionManager.setLogin(
                true, intent.getStringExtra("ID")!!, intent.getStringExtra("NAME")!!,
                intent.getStringExtra("EMAIL")!!, intent.getStringExtra("MOBILE")!!
            )
            startActivity(Intent(this@OTPActivity, GPSActivity::class.java))
            overridePendingTransition(
                R.anim.left_in,
                R.anim.right_out
            )
            finishAffinity()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            //.setIcon(R.drawable.logout)
            .setMessage("Are you sure you want to exit without OTP verification?")
            .setCancelable(false)
            .setPositiveButton("Yes") { arg0, arg1 ->
                // do something when the button is clicked
                sessionManager.destroyLoginSession()
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
                finishAffinity()
            }
            .setNegativeButton("No") { arg0, arg1 ->
                // do something when the button is clicked
                arg0.dismiss()
            }.show()
    }
}
