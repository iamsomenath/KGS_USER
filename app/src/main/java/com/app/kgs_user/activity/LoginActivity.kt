package com.app.kgs_user.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.app.kgs_user.R
import com.app.kgs_user.login_mvp.LoginInteractor
import com.app.kgs_user.login_mvp.LoginPresenter
import com.app.kgs_user.login_mvp.LoginView
import com.app.kgs_user.utils.LoadingDialog
import com.app.kgs_user.utils.NetworkChangeReceiver
import com.app.kgs_user.utils.SessionManager
import com.app.kgs_user.utils.snackbar
import com.facebook.FacebookSdk
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), LoginView {

    lateinit var loginPresenter: LoginPresenter
    private var loadingDialog: LoadingDialog? = null
    lateinit var sessionManager: SessionManager
    internal var network: Boolean? = false
    lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)

        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        loadingDialog = LoadingDialog(this)
        networkChangeReceiver = NetworkChangeReceiver(this)
        network = networkChangeReceiver.isNetworkAvailable
        loginPresenter = LoginPresenter(this, LoginInteractor())


        forgot_pass.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            overridePendingTransition(
                R.anim.left_in,
                R.anim.right_out
            )
        }

        signup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            overridePendingTransition(
                R.anim.left_in,
                R.anim.right_out
            )
        }

        login.setOnClickListener {
            if (mobile.text.toString().isEmpty()) {
                mobile.error = "Please Enter Mobile Number"
                mobile.requestFocus()
                //contentLayout.snackbar("Please Enter Mobile Number")
            } else if (mobile.text.toString().length != 10) {
                mobile.error = "Please Enter Valid Mobile Number"
                mobile.requestFocus()
                //contentLayout.snackbar("Please Enter Valid Mobile Number")
            } else if (etPassword.text.toString().isEmpty()) {
                etPassword.error = "Please Enter Password"
                etPassword.requestFocus()
                //contentLayout.snackbar("Please Enter Password")
            }else{
                if (network!!) {
                    loginPresenter.validateCredentials(
                        mobile.text.toString(),
                        etPassword.text.toString(),
                        this@LoginActivity
                    )
                }else{
                    //toast("Please check your internet connection!!!")
                    contentLayout.snackbar("Please check your internet connection!!!")
                }
            }
        }
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    override fun showProgress() {
        loadingDialog!!.showDialog()
    }

    override fun hideProgress() {
        loadingDialog!!.hideDialog()
    }

    override fun setLoginError(errResponse: String) {
        //Toast.makeText(this, errResponse, Toast.LENGTH_SHORT).show()
        contentLayout.snackbar(errResponse)
    }

    override fun navigateToHome(response: String) {
        try {
            val jsonObject = JSONObject(response)
            Log.d("User_details", jsonObject.toString())
            if (jsonObject.getInt("status") == 200) {
                try {
                    if (jsonObject.getJSONArray("data").length() == 0)
                        contentLayout.snackbar(jsonObject.getString("message"))
                } catch (e: Exception) {
                    val jobj = jsonObject.getJSONObject("data")
                    if (jobj.getString("status") == "A") {
                        sessionManager.setLogin(
                            true, jobj.getString("id"), jobj.getString("name"),
                            jobj.getString("email"), jobj.getString("contact")
                        )
                        showPopup("Login Successful")
                    }else{
                        val inte = Intent(this, OTPActivity::class.java)
                        inte.putExtra("ID", jobj.getString("id"))
                        inte.putExtra("NAME", jobj.getString("name"))
                        inte.putExtra("EMAIL", jobj.getString("email"))
                        inte.putExtra("MOBILE", jobj.getString("contact"))
                        //inte.putExtra("OTP", jobj.getString("OTP"))
                        startActivity(inte)
                        finishAffinity()
                    }
                }
            } else
                contentLayout.snackbar(jsonObject.getString("message"))
            //showPopup(jsonObject.getString("message"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showPopup(message: String) {
        val dialog = SweetAlertDialog(this@LoginActivity, SweetAlertDialog.SUCCESS_TYPE)
        dialog.titleText = getString(R.string.app_name)
        dialog.setCancelable(false)
        dialog.contentText = message
        dialog.confirmText = "DONE"
        dialog.setConfirmClickListener { sDialog ->
            sDialog.dismissWithAnimation()
            startActivity(Intent(this@LoginActivity, WelcomeActivity::class.java))
            overridePendingTransition(
                R.anim.left_in,
                R.anim.right_out
            )
            finishAffinity()
        }
        dialog.show()
    }
}
