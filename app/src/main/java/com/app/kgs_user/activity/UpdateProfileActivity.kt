package com.app.kgs_user.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.app.kgs_user.R
import com.app.kgs_user.model.UpdateItem
import com.app.kgs_user.viewmodel.UpdateProfileViewModel
import com.app.kgs_user.callBacks.CallbackUpdateProfile
import com.app.kgs_user.utils.*
import kotlinx.android.synthetic.main.activity_update_profile.*

class UpdateProfileActivity : AppCompatActivity(), CallbackUpdateProfile {

    lateinit var sessionManager: SessionManager
    lateinit var loadingDialog: LoadingDialog
    lateinit var updateProfileViewModel: UpdateProfileViewModel
    internal var network: Boolean? = false
    lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        sessionManager = SessionManager(this)
        loadingDialog = LoadingDialog(this)

        et_input_name!!.setText(sessionManager.name)
        et_input_name!!.setSelection(sessionManager.name!!.length)
        et_input_email!!.setText(sessionManager.email)
        et_input_mobile!!.setText(sessionManager.mobile)

        networkChangeReceiver = NetworkChangeReceiver(this)
        network = networkChangeReceiver.isNetworkAvailable

        updateProfileViewModel = ViewModelProviders.of(this).get(UpdateProfileViewModel::class.java)
        updateProfileViewModel.setCallback(this)

        iv_updateProfile_back.setOnClickListener {
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
            finish()
        }

        tv_ResetPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        tvBtn_updateProfile!!.setOnClickListener { view ->
            if (checkValidation()) {
                if (network!!) {
                    updateProfileViewModel.updateProfile(
                        sessionManager.getId!!, et_input_name!!.text!!.toString(),
                        et_input_email!!.text!!.toString(), et_input_mobile.text.toString()
                    )
                } else {
                    mainContent.snackbar(getString(R.string.error_message))
                }
            }
        }
    }

    private fun checkValidation(): Boolean {
        if (et_input_name!!.text!!.toString().isEmpty() || et_input_name!!.text!!.isEmpty()) {
            et_input_name!!.requestFocus()
            et_input_name!!.error = "Please enter name"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_input_email!!.text!!.toString()).matches()) {
            et_input_email!!.requestFocus()
            et_input_email!!.error = "Please enter valid email"
            return false
        }
        return true
    }

    override fun onSuccess(data: ArrayList<UpdateItem>) {
        loadingDialog.hideDialog()
        if (data.isEmpty()) {
            mainContent.snackbar("Unable to update your profile")
        } else {
            sessionManager.setLogin(true, sessionManager.getId!!, data[0].name!!, data[0].email!!, data[0].contact!!)
            toast("Profile updated successfully.......")
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
            finish()
        }
    }

    override fun onError(message: String) {
        loadingDialog.hideDialog()
        mainContent.snackbar(message)

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
