package com.app.kgs_user.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.app.kgs_user.R
import com.app.kgs_user.utils.LoadingDialog
import com.app.kgs_user.utils.SessionManager
import kotlinx.android.synthetic.main.activity_account_details.*

class AccountDetailsActivity : BaseActivity() {

    lateinit var loadingDialog: LoadingDialog
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)
        loadingDialog = LoadingDialog(this)
        sessionManager = SessionManager(this)
        initViewModel()
        initFields()
    }

    private fun initViewModel() {
    }

    override fun initFields() {

        tv_userMobileNo!!.text = "+91-" + sessionManager.mobile
        setupOnClick()
    }

    override fun setupOnClick() {
        iv_accountDetails_back!!.setOnClickListener { v -> super.onBackPressed() }

        layout_gotoCompleteSetup!!.setOnClickListener { gotoUpdateProfile() }
        layout_contactus!!.setOnClickListener { gotoContactUs() }
        layout_logout!!.setOnClickListener { showLogoutPopup() }
        accountDetails_about!!.setOnClickListener { gotoAboutPage() }
        orders_layout!!.setOnClickListener { gotoOrderPage() }
        wallet_layout!!.setOnClickListener { gotoWallet() }
    }

    private fun gotoWallet() {
        val intent = Intent(this, WalletActivity::class.java)
        startActivity(intent)
    }


    private fun gotoOrderPage() {
        val intent = Intent(this, MyOrdersActivity::class.java)
        startActivity(intent)
    }

    private fun gotoAboutPage() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun gotoUpdateProfile() {
        val intent = Intent(this, UpdateProfileActivity::class.java)
        startActivity(intent)
    }

    private fun gotoContactUs() {
        val intent = Intent(this, ContactActivity::class.java)
        startActivity(intent)
    }

    private fun showLogoutPopup() {
        MaterialDialog.Builder(this)
            .title(resources.getString(R.string.dialogTitle_logout))
            .content(resources.getString(R.string.dialogMessage_logout))
            .positiveText(resources.getString(R.string.dialogPositiveButtonText_logout))
            .positiveColor(ContextCompat.getColor(this, R.color.button_and_vespac_red_color))
            .negativeText(resources.getString(R.string.dialogPositiveButtonText_cancel))
            .negativeColor(ContextCompat.getColor(this, R.color.colorTranslucentButton))
            .onPositive { dialog, which ->
                sessionManager.destroyLoginSession()
                goToLoginActivity()
            }
            .onNegative({ dialog, which -> dialog.dismiss() }).show()
    }

    private fun goToLoginActivity() {
        val intent = Intent(this@AccountDetailsActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finishAffinity()
    }
}
