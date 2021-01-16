package com.app.kgs_user.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.app.kgs_user.callBacks.CallbackResetPassword
import com.app.kgs_user.model.DataItemResetPass
import com.app.kgs_user.R
import com.app.kgs_user.viewmodel.ResetPasswordViewModel
import com.app.kgs_user.utils.*
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity(), CallbackResetPassword {

    lateinit var loadingDialog: LoadingDialog
    lateinit var sessionManager: SessionManager
    internal var network: Boolean? = false
    lateinit var networkChangeReceiver: NetworkChangeReceiver
    lateinit var viewModel: ResetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        sessionManager = SessionManager(this)
        loadingDialog = LoadingDialog(this)
        networkChangeReceiver = NetworkChangeReceiver(this)
        network = networkChangeReceiver.isNetworkAvailable

        viewModel = ViewModelProviders.of(this).get(ResetPasswordViewModel::class.java)
        viewModel.setCallback(this)

        iv_back.setOnClickListener {
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
            finish()
        }

        tvBtn_updatePassword.setOnClickListener {
            if (et_old_pass.text.toString().isEmpty()) {
                et_old_pass.error = "Please Enter Old Password"
                et_old_pass.requestFocus()
                //contentLayout.snackbar("Please Enter Mobile Number")
                return@setOnClickListener
            }
            if (et_pass.text.toString().isEmpty()) {
                et_pass.error = "Please Enter New Password"
                et_pass.requestFocus()
                //contentLayout.snackbar("Please Enter Valid Mobile Number")
                return@setOnClickListener
            }
            if (et_re_pass.text.toString().isEmpty()) {
                et_re_pass.error = "Please Re-type Password"
                et_re_pass.requestFocus()
                //contentLayout.snackbar("Please Enter Password")
                return@setOnClickListener
            }
            if (et_re_pass.text.toString() != et_pass.text.toString()) {
                et_re_pass.error = "Password does't match"
                et_re_pass.requestFocus()
                //contentLayout.snackbar("Please Enter Password")
                return@setOnClickListener
            }
            if (network!!) {
                loadingDialog.showDialog()
                viewModel.resetPassword(sessionManager.getId!!, et_old_pass.text.toString(), et_pass.text.toString())
            } else {
                mainContent.snackbar(getString(R.string.error_message))
            }
        }
    }

    override fun onResetPasswordSuccess(data: ArrayList<DataItemResetPass>) {
        loadingDialog.hideDialog()
        if (data.isEmpty()) {
            mainContent.snackbar("Old password not match")
        } else {
            toast("Password updated successfully.......")
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
            finish()
        }
    }

    override fun onResetPasswordError(message: String) {
        loadingDialog.hideDialog()
        mainContent.snackbar("Unable to Reset your Password")
    }

    override fun onNetworkError(message: String) {
        loadingDialog.hideDialog()
        mainContent.snackbar(message)
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }
}
